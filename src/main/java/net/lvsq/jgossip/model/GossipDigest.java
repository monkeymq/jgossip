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

package net.lvsq.jgossip.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author lvsq
 */
public class GossipDigest implements Serializable, Comparable<GossipDigest> {
    private InetSocketAddress endpoint;
    private long heartbeatTime;
    private long version;
    private String id;

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
        this.id = endpoint.getId();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GossipDigest{" +
                "endpoint=" + endpoint.toString() +
                ", heartbeatTime=" + heartbeatTime +
                ", version=" + version +
                '}';
    }
}
