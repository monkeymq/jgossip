package net.lvsq.jgossip.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.lvsq.jgossip.core.CustomDeserializer;
import net.lvsq.jgossip.core.CustomSerializer;

import java.io.Serializable;
import java.util.Map;

public class Ack2Message implements Serializable {
    @JsonSerialize(keyUsing = CustomSerializer.class)
    @JsonDeserialize(keyUsing = CustomDeserializer.class)
    private Map<GossipMember, HeartbeatState> endpoints;

    public Ack2Message() {
    }

    public Ack2Message(Map<GossipMember, HeartbeatState> endpoints) {

        this.endpoints = endpoints;
    }

    @Override
    public String toString() {
        return "GossipDigestAck2Message{" +
                "endpoints=" + endpoints +
                '}';
    }

    public Map<GossipMember, HeartbeatState> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<GossipMember, HeartbeatState> endpoints) {
        this.endpoints = endpoints;
    }
}
