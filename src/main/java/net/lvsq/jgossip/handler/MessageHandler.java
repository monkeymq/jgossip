package net.lvsq.jgossip.handler;

public interface MessageHandler {
    void handle(String cluster, String data, String from);
}
