package me.chat.server.server;

import me.chat.server.tasks.util.Concurrencies;

import javax.annotation.concurrent.GuardedBy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * User: sennen
 * Date: 31/07/2014
 * Time: 00:42
 */
class ResponsesToSend {
    private Lock responsesLock = new ReentrantLock();

    @GuardedBy("responsesLock")
    private final BlockingQueue<ResponseToSend> responsesToSend = new ArrayBlockingQueue<>(20);

    public void put(ResponseToSend responseToSend) {
        responsesLock.lock();
        try {
            Concurrencies.buildInterruptionReadyRun(() -> responsesToSend.offer(responseToSend))
                         .run();
        } finally {
            responsesLock.unlock();
        }
    }

    public void executeForDoneResponses(Consumer<ResponseToSend> execution) {
        responsesLock.lock();
        responsesToSend.stream()
                       .filter(responseToSend -> responseToSend.response.isDone())
                       .forEach((responseToSend) -> {
                           responsesToSend.remove(responseToSend);
                           execution.accept(responseToSend);
                       });
        responsesLock.unlock();
    }
}
