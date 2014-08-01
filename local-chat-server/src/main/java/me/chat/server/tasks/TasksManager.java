package me.chat.server.tasks;

import me.chat.server.commands.GlobalCommand;
import me.chat.server.tasks.util.Concurrencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseSender.class);

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
                     .whenInterruption(() -> this.reactToInterruption(clientSocketAddress, request))
                     .run();
    }

    private void reactToInterruption(InetSocketAddress clientSocketAddress, String request) {
        LOGGER.info("Request could not be handled : " + request + " " + clientSocketAddress);
    }
}
