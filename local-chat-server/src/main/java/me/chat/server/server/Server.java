package me.chat.server.server;

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: sennen
 * Date: 01/08/2014
 * Time: 21:09
 */
@Component
class Server {
    private static final int PORT = 4444;
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private Lock serverSocketLock = new ReentrantLock();

    @GuardedBy("serverSocketLock")
    private Optional<ServerSocket> serverSocket = Optional.empty();

    Request getRequest() {
        serverSocketLock.lock();
        try {
            ServerSocket serverSocket = getServerSocket();
            if (serverSocket.isClosed()) {
                throw new RequestNotRetrievedException();
            }
            Socket serverClientSocket;
            try {
                serverClientSocket = serverSocket.accept();
            } catch (SocketTimeoutException e) {
                throw new RequestNotRetrievedException();
            }
            final InetSocketAddress clientSocketAddress = (InetSocketAddress) serverClientSocket.getRemoteSocketAddress();
            InputStream clientInputStream = serverClientSocket.getInputStream();
            try (InputStreamReader inputStreamReader = new InputStreamReader(clientInputStream);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return new Request(clientSocketAddress, bufferedReader.readLine());
            } finally {
                serverClientSocket.close();
            }
        } catch (IOException e) {
            throw new RequestNotRetrievedException();
        } finally {
            serverSocketLock.unlock();
        }
    }

    void stop() {
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
