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

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lvsq
 */
public class VersionHelper {
    private static final AtomicLong v = new AtomicLong(0);
    private static final VersionHelper ourInstance = new VersionHelper();

    public static VersionHelper getInstance() {
        return ourInstance;
    }

    private VersionHelper() {
    }

    public long nextVersion() {
        return v.incrementAndGet();
    }
}
