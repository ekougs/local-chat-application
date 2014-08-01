package me.chat.server.tasks;

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
public class ResponsesToSend {
    private final Lock responsesLock = new ReentrantLock();

    @GuardedBy("responsesLock")
    private final BlockingQueue<ResponseToSend> responsesToSend = new ArrayBlockingQueue<>(20);

    public void put(ResponseToSend responseToSend) {
        Concurrencies.buildInterruptionReadyRun(() -> responsesToSend.offer(responseToSend))
                     .run();
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
