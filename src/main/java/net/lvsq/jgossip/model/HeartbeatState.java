package net.lvsq.jgossip.model;


import net.lvsq.jgossip.core.VersionHelper;

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
        this.version = VersionHelper.getInstance().nextVersion();
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
