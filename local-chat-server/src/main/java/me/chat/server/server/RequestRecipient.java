package me.chat.server.server;

import me.chat.server.tasks.TasksManager;
import me.chat.server.tasks.util.Concurrencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

/**
 * User: sennen
 * Date: 06/07/2014
 * Time: 15:13
 */
@Component
public class RequestRecipient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRecipient.class);

    @Autowired
    private TasksManager tasksManager;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private Server server;

    public void listen() {
        while (!Thread.currentThread().isInterrupted()) {
            final Request request;
            try {
                request = server.getRequest();
                executorService.submit(() -> tasksManager.submit(request));
            } catch (RequestNotRetrievedException e) {
                Concurrencies.buildInterruptionReadyRun(() -> Thread.sleep(50))
                        .whenInterruption(executorService::shutdown)
                        .run();
            }
        }
    }

    public void stop() {
        server.stop();
    }
}
