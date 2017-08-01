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

/**
 * @author lvsq
 */
public class GossipMember implements Serializable {
    private String cluster;
    private String ipAddress;
    private Integer port;
    private String id;
    private GossipState state;

    public GossipMember() {
    }

    public GossipMember(String cluster, String ipAddress, Integer port, String id, GossipState state) {
        this.cluster = cluster;
        this.ipAddress = ipAddress;
        this.port = port;
        this.id = id;
        this.state = state;
    }

    public GossipState getState() {
        return state;
    }

    public void setState(GossipState state) {
        this.state = state;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getId() {
        if (id == null) {
            setId(ipAndPort());
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GossipMember{" +
                "cluster='" + cluster + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", id='" + id + '\'' +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GossipMember member = (GossipMember) o;

        if (!cluster.equals(member.cluster)) return false;
        if (!ipAddress.equals(member.ipAddress)) return false;
        return port.equals(member.port);
    }

    @Override
    public int hashCode() {
        int result = cluster.hashCode();
        result = 31 * result + ipAddress.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }

    public String ipAndPort() {
        return ipAddress.concat(":").concat(String.valueOf(port));
    }
}
