package net.lvsq.jgossip;

import io.vertx.core.json.JsonObject;
import net.lvsq.jgossip.core.GossipMessageFactory;
import net.lvsq.jgossip.model.GossipMember;
import net.lvsq.jgossip.model.GossipState;
import net.lvsq.jgossip.model.MessageType;
import net.lvsq.jgossip.model.RegularMessage;
import org.junit.Before;
import org.junit.Test;

/**
 * @author silv
 * @date 2021/9/9
 */
public class TestRegularMessage {
    GossipMember member = new GossipMember();
    RegularMessage regularMessage;

    @Before
    public void init() {
        member.setId("id");
        member.setState(GossipState.DOWN);
        member.setCluster("cluster");
        member.setIpAddress("ip");
        member.setPort(111);

        regularMessage = new RegularMessage(member, "payload");
    }

    @Test
    public void encode() {
        JsonObject msg = JsonObject.mapFrom(regularMessage);
        String m = GossipMessageFactory.getInstance().makeMessage(MessageType.REG_MESSAGE, msg.encode(), "cluster", "ip").encode();

        JsonObject dj = new JsonObject(m);
        JsonObject data = new JsonObject(dj.getString("data"));
        data.mapTo(RegularMessage.class);

    }
}
