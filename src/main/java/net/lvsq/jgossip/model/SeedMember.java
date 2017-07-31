package net.lvsq.jgossip.model;


import java.io.Serializable;

public class SeedMember implements Serializable {
    private String cluster;
    private String ipAddress;
    private Integer port;
    private String id;

    public SeedMember(String cluster, String ipAddress, Integer port, String id) {
        this.cluster = cluster;
        this.ipAddress = ipAddress;
        this.port = port;
        this.id = id;
    }

    public SeedMember() {

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
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedMember that = (SeedMember) o;

        if (!cluster.equals(that.cluster)) return false;
        if (!ipAddress.equals(that.ipAddress)) return false;
        return port.equals(that.port);
    }

    @Override
    public int hashCode() {
        int result = cluster.hashCode();
        result = 31 * result + ipAddress.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SeedMember{" +
                "cluster='" + cluster + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", id='" + id + '\'' +
                '}';
    }
}
