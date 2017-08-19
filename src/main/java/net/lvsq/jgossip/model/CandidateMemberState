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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lvsq on 8/19/2017.
 */
public class CandidateMemberState {
    private long heartbeatTime;
    private AtomicInteger downingCount;

    public CandidateMemberState(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
        this.downingCount = new AtomicInteger(0);
    }

    public void updateCount() {
        this.downingCount.incrementAndGet();
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public AtomicInteger getDowningCount() {
        return downingCount;
    }

    public void setDowningCount(AtomicInteger downingCount) {
        this.downingCount = downingCount;
    }

    @Override
    public String toString() {
        return "CandidateMemberState{" +
                "heartbeatTime=" + heartbeatTime +
                ", downingCount=" + downingCount.get() +
                '}';
    }
}
