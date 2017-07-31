package net.lvsq.jgossip.handler;

import io.vertx.core.json.JsonObject;
import net.lvsq.jgossip.core.GossipManager;
import net.lvsq.jgossip.model.Ack2Message;
import net.lvsq.jgossip.model.GossipMember;
import net.lvsq.jgossip.model.HeartbeatState;

import java.util.Map;

public class Ack2MessageHandler implements MessageHandler {
    @Override
    public void handle(String cluster, String data, String from) {
        JsonObject dj = new JsonObject(data);
        Ack2Message ack2Message = dj.mapTo(Ack2Message.class);

        Map<GossipMember, HeartbeatState> deltaEndpoints = ack2Message.getEndpoints();
        GossipManager.getInstance().apply2LocalState(deltaEndpoints);
    }
}
