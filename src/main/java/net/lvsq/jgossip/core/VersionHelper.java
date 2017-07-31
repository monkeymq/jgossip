package net.lvsq.jgossip.core;

import java.util.concurrent.atomic.AtomicLong;

public class VersionHelper {
    private static AtomicLong v = new AtomicLong(0);
    private static VersionHelper ourInstance = new VersionHelper();

    public static VersionHelper getInstance() {
        return ourInstance;
    }

    private VersionHelper() {
    }

    public long nextVersion() {
        return v.incrementAndGet();
    }
}
