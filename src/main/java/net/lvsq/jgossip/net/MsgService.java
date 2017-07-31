package net.lvsq.jgossip.net;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public interface MsgService {
    Logger LOGGER = LoggerFactory.getLogger(MsgService.class);

    void listen(String ipAddress, int port);

    void handleMsg(Buffer data);

    void sendMsg(String targetIp, Integer targetPort, Buffer data);

    void unListen();
}
