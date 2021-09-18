package net.lvsq.jgossip;

import net.lvsq.jgossip.core.GossipService;
import net.lvsq.jgossip.core.GossipSettings;
import net.lvsq.jgossip.model.SeedMember;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestGossipService {

    @Test
    public void startGossip() throws Exception {
        String cluster = "testcluster";
        String ipAddress = "127.0.0.1";
        int port = 5000;
        List<SeedMember> seedNodes = new ArrayList<>();
        SeedMember seed = new SeedMember();
        seed.setCluster(cluster);
        seed.setIpAddress(ipAddress);
        seed.setPort(port);
        seedNodes.add(seed);

        for (int i = 0; i < 1; i++) {
            GossipService gossipService = null;
            try {
                gossipService = new GossipService(cluster, ipAddress, port + i, null, seedNodes, new GossipSettings(), (member, state, payload) -> {
                    System.out.println("member:" + member + "  state: " + state);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            gossipService.start();
        }
    }
}
