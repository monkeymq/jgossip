package net.lvsq.jgossip.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.lvsq.jgossip.core.CustomDeserializer;
import net.lvsq.jgossip.core.CustomSerializer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AckMessage implements Serializable {
    private List<GossipDigest> olders;

    @JsonSerialize(keyUsing = CustomSerializer.class)
    @JsonDeserialize(keyUsing = CustomDeserializer.class)
    private Map<GossipMember, HeartbeatState> newers;

    public AckMessage() {
    }

    public AckMessage(List<GossipDigest> olders, Map<GossipMember, HeartbeatState> newers) {
        this.olders = olders;
        this.newers = newers;
    }

    public List<GossipDigest> getOlders() {
        return olders;
    }

    public void setOlders(List<GossipDigest> olders) {
        this.olders = olders;
    }

    public Map<GossipMember, HeartbeatState> getNewers() {
        return newers;
    }

    public void setNewers(Map<GossipMember, HeartbeatState> newers) {
        this.newers = newers;
    }

    @Override
    public String toString() {
        return "AckMessage{" +
                "olders=" + olders +
                ", newers=" + newers +
                '}';
    }

}
