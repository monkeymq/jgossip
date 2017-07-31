package net.lvsq.jgossip.model;


public enum GossipState {
    UP("up"), DOWN("down"), JOIN("join");

    private final String state;

    GossipState(String state) {
        this.state = state;
    }

}
