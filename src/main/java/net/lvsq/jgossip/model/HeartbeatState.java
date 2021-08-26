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


import net.lvsq.jgossip.core.VersionHelper;

/**
 * @author lvsq
 */
public class HeartbeatState {
    private long heartbeatTime;
    private long version;

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

    public HeartbeatState() {
        this.heartbeatTime = System.currentTimeMillis();
//        this.version = VersionHelper.getInstance().nextVersion();
    }

    public long updateVersion() {
        setHeartbeatTime(System.currentTimeMillis());
        this.version = VersionHelper.getInstance().nextVersion();
        return version;
    }

    @Override
    public String toString() {
        return "HeartbeatState{" +
                "heartbeatTime=" + heartbeatTime +
                ", version=" + version +
                '}';
    }
}
