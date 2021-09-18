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

import io.vertx.core.json.JsonObject;
import net.lvsq.jgossip.model.MessageType;

/**
 * @author lvsq
 */
public class GossipMessageFactory {
    private static final GossipMessageFactory ourInstance = new GossipMessageFactory();
    public static final String KEY_MSG_TYPE = "msgtype";
    public static final String KEY_DATA = "data";
    public static final String KEY_CLUSTER = "cluster";
    public static final String KEY_FROM = "from";

    public static GossipMessageFactory getInstance() {
        return ourInstance;
    }

    private GossipMessageFactory() {
    }

    public JsonObject makeMessage(MessageType type, String data, String cluster, String from) {
        JsonObject bj = new JsonObject();
        bj.put(KEY_MSG_TYPE, type);
        bj.put(KEY_CLUSTER, cluster);
        bj.put(KEY_DATA, data);
        bj.put(KEY_FROM, from);
        return bj;
    }
}
