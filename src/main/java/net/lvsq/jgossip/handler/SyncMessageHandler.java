package net.lvsq.jgossip.handler;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import net.lvsq.jgossip.core.GossipManager;
import net.lvsq.jgossip.core.Serializer;
import net.lvsq.jgossip.model.AckMessage;
import net.lvsq.jgossip.model.GossipDigest;
import net.lvsq.jgossip.model.GossipMember;
import net.lvsq.jgossip.model.HeartbeatState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncMessageHandler implements MessageHandler {
    @Override
    public void handle(String cluster, String data, String from) {
        if (data != null) {
            JsonArray array = new JsonArray(data);
            List<GossipDigest> olders = new ArrayList<>();
            Map<GossipMember, HeartbeatState> newers = new HashMap<>();
            for (Object e : array) {
                GossipDigest g = Serializer.getInstance().decode(Buffer.buffer().appendString(e.toString()), GossipDigest.class);
                compareDigest(g, cluster, olders, newers);
            }
            AckMessage ackMessage = new AckMessage(olders, newers);
            Buffer ackBuffer = GossipManager.getInstance().encodeAckMessage(ackMessage);
            if (from != null) {
                String[] host = from.split(":");
                GossipManager.getInstance().getSettings().getMsgService().sendMsg(host[0], Integer.valueOf(host[1]), ackBuffer);
            }
        }
    }

    private void compareDigest(GossipDigest g, String cluster, List<GossipDigest> olders, Map<GossipMember, HeartbeatState> newers) {
        GossipMember member = new GossipMember();
        member.setCluster(cluster);
        member.setIpAddress(g.getEndpoint().getAddress().getHostAddress());
        member.setPort(g.getEndpoint().getPort());
        HeartbeatState hb = GossipManager.getInstance().getEndpointMembers().get(member);
        long remoteHeartbeatTime = g.getHeartbeatTime();
        long remoteVersion = g.getVersion();
        if (hb != null) {
            long localHeartbeatTime = hb.getHeartbeatTime();
            long localVersion = hb.getVersion();

            if (remoteHeartbeatTime > localHeartbeatTime) {
                olders.add(g);
            } else if (remoteHeartbeatTime < localHeartbeatTime) {
                newers.put(member, hb);
            } else if (remoteHeartbeatTime == localHeartbeatTime) {
                if (remoteVersion > localVersion) {
                    olders.add(g);
                } else if (remoteVersion < localVersion) {
                    newers.put(member, hb);
                }
            }
        } else {
            olders.add(g);
        }
    }
}
