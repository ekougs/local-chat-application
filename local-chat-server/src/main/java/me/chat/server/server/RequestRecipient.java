package me.chat.server.server;

import me.chat.server.tasks.TasksManager;
import me.chat.server.tasks.util.Concurrencies;
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
    @Autowired
    private TasksManager tasksManager;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private Server server;

    public void listen() {
        server.open();
        while (!Thread.currentThread().isInterrupted()) {
            final String request;
            try {
                request = server.getRequest();
                executorService.submit(() -> tasksManager.submit(request));
            } catch (RequestNotRetrievedException e) {
                Concurrencies.buildInterruptionReadyRun(() -> Thread.sleep(50))
                             .whenInterruption(executorService::shutdown)
                             .run();
            } catch (ServerClosedException e) {
                break;
            }
        }
    }

    public void stop() {
        server.close();
    }
}
