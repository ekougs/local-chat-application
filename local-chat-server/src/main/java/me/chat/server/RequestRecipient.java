package me.chat.server;

import me.chat.server.tasks.TasksManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * User: sennen
 * Date: 06/07/2014
 * Time: 15:13
 */
@Component
public class RequestRecipient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestRecipient.class);
    private static final int PORT = 4444;

    @Autowired
    private TasksManager tasksManager;

    @Autowired
    private ExecutorService executorService;
    private boolean stop;

    public void listen() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (!stop && !Thread.currentThread().isInterrupted()) {
                Socket serverClientSocket = serverSocket.accept();
                InetSocketAddress clientSocketAddress = (InetSocketAddress) serverClientSocket.getRemoteSocketAddress();
                InputStream clientInputStream = serverClientSocket.getInputStream();
                try (InputStreamReader inputStreamReader = new InputStreamReader(clientInputStream);
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String request = bufferedReader.readLine();
                    tasksManager.submit(clientSocketAddress, request);
                }
                serverClientSocket.close();
            }

        } catch (IOException e) {
            LOGGER.error("Could not begin clients listening", e);
        }
    }

    public void stop() {
        this.stop = true;
    }
}
