package me.chat.server.tasks.util;

/**
* User: sennen
* Date: 30/07/2014
* Time: 23:56
*/
@FunctionalInterface
public interface InterruptableRunnable {
    void run() throws InterruptedException;
}
