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

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.lvsq.jgossip.event.GossipListener;
import net.lvsq.jgossip.model.Ack2Message;
import net.lvsq.jgossip.model.AckMessage;
import net.lvsq.jgossip.model.CandidateMemberState;
import net.lvsq.jgossip.model.GossipDigest;
import net.lvsq.jgossip.model.GossipMember;
import net.lvsq.jgossip.model.GossipState;
import net.lvsq.jgossip.model.HeartbeatState;
import net.lvsq.jgossip.model.MessageType;
import net.lvsq.jgossip.model.RegularMessage;
import net.lvsq.jgossip.model.SeedMember;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author lvsq
 */
public class GossipManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GossipManager.class);
    private static final GossipManager instance = new GossipManager();
    private boolean isWorking = false;
    private Boolean isSeedNode = null;
    private final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService doGossipExecutor = Executors.newScheduledThreadPool(1);
//    private ScheduledExecutorService clearExecutor = Executors.newSingleThreadScheduledExecutor();

    private final Map<GossipMember, HeartbeatState> endpointMembers = new ConcurrentHashMap<>();
    private final List<GossipMember> liveMembers = new ArrayList<>();
    private final List<GossipMember> deadMembers = new ArrayList<>();
    private final Map<GossipMember, CandidateMemberState> candidateMembers = new ConcurrentHashMap<>();
    private GossipSettings settings;
    private GossipMember localGossipMember;
    private String cluster;
    private GossipListener listener;
    private final Random random = new Random();

    private GossipManager() {
    }

    public static GossipManager getInstance() {
        return instance;
    }

    public void init(String cluster, String ipAddress, Integer port, String id, List<SeedMember> seedMembers, GossipSettings settings, GossipListener listener) {
        this.cluster = cluster;
        this.localGossipMember = new GossipMember();
        this.localGossipMember.setCluster(cluster);
        this.localGossipMember.setIpAddress(ipAddress);
        this.localGossipMember.setPort(port);
        this.localGossipMember.setId(id);
        this.localGossipMember.setState(GossipState.JOIN);
        this.endpointMembers.put(localGossipMember, new HeartbeatState());
        this.listener = listener;
        this.settings = settings;
        this.settings.setSeedMembers(seedMembers);
        fireGossipEvent(localGossipMember, GossipState.JOIN);
    }

    protected void start() {
        LOGGER.info(String.format("Starting jgossip! cluster[%s] ip[%s] port[%d] id[%s]", localGossipMember.getCluster(), localGossipMember.getIpAddress(), localGossipMember.getPort(), localGossipMember.getId()
        ));
        isWorking = true;
        settings.getMsgService().listen(getSelf().getIpAddress(), getSelf().getPort());
        doGossipExecutor.scheduleAtFixedRate(new GossipTask(), settings.getGossipInterval(), settings.getGossipInterval(), TimeUnit.MILLISECONDS);
    }

    public List<GossipMember> getLiveMembers() {
        return liveMembers;
    }

    public List<GossipMember> getDeadMembers() {
        return deadMembers;
    }

    public GossipSettings getSettings() {
        return settings;
    }

    public GossipMember getSelf() {
        return localGossipMember;
    }

    public String getID() {
        return getSelf().getId();
    }

    public boolean isWorking() {
        return isWorking;
    }

    public Map<GossipMember, HeartbeatState> getEndpointMembers() {
        return endpointMembers;
    }

    public String getCluster() {
        return cluster;
    }

    private void randomGossipDigest(List<GossipDigest> digests) throws UnknownHostException {
        List<GossipMember> endpoints = new ArrayList<>(endpointMembers.keySet());
        Collections.shuffle(endpoints, random);
        for (GossipMember ep : endpoints) {
            HeartbeatState hb = endpointMembers.get(ep);
            long hbTime = 0;
            long hbVersion = 0;
            if (hb != null) {
                hbTime = hb.getHeartbeatTime();
                hbVersion = hb.getVersion();
            }
            digests.add(new GossipDigest(ep, hbTime, hbVersion));
        }
    }

    class GossipTask implements Runnable {

        @Override
        public void run() {
            //Update local member version
            long version = endpointMembers.get(getSelf()).updateVersion();
            if (isDiscoverable(getSelf())) {
                up(getSelf());
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("sync data");
                LOGGER.trace(String.format("Now my heartbeat version is %d", version));
            }

            List<GossipDigest> digests = new ArrayList<>();
            try {
                randomGossipDigest(digests);
                if (digests.size() > 0) {
                    Buffer syncMessageBuffer = encodeSyncMessage(digests);
                    sendBuf(syncMessageBuffer);
                }
                checkStatus();
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("live member : " + getLiveMembers());
                    LOGGER.trace("dead member : " + getDeadMembers());
                    LOGGER.trace("endpoint : " + getEndpointMembers());
                }
                new Thread(() -> {
                    MessageManager mm = settings.getMessageManager();
                    if (!mm.isEmpty()) {
                        for (String id : mm.list()) {
                            RegularMessage msg = mm.acquire(id);
                            int c = msg.getForwardCount();
                            int maxTry = convergenceCount();
//                            if (isSeedNode()) {
//                                maxTry = convergenceCount();
//                            }
                            if (c < maxTry) {
                                sendBuf(encodeRegularMessage(msg));
                                msg.setForwardCount(c + 1);
                            }
                            if ((System.currentTimeMillis() - msg.getCreateTime()) >= msg.getTtl()) {
                                mm.remove(id);
                            }
                        }
                    }
                }).start();
            } catch (UnknownHostException e) {
                LOGGER.error(e.getMessage());
            }

        }

        private void sendBuf(Buffer buf) {
            //step 1. goosip to some random live members
            boolean b = gossip2LiveMember(buf);

            //step 2. goosip to a random dead memeber
            gossip2UndiscoverableMember(buf);

            //step3.
            if (!b || liveMembers.size() <= settings.getSeedMembers().size()) {
                gossip2Seed(buf);
            }
        }
    }

    private Buffer encodeSyncMessage(List<GossipDigest> digests) {
        Buffer buffer = Buffer.buffer();
        JsonArray array = new JsonArray();
        for (GossipDigest e : digests) {
            array.add(Serializer.getInstance().encode(e).toString());
        }
        buffer.appendString(GossipMessageFactory.getInstance().makeMessage(MessageType.SYNC_MESSAGE, array.encode(), getCluster(), getSelf().ipAndPort()).encode());
        return buffer;
    }

    public Buffer encodeAckMessage(AckMessage ackMessage) {
        Buffer buffer = Buffer.buffer();
        JsonObject ackJson = JsonObject.mapFrom(ackMessage);
        buffer.appendString(GossipMessageFactory.getInstance().makeMessage(MessageType.ACK_MESSAGE, ackJson.encode(), getCluster(), getSelf().ipAndPort()).encode());
        return buffer;
    }

    public Buffer encodeAck2Message(Ack2Message ack2Message) {
        Buffer buffer = Buffer.buffer();
        JsonObject ack2Json = JsonObject.mapFrom(ack2Message);
        buffer.appendString(GossipMessageFactory.getInstance().makeMessage(MessageType.ACK2_MESSAGE, ack2Json.encode(), getCluster(), getSelf().ipAndPort()).encode());
        return buffer;
    }

    private Buffer encodeShutdownMessage() {
        Buffer buffer = Buffer.buffer();
        JsonObject self = JsonObject.mapFrom(getSelf());
        buffer.appendString(GossipMessageFactory.getInstance().makeMessage(MessageType.SHUTDOWN, self.encode(), getCluster(), getSelf().ipAndPort()).encode());
        return buffer;
    }

    private Buffer encodeRegularMessage(RegularMessage regularMessage) {
        Buffer buffer = Buffer.buffer();
        JsonObject msg = JsonObject.mapFrom(regularMessage);
        buffer.appendString(GossipMessageFactory.getInstance().makeMessage(MessageType.REG_MESSAGE, msg.encode(), getCluster(), getSelf().ipAndPort()).encode());
        return buffer;
    }

    public void apply2LocalState(Map<GossipMember, HeartbeatState> endpointMembers) {
        Set<GossipMember> keys = endpointMembers.keySet();
        for (GossipMember m : keys) {
            if (getSelf().equals(m)) {
                continue;
            }

            try {
                HeartbeatState localState = getEndpointMembers().get(m);
                HeartbeatState remoteState = endpointMembers.get(m);

                if (localState != null) {
                    long localHeartbeatTime = localState.getHeartbeatTime();
                    long remoteHeartbeatTime = remoteState.getHeartbeatTime();
                    if (remoteHeartbeatTime > localHeartbeatTime) {
                        remoteStateReplaceLocalState(m, remoteState);
                    } else if (remoteHeartbeatTime == localHeartbeatTime) {
                        long localVersion = localState.getVersion();
                        long remoteVersion = remoteState.getVersion();
                        if (remoteVersion > localVersion) {
                            remoteStateReplaceLocalState(m, remoteState);
                        }
                    }
                } else {
                    remoteStateReplaceLocalState(m, remoteState);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void remoteStateReplaceLocalState(GossipMember member, HeartbeatState remoteState) {
        if (member.getState() == GossipState.UP) {
            up(member);
        }
        if (member.getState() == GossipState.DOWN) {
            down(member);
        }
        if (endpointMembers.containsKey(member)) {
            endpointMembers.remove(member);
        }
        endpointMembers.put(member, remoteState);
    }

    public GossipMember createByDigest(GossipDigest digest) {
        GossipMember member = new GossipMember();
        member.setPort(digest.getEndpoint().getPort());
        member.setIpAddress(digest.getEndpoint().getAddress().getHostAddress());
        member.setCluster(cluster);

        Set<GossipMember> keys = getEndpointMembers().keySet();
        for (GossipMember m : keys) {
            if (m.equals(member)) {
                member.setId(m.getId());
                member.setState(m.getState());
                break;
            }
        }

        return member;
    }

    /**
     * send sync message to some live members
     *
     * @param buffer sync data
     * @return if send to a seed member then return TURE
     */
    private boolean gossip2LiveMember(Buffer buffer) {
        int liveSize = liveMembers.size();
        if (liveSize <= 0) {
            return false;
        }
        boolean b = false;
        int c = Math.min(liveSize, convergenceCount());
        for (int i = 0; i < c; i++) {
            int index = random.nextInt(liveSize);
            b = b || sendGossip(buffer, liveMembers, index);
        }
        return b;
    }

    /**
     * send sync message to a dead member
     *
     * @param buffer sync data
     */
    private void gossip2UndiscoverableMember(Buffer buffer) {
        int deadSize = deadMembers.size();
        if (deadSize <= 0) {
            return;
        }
        int index = (deadSize == 1) ? 0 : random.nextInt(deadSize);
        sendGossip(buffer, deadMembers, index);
    }

    private void gossip2Seed(Buffer buffer) {
        int size = settings.getSeedMembers().size();
        if (size > 0) {
            if (size == 1 && isSeedNode()) {
                return;
            }
            int index = (size == 1) ? 0 : random.nextInt(size);
            if (liveMembers.size() == 1) {
                sendGossip2Seed(buffer, settings.getSeedMembers(), index);
            } else {
                double prob = size / (double) liveMembers.size();
                if (random.nextDouble() < prob) {
                    sendGossip2Seed(buffer, settings.getSeedMembers(), index);
                }
            }
        }
    }

    private boolean sendGossip(Buffer buffer, List<GossipMember> members, int index) {
        if (buffer != null && index >= 0) {
            try {
                GossipMember target = members.get(index);
                if (target.equals(getSelf())) {
                    int m_size = members.size();
                    if (m_size == 1) {
                        return false;
                    } else {
                        target = members.get((index + 1) % m_size);
                    }
                }
                settings.getMsgService().sendMsg(target.getIpAddress(), target.getPort(), buffer);
                return settings.getSeedMembers().contains(gossipMember2SeedMember(target));
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return false;
    }

    private boolean sendGossip2Seed(Buffer buffer, List<SeedMember> members, int index) {
        if (buffer != null && index >= 0) {
            try {
                SeedMember target = members.get(index);
                int m_size = members.size();
                if (target.equals(gossipMember2SeedMember(getSelf()))) {
                    if (m_size <= 1) {
                        return false;
                    } else {
                        target = members.get((index + 1) % m_size);
                    }
                }
                settings.getMsgService().sendMsg(target.getIpAddress(), target.getPort(), buffer);
                return true;
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return false;
    }

    private SeedMember gossipMember2SeedMember(GossipMember member) {
        return new SeedMember(member.getCluster(), member.getIpAddress(), member.getPort(), member.getId());
    }

    private void checkStatus() {
        try {
            GossipMember local = getSelf();
            Map<GossipMember, HeartbeatState> endpoints = getEndpointMembers();
            Set<GossipMember> epKeys = endpoints.keySet();
            for (GossipMember k : epKeys) {
                if (!k.equals(local)) {
                    HeartbeatState state = endpoints.get(k);
                    long now = System.currentTimeMillis();
                    long duration = now - state.getHeartbeatTime();
                    long convictedTime = convictedTime();
                    LOGGER.info("check : " + k + " state : " + state + " duration : " + duration + " convictedTime : " + convictedTime);
                    if (duration > convictedTime && (isAlive(k) || getLiveMembers().contains(k))) {
                        downing(k, state);
                    }
                    if (duration <= convictedTime && (isDiscoverable(k) || getDeadMembers().contains(k))) {
                        up(k);
                    }
                }
            }
            checkCandidate();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private int convergenceCount() {
        int size = getEndpointMembers().size();
        return (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
    }

    private long convictedTime() {
        long executeGossipTime = 500;
        return ((convergenceCount() * (settings.getNetworkDelay() * 3L + executeGossipTime)) << 1) + settings.getGossipInterval();
    }

    private boolean isDiscoverable(GossipMember member) {
        return member.getState() == GossipState.JOIN || member.getState() == GossipState.DOWN;
    }

    private boolean isAlive(GossipMember member) {
        return member.getState() == GossipState.UP;
    }

    public boolean isSeedNode() {
        if (isSeedNode == null) {
            isSeedNode = settings.getSeedMembers().contains(gossipMember2SeedMember(getSelf()));
        }
        return isSeedNode;
    }

    public GossipListener getListener() {
        return listener;
    }

    private void fireGossipEvent(GossipMember member, GossipState state) {
        fireGossipEvent(member, state, null);
    }

    public void fireGossipEvent(GossipMember member, GossipState state, Object payload) {
        if (getListener() != null) {
            if (state == GossipState.RCV) {
                new Thread(() -> getListener().gossipEvent(member, state, payload)).start();
            } else {
                getListener().gossipEvent(member, state, payload);
            }
        }
    }

//    private void clearMember(GossipMember member) {
//        rwlock.writeLock().lock();
//        try {
//            endpointMembers.remove(member);
//        } finally {
//            rwlock.writeLock().unlock();
//        }
//    }

    public void down(GossipMember member) {
        LOGGER.info("down ~~");
        try {
            rwlock.writeLock().lock();
            member.setState(GossipState.DOWN);
            liveMembers.remove(member);
            if (!deadMembers.contains(member)) {
                deadMembers.add(member);
            }
//            clearExecutor.schedule(() -> clearMember(member), getSettings().getDeleteThreshold() * getSettings().getGossipInterval(), TimeUnit.MILLISECONDS);
            fireGossipEvent(member, GossipState.DOWN);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    private void up(GossipMember member) {
        try {
            rwlock.writeLock().lock();
            member.setState(GossipState.UP);
            if (!liveMembers.contains(member)) {
                liveMembers.add(member);
            }
            if (candidateMembers.containsKey(member)) {
                candidateMembers.remove(member);
            }
            if (deadMembers.contains(member)) {
                deadMembers.remove(member);
                LOGGER.info("up ~~");
                if (!member.equals(getSelf())) {
                    fireGossipEvent(member, GossipState.UP);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            rwlock.writeLock().unlock();
        }

    }

    private void downing(GossipMember member, HeartbeatState state) {
        LOGGER.info("downing ~~");
        try {
            if (candidateMembers.containsKey(member)) {
                CandidateMemberState cState = candidateMembers.get(member);
                if (state.getHeartbeatTime() == cState.getHeartbeatTime()) {
                    cState.updateCount();
                } else if (state.getHeartbeatTime() > cState.getHeartbeatTime()) {
                    candidateMembers.remove(member);
                }
            } else {
                candidateMembers.put(member, new CandidateMemberState(state.getHeartbeatTime()));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void checkCandidate() {
        Set<GossipMember> keys = candidateMembers.keySet();
        for (GossipMember m : keys) {
            if (candidateMembers.get(m).getDowningCount().get() >= convergenceCount()) {
                down(m);
                candidateMembers.remove(m);
            }
        }
    }


    protected void shutdown() {
        getSettings().getMsgService().unListen();
        doGossipExecutor.shutdown();
        try {
            Thread.sleep(getSettings().getGossipInterval());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Buffer buffer = encodeShutdownMessage();
        for (int i = 0; i < getLiveMembers().size(); i++) {
            sendGossip(buffer, getLiveMembers(), i);
        }
        isWorking = false;
    }

    public void publish(Object payload) {
        RegularMessage msg = new RegularMessage(getSelf(), payload, convictedTime());
        settings.getMessageManager().add(msg);
    }

}