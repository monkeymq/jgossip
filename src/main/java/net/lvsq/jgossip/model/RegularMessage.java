package net.lvsq.jgossip.model;

import java.io.Serializable;

/**
 * @author silv
 */
public class RegularMessage implements Serializable {
    private static final long DEFAULT_TTL = 300000;
    private String id;
    private long ttl;
    private long createTime;
    private Object payload;
    private int forwardCount;

    private GossipMember creator;

    public RegularMessage() {
    }

    public RegularMessage(GossipMember creator, Object payload) {
        this(creator, payload, DEFAULT_TTL);
    }

    public RegularMessage(GossipMember creator, Object payload, Long ttl) {
        long now = System.currentTimeMillis();
        this.ttl = ttl == null ? DEFAULT_TTL : ttl;
        this.creator = creator;
        this.payload = payload;
        this.id = "REG_MSG_" + now;
        this.createTime = now;
        this.forwardCount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public GossipMember getCreator() {
        return creator;
    }

    public void setCreator(GossipMember creator) {
        this.creator = creator;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public int getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(int forwardCount) {
        this.forwardCount = forwardCount;
    }

    @Override
    public String toString() {
        return "RegularMessage{" +
                "id='" + id + '\'' +
                ", ttl=" + ttl +
                ", createTime=" + createTime +
                ", payload=" + payload +
                ", forwardCount=" + forwardCount +
                ", creator=" + creator +
                '}';
    }
}
