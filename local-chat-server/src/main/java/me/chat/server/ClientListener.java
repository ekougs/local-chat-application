package me.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: sennen
 * Date: 06/07/2014
 * Time: 15:13
 */
public class ClientListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientListener.class);
    private static final int PORT = 4444;

    public void listen() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                InetAddress localAddress = clientSocket.getLocalAddress();
            }

        } catch (IOException e) {
            LOGGER.error("Could not begin clients listening", e);
        }
    }
}
