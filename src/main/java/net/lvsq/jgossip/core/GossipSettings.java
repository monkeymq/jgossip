package net.lvsq.jgossip.core;


import net.lvsq.jgossip.model.SeedMember;
import net.lvsq.jgossip.net.MsgService;
import net.lvsq.jgossip.net.udp.UDPMsgService;

import java.util.List;

public class GossipSettings {
    //Time between gossip ping in ms. Default is 1 second
    private int gossipInterval = 1000;

    //Network delay in ms. Default is 200ms
    private int networkDelay = 200;

    //Which message sync implementation. Default is UDPMsgService.class
    private MsgService msgService = new UDPMsgService();

    private List<SeedMember> seedMembers;

    public int getGossipInterval() {
        return gossipInterval;
    }

    public void setGossipInterval(int gossipInterval) {
        this.gossipInterval = gossipInterval;
    }

    public int getNetworkDelay() {
        return networkDelay;
    }

    public void setNetworkDelay(int networkDelay) {
        this.networkDelay = networkDelay;
    }

    public List<SeedMember> getSeedMembers() {
        return seedMembers;
    }

    public void setSeedMembers(List<SeedMember> seedMembers) {
        this.seedMembers = seedMembers;
    }

    public MsgService getMsgService() {
        return msgService;
    }

    public void setMsgService(MsgService msgService) {
        this.msgService = msgService;
    }
}
