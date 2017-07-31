package net.lvsq.jgossip.core;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;

public class Serializer {
    private static Serializer ourInstance = new Serializer();

    public static Serializer getInstance() {
        return ourInstance;
    }

    private Serializer() {
    }

    public Buffer encode(Serializable obj) {
        Buffer buffer = Buffer.buffer();
        try {
            buffer.appendString(JsonObject.mapFrom(obj).encode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public <T> T decode(Buffer buffer, Class<T> typeReference) {
        T gdsm = null;
        if (buffer != null) {
            try {
                gdsm = buffer.toJsonObject().mapTo(typeReference);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return gdsm;
    }
}
