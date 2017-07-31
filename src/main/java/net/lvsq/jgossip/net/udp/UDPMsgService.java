package net.lvsq.jgossip.net.udp;

import io.netty.util.internal.StringUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.json.JsonObject;
import net.lvsq.jgossip.core.GossipManager;
import net.lvsq.jgossip.core.GossipMessageFactory;
import net.lvsq.jgossip.handler.Ack2MessageHandler;
import net.lvsq.jgossip.handler.AckMessageHandler;
import net.lvsq.jgossip.handler.MessageHandler;
import net.lvsq.jgossip.handler.ShutdownMessageHandler;
import net.lvsq.jgossip.handler.SyncMessageHandler;
import net.lvsq.jgossip.model.MessageType;
import net.lvsq.jgossip.net.MsgService;

public class UDPMsgService implements MsgService {
    DatagramSocket socket;

    @Override
    public void listen(String ipAddress, int port) {
        socket = Vertx.vertx().createDatagramSocket(new DatagramSocketOptions());
        socket.listen(port, ipAddress, asyncResult -> {
            if (asyncResult.succeeded()) {
                socket.handler(packet -> handleMsg(packet.data()));
            } else {
                LOGGER.error("Listen failed " + asyncResult.cause());
            }
        });
    }

    @Override
    public void handleMsg(Buffer data) {
        JsonObject j = data.toJsonObject();
        String msgType = j.getString(GossipMessageFactory.KEY_MSG_TYPE);
        String _data = j.getString(GossipMessageFactory.KEY_DATA);
        String cluster = j.getString(GossipMessageFactory.KEY_CLUSTER);
        String from = j.getString(GossipMessageFactory.KEY_FROM);
        if (StringUtil.isNullOrEmpty(cluster) || !GossipManager.getInstance().getCluster().equals(cluster)) {
            LOGGER.error("This message shouldn't exist my world!");
            return;
        }
        MessageHandler handler = null;
        MessageType type = MessageType.valueOf(msgType);
        if (type == MessageType.SYNC_MESSAGE) {
            handler = new SyncMessageHandler();
        } else if (type == MessageType.ACK_MESSAGE) {
            handler = new AckMessageHandler();
        } else if (type == MessageType.ACK2_MESSAGE) {
            handler = new Ack2MessageHandler();
        } else if (type == MessageType.SHUTDOWN) {
            handler = new ShutdownMessageHandler();
        } else {
            LOGGER.error("Not supported message type");
        }
        if (handler != null) {
            handler.handle(cluster, _data, from);
        }
    }

    @Override
    public void sendMsg(String targetIp, Integer targetPort, Buffer data) {
        if (targetIp != null && targetPort != null && data != null) {
            socket.send(data, targetPort, targetIp, asyncResult -> {
            });
        }
    }

    @Override
    public void unListen() {
        if (socket != null) {
            socket.close(asyncResult -> {
                if (asyncResult.succeeded()) {
                    LOGGER.info("Socket was close!");
                } else {
                    LOGGER.error("Close socket an error has occurred. " + asyncResult.cause().getMessage());
                }
            });
        }
    }
}
