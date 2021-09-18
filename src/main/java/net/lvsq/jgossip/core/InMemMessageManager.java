package net.lvsq.jgossip.core;

import net.lvsq.jgossip.model.RegularMessage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author silv
 */
public class InMemMessageManager implements MessageManager {
    private static final ConcurrentHashMap<String, RegularMessage> RegMessages = new ConcurrentHashMap<>();

    @Override
    public void add(RegularMessage msg) {
        RegMessages.putIfAbsent(msg.getId(), msg);
    }

    @Override
    public RegularMessage acquire(String id) {
        return RegMessages.get(id);
    }

    @Override
    public RegularMessage remove(String id) {
        return RegMessages.remove(id);
    }

    @Override
    public boolean contains(String id) {
        return RegMessages.containsKey(id);
    }

    @Override
    public boolean isEmpty() {
        return RegMessages.isEmpty();
    }

    @Override
    public Set<String> list() {
        return RegMessages.keySet();
    }
}
