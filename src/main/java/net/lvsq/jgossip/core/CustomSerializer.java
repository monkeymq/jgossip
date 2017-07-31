package net.lvsq.jgossip.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.lvsq.jgossip.model.GossipMember;

import java.io.IOException;

public class CustomSerializer extends JsonSerializer<GossipMember> {

    @Override
    public void serialize(GossipMember value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        gen.writeFieldName(mapper.writeValueAsString(value));
    }
}