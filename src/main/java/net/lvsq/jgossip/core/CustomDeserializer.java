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

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lvsq.jgossip.model.GossipMember;

import java.io.IOException;

/**
 * @author lvsq
 */
public class CustomDeserializer extends KeyDeserializer {
    ObjectMapper mapper = new ObjectMapper();

    public CustomDeserializer() {
    }

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return mapper.readValue(key, GossipMember.class);
    }
}
