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
import java.util.List;

/**
 * @author lvsq
 */
public class SyncMessage implements Serializable {
    private String cluster;
    private List<GossipDigest> digestList;

    public SyncMessage() {
    }

    public SyncMessage(String cluster, List<GossipDigest> digestList) {
        this.cluster = cluster;
        this.digestList = digestList;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public List<GossipDigest> getDigestList() {
        return digestList;
    }

    public void setDigestList(List<GossipDigest> digestList) {
        this.digestList = digestList;
    }

    @Override
    public String toString() {
        return "GossipDigestSyncMessage{" +
                "cluster='" + cluster + '\'' +
                ", digestList=" + digestList +
                '}';
    }

}
