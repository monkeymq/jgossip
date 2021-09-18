package net.lvsq.jgossip.handler;

import io.vertx.core.json.JsonObject;
import net.lvsq.jgossip.core.GossipManager;
import net.lvsq.jgossip.core.MessageManager;
import net.lvsq.jgossip.model.GossipState;
import net.lvsq.jgossip.model.RegularMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author silv
 */
public class RegularMessageHandler implements MessageHandler {
    private static final ConcurrentHashMap<String, String> RECEIVED = new ConcurrentHashMap<>();

    @Override
    public void handle(String cluster, String data, String from) {
        JsonObject dj = new JsonObject(data);
        RegularMessage msg = dj.mapTo(RegularMessage.class);
        MessageManager mm = GossipManager.getInstance().getSettings().getMessageManager();
        String creatorId = msg.getCreator().getId();
        if (!RECEIVED.containsKey(creatorId)) {
            RECEIVED.put(creatorId, msg.getId());
        } else {
            String rcvedId = RECEIVED.get(creatorId);
            int c = msg.getId().compareTo(rcvedId);
            if (c <= 0) {
                return;
            } else {
                mm.remove(rcvedId);
                RECEIVED.put(creatorId, msg.getId());
            }
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Received a message from : [" + from + "], message : [" + msg + "]");
        }
        if (!mm.contains(msg.getId())) {
            msg.setForwardCount(0);
            mm.add(msg);
            GossipManager.getInstance().fireGossipEvent(msg.getCreator(), GossipState.RCV, msg.getPayload());
        }
    }
}
