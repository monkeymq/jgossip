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

# Settings

* gossipInterval - How often (in milliseconds) to gossip list of members to other node(s). Default is 1000ms
* networkDelay - Network delay in ms. Default is 200ms
* msgService - Which message sync implementation. Default is UDPMsgService, you can extand it.

# Event Listener

Now, we have three kinds of event
```java
GossipState.UP;
GossipState.DOWN;
GossipState.JOIN;
```

