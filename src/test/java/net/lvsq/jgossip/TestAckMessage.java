package net.lvsq.jgossip;

import io.vertx.core.json.JsonObject;
import net.lvsq.jgossip.model.AckMessage;
import net.lvsq.jgossip.model.GossipDigest;
import net.lvsq.jgossip.model.GossipMember;
import net.lvsq.jgossip.model.GossipState;
import net.lvsq.jgossip.model.HeartbeatState;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestAckMessage {
    AckMessage ackMessage = new AckMessage();


    public void init() {
        List<GossipDigest> olders = new ArrayList<>();
        Map<GossipMember, HeartbeatState> newers = new HashMap<>();

        GossipDigest digest = new GossipDigest();
        digest.setEndpoint(new InetSocketAddress(123));
        digest.setHeartbeatTime(123445);
        digest.setVersion(1);
        olders.add(digest);

        GossipMember member = new GossipMember();
        member.setCluster("cluster");
        member.setPort(33);
        member.setIpAddress("ip");
        member.setId("id");
        member.setState(GossipState.DOWN);

        HeartbeatState state = new HeartbeatState();
        state.setHeartbeatTime(123);
        state.setVersion(1);

        newers.put(member, state);

        ackMessage.setOlders(olders);
        ackMessage.setNewers(newers);
    }

    @Test
    public void encode() {
        init();
        String j = JsonObject.mapFrom(ackMessage).encode();
        System.out.println(j);
    }

    @Test
    public void decode() {
        init();
        String j = JsonObject.mapFrom(ackMessage).encode();
        AckMessage member1 = (new JsonObject(j)).mapTo(AckMessage.class);
        System.out.println(member1.getNewers());
    }
}
