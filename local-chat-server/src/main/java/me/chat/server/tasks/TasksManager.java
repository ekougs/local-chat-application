package me.chat.server.tasks;

import me.chat.server.commands.GlobalCommand;
import me.chat.server.tasks.util.Concurrencies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 14:33
 */
@Component
public class TasksManager {
    @Autowired
    private GlobalCommand globalCommand;

    @Autowired
    private ResponseSender responseSender;

    @Autowired
    private ExecutorService executor;

    public void submit(InetSocketAddress clientSocketAddress, String request) {
        Concurrencies.buildInterruptionReadyRun(() -> {
            CommandCallable commandCallable = new CommandCallable(clientSocketAddress, globalCommand, request);
            responseSender.sendWhenPossible(commandCallable, executor.submit(commandCallable));
        })
                     .whenInterruption(this::reactToInterruption)
                     .run();
    }

    private void reactToInterruption() {
        executor.shutdown();
    }
}
