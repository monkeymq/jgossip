package net.lvsq.jgossip;

import io.vertx.core.buffer.Buffer;
import net.lvsq.jgossip.core.Serializer;
import net.lvsq.jgossip.model.GossipDigest;
import net.lvsq.jgossip.model.GossipMember;
import net.lvsq.jgossip.model.GossipState;
import net.lvsq.jgossip.model.SyncMessage;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TestGossipDigestSyncMessage {
    @Test
    public void encodeAndDecode() throws UnknownHostException {
        String c = "test.cluster";
        List<GossipDigest> digestList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            GossipMember endpoint = new GossipMember();
            endpoint.setId("id" + 1);
            endpoint.setPort(i);
            endpoint.setIpAddress("127.0.0.1");
            endpoint.setCluster(c);
            endpoint.setState(GossipState.JOIN);
            long heartbeatTime = 1000 + i;
            long version = i;
            GossipDigest digest = new GossipDigest(endpoint, heartbeatTime, version);
            digestList.add(digest);
        }
        SyncMessage message = new SyncMessage(c, digestList);
        Buffer buffer = Serializer.getInstance().encode(message);
        System.out.println("encode ：" + buffer.toString());

        SyncMessage message1 = Serializer.getInstance().decode(buffer, SyncMessage.class);
        System.out.println("decode : " + message1.getDigestList());

        System.out.println(buffer.length());
    }
}
