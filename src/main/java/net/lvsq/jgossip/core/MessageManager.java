package net.lvsq.jgossip.core;

import net.lvsq.jgossip.model.RegularMessage;

import java.util.Set;

/**
 * @author silv
 */
public interface MessageManager {
    void add(RegularMessage msg);

    RegularMessage acquire(String id);

    RegularMessage remove(String id);

    boolean contains(String id);

    boolean isEmpty();

    Set<String> list();
}
