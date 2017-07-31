package net.lvsq.jgossip.core;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lvsq.jgossip.model.GossipMember;

import java.io.IOException;

public class CustomDeserializer extends KeyDeserializer {
    ObjectMapper mapper = new ObjectMapper();

    public CustomDeserializer() {
    }

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return mapper.readValue(key, GossipMember.class);
    }
}