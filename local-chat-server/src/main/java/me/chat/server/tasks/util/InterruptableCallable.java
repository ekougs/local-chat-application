package me.chat.server.tasks.util;

import java.util.concurrent.ExecutionException;

/**
* User: sennen
* Date: 30/07/2014
* Time: 23:57
*/
@FunctionalInterface
public interface InterruptableCallable<T> {
    T call() throws InterruptedException, ExecutionException;
}
