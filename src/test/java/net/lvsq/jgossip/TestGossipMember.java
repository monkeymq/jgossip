package net.lvsq.jgossip;

import io.vertx.core.json.JsonObject;
import net.lvsq.jgossip.model.GossipMember;
import net.lvsq.jgossip.model.GossipState;
import org.junit.Before;
import org.junit.Test;

public class TestGossipMember {

    private GossipMember member = new GossipMember();
    private String j;

    @Before
    public void init() {
        member.setId("id");
        member.setState(GossipState.DOWN);
        member.setCluster("cluster");
        member.setIpAddress("ip");
        member.setPort(111);
    }

    @Test
    public void encode() {
        j = JsonObject.mapFrom(member).encode();
        System.out.println(j);
    }

    @Test
    public void decode() {
        j = JsonObject.mapFrom(member).encode();
        GossipMember member1 = (new JsonObject(j)).mapTo(GossipMember.class);
        System.out.println(member1.getState());
    }
}
