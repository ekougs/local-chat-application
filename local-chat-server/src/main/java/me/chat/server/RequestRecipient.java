package me.chat.server;

import me.chat.server.tasks.TasksManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.concurrent.GuardedBy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    // TODO
    private final Lock serverSocketLock = new ReentrantLock();

    @GuardedBy("serverSocketLock")
    private Optional<ServerSocket> serverSocket = Optional.empty();

    public void listen() {
        try {
            ServerSocket serverSocket = getServerSocket();
            while (isOpen() && !Thread.currentThread().isInterrupted()) {
                serverSocketLock.lock();
                Socket serverClientSocket;
                try {
                    serverClientSocket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    serverSocketLock.unlock();
                    continue;
                }
                final InetSocketAddress clientSocketAddress = (InetSocketAddress) serverClientSocket.getRemoteSocketAddress();
                InputStream clientInputStream = serverClientSocket.getInputStream();
                try (InputStreamReader inputStreamReader = new InputStreamReader(clientInputStream);
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String request = bufferedReader.readLine();
                    serverSocketLock.unlock();
                    executorService.submit(() -> tasksManager.submit(clientSocketAddress, request));
                } finally {
                    serverClientSocket.close();
                    serverSocketLock.unlock();
                }
                serverSocket = getServerSocket();
            }

        } catch (IOException e) {
            LOGGER.error("Could not begin clients listening", e);
        }
    }

    private boolean isOpen() {
        serverSocketLock.lock();
        boolean isOpen = serverSocket.isPresent() && !serverSocket.get().isClosed();
        serverSocketLock.unlock();
        return isOpen;
    }

    public void stop() {
        serverSocketLock.lock();
        try {
            if (serverSocket.isPresent()) {
                serverSocket.get().close();
                serverSocket = Optional.empty();
            }
        } catch (IOException e) {
            LOGGER.error("Could not close server socket", e);
        }
        serverSocketLock.unlock();
    }

    private ServerSocket getServerSocket() throws IOException {
        serverSocketLock.lock();
        if (!serverSocket.isPresent()) {
            ServerSocket newServerSocket = new ServerSocket(PORT);
            newServerSocket.setSoTimeout(50);
            serverSocket = Optional.of(newServerSocket);
        }
        ServerSocket actualServerSocket = serverSocket.get();
        serverSocketLock.unlock();
        return actualServerSocket;
    }
}
