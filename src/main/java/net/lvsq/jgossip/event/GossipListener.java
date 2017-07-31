package net.lvsq.jgossip.event;


import net.lvsq.jgossip.model.GossipMember;
import net.lvsq.jgossip.model.GossipState;

public interface GossipListener {
    void gossipEvent(GossipMember member, GossipState state);
}
