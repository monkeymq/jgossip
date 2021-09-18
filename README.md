# Gossip
Gossip protocol is a method for a group of nodes to discover and check the liveliness of a cluster. More information can be found at http://en.wikipedia.org/wiki/Gossip_protocol.

# Usage
## Maven
```xml
<dependency>
  <groupId>net.lvsq</groupId>
  <artifactId>jgossip</artifactId>
  <version>1.5.0</version>
</dependency>
```


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
GossipService gossipService = new GossipService(cluster,ipAddress, port, id, seedNodes, new GossipSettings(), (member, state) -> {
        //Do anything what you want
    });
```

#### Run `GossipService`
```java
gossipService.start();
```

#### Stop
```java
gossipService.shutdown();
```

#### Get offline nodes
```java
gossipService.getGossipManager().getDeadMembers();
```

#### Get online nodes
```java
gossipService.getGossipManager().getLiveMembers();
```

# Settings
* gossipInterval - How often (in milliseconds) to gossip list of members to other node(s). Default is 1000ms
* networkDelay - Network delay in ms. Default is 200ms
* msgService - Which message sync implementation. Default is **UDPMsgService.class** use UDP protocol to send message, certainly you can extand it.
* deleteThreshold - Delete the deadth node when the sync message is not received more than [deleteThreshold] times. Default is 3

# Event Listener
Currently, **jgossip** has four events
```java
GossipState.UP;
GossipState.DOWN;
GossipState.JOIN;
GossipState.RCV;
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

    gossipService = new GossipService(cluster, myIpAddress, gossip_port, null, seedNodes, settings, (member, state, payload) -> {
        if (state == GossipState.RCV) {
            System.out.println("member:" + member + "  state: " + state + " payload: " + payload);
        }
        if (state == GossipState.DOWN) {
            System.out.println("[[[[[[[[[member:" + member + "  was down!!! ]]]]]]]]]");
        }});
} catch (Exception e) {
    e.printStackTrace();
}
gossipService.start();
        
```

Run the above code in each application to create a cluster based on the Gossip protocol. You can provide a meaningful `GossipListener` as the last parameter of `GossipService`. When state of a node changes, you can capture this change and make some responses.


# Publish Messages
If you want to send messages to gossip cluster:

```java
gossipService.getGossipManager().publish("Hello World");
```

If a node in cluster received messages, it will trigger the `GossipState.RCV` event, and handler predefined by `GossipListener` can consume these messages, such as <html><span style="color: green">"Hello World"</span><html> above.


The type of message is arbitrary, but only if it can be serialized. **jgossip** will try its best to deliver to every node. By default, messages will stay in memory for a while, and then **jgossip** will automatically delete them. So the best scenario for this feature is to send some simple messages regularly.

Please pay attention, if you need to send mass messages in a short time, which will consume a lot of resources, this requires you to weigh the actual situation.

