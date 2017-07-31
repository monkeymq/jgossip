package net.lvsq.jgossip.model;

import java.io.Serializable;
import java.util.List;

public class SyncMessage implements Serializable {
    private String cluster;
    private List<GossipDigest> digestList;

    public SyncMessage() {
    }

    public SyncMessage(String cluster, List<GossipDigest> digestList) {
        this.cluster = cluster;
        this.digestList = digestList;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public List<GossipDigest> getDigestList() {
        return digestList;
    }

    public void setDigestList(List<GossipDigest> digestList) {
        this.digestList = digestList;
    }

    @Override
    public String toString() {
        return "GossipDigestSyncMessage{" +
                "cluster='" + cluster + '\'' +
                ", digestList=" + digestList +
                '}';
    }

}
