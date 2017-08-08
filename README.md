# Gossip
Gossip protocol is a method for a group of nodes to discover and check the liveliness of a cluster. More information can be found at http://en.wikipedia.org/wiki/Gossip_protocol.

# Usage
#### First you need one or more seed members

```java
List<SeedMember> seedNodes = new ArrayLis<>();
SeedMember seed = new SeedMember();
seed.setCluster(cluster);
seed.setIpAddress(ipAddress);
seed.setPort(port);
seedNodes.add(seed);
```


#### Then, instantiation a `GossipService` object
```java
GossipService gossipService = new GossipService(cluster,ipAddress, port, id, seedNodes, new GossipSettings(), (member, state) -> {});
```

#### Run `GossipService`
```java
gossipService.start();
```

#### Stop
```java
gossipService.shutdown();
```

# Settings
* gossipInterval - How often (in milliseconds) to gossip list of members to other node(s). Default is 1000ms
* networkDelay - Network delay in ms. Default is 200ms
* msgService - Which message sync implementation. Default is **UDPMsgService.class** use UDP protocol to send message, certainly you can extand it.
* deleteThreshold - Delete the deadth node when the sync message is not received more than [deleteThreshold] times. Default is 3

# Event Listener
Now, we have three kinds of event
```java
GossipState.UP;
GossipState.DOWN;
GossipState.JOIN;
```

# Example
```java
int gossip_port = 60001;
String cluster = "gossip_cluster";

GossipSettings settings = new GossipSettings();
settings.setGossipInterval(1000);

try {
    String myIpAddress = InetAddress.getLocalHost().getHostAddress();
    List<SeedMember> seedNodes = new ArrayList<>();
    SeedMember seed = new SeedMember();
    seed.setCluster(cluster);
    seed.setIpAddress(myIpAddress);
    seed.setPort(60001);
    seedNodes.add(seed);

    gossipService = new GossipService(cluster, myIpAddress, gossip_port, null, seedNodes, settings, (member, state) ->System.out.println("member:" + member + "  state: " + state));
} catch (Exception e) {
    e.printStackTrace();
}
gossipService.start();
        
```
