// Copyright (c) 2017 The jgossip Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.lvsq.jgossip.core;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;

/**
 * @author lvsq
 */
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
