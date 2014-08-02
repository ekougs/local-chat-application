package me.chat.server;

import me.chat.common.UserConstants;
import me.chat.server.server.RequestRecipient;
import me.chat.server.users.UsersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * User: sennen
 * Date: 02/08/2014
 * Time: 00:18
 */
public class AcceptanceTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptanceTestCase.class);

    @Autowired
    private RequestRecipient requestRecipient;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private UsersManager usersManager;

    InetSocketAddress localHost1;

    public void setUp() {
        executor.submit(requestRecipient::listen);
        localHost1 = new InetSocketAddress(5555);
    }

    public void tearDown() {
        for (String user : UserConstants.getAllUsers()) {
            usersManager.disconnect(user);
        }
        requestRecipient.stop();
    }

    Future<String> whenCommandAsyncSent(String command) throws IOException {
        Future<String> responseFuture = executor.submit((Callable<String>) AcceptanceTestCase::getResponse);
        executor.submit(() -> AcceptanceTestCase.sendCommand(command));
        return responseFuture;
    }

    private static void sendCommand(String command) {
        PrintWriter connexionSocketOutput = null;
        Socket requestSocket = null;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            requestSocket = new Socket(localHost.getHostAddress(), 4444);
            connexionSocketOutput = new PrintWriter(requestSocket.getOutputStream());
            connexionSocketOutput.println(command);
            connexionSocketOutput.flush();
            connexionSocketOutput.close();
            requestSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (connexionSocketOutput != null) {
                connexionSocketOutput.close();
            }
            if (requestSocket != null) {
                try {
                    requestSocket.close();
                } catch (IOException e) {
                    LOGGER.warn("Could not close request socket successfully", e);
                }
            }
        }
    }

    private static String getResponse() throws IOException {
        ServerSocket responseRecipient = new ServerSocket(5555);
        Socket responseSocket = responseRecipient.accept();
        InputStream clientInputStream = responseSocket.getInputStream();
        String response;
        try (InputStreamReader inputStreamReader = new InputStreamReader(clientInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            response = bufferedReader.readLine();
        }
        responseRecipient.close();
        return response;
    }
}
