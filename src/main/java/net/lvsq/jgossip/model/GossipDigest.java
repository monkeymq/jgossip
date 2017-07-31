package net.lvsq.jgossip.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class GossipDigest implements Serializable, Comparable<GossipDigest> {
    private InetSocketAddress endpoint;
    private long heartbeatTime;
    private long version;

    @Override
    public int compareTo(GossipDigest o) {
        if (heartbeatTime != o.heartbeatTime) {
            return (int) (heartbeatTime - o.heartbeatTime);
        }
        return (int) (version - o.version);
    }

    public GossipDigest() {
    }

    public GossipDigest(GossipMember endpoint, long heartbeatTime, long version) throws UnknownHostException {
        this.endpoint = new InetSocketAddress(InetAddress.getByName(endpoint.getIpAddress()), endpoint.getPort());
        this.heartbeatTime = heartbeatTime;
        this.version = version;
    }

    public InetSocketAddress getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(InetSocketAddress endpoint) {
        this.endpoint = endpoint;
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "GossipDigest{" +
                "endpoint=" + endpoint +
                ", heartbeatTime=" + heartbeatTime +
                ", version=" + version +
                '}';
    }
}
