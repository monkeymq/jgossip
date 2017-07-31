package net.lvsq.jgossip.handler;

import io.vertx.core.json.JsonObject;
import net.lvsq.jgossip.core.GossipManager;
import net.lvsq.jgossip.model.GossipMember;

/**
 * Created by silv on 7/31/2017.
 */
public class ShutdownMessageHandler implements MessageHandler {
    @Override
    public void handle(String cluster, String data, String from) {
        JsonObject dj = new JsonObject(data);
        GossipMember whoShutdown = dj.mapTo(GossipMember.class);
        if (whoShutdown != null) {
            GossipManager.getInstance().down(whoShutdown);
        }
    }
}
