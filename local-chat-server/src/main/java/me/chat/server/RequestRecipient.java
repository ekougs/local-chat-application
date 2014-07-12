package me.chat.server;

import me.chat.server.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
    @Qualifier("global")
    private Command commandFactory;

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
