package me.chat.server;

import me.chat.server.users.UserNotConnectedException;
import me.chat.server.users.UsersManager;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static me.chat.common.UserConstants.SENNEN;

/**
 * User: sennen
 * Date: 31/07/2014
 * Time: 22:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class ConnectionAcceptanceTest {

    @Autowired
    private RequestRecipient requestRecipient;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private UsersManager usersManager;

    @Before
    public void setUp() {
        executor.submit(requestRecipient::listen);
    }

    @After
    public void tearDown() {
        requestRecipient.stop();
    }

    @Test
    public void testConnectionCommand() throws Exception {
        givenUserIsNotConnected(SENNEN);

        Future<String> connectionResponse = whenCommandAsyncSent("connect:Sennen");

        thenResponseOKAndUserIsConnected(SENNEN, connectionResponse);
    }

    @Test
    public void testDisconnectionCommand() throws Exception {
        givenUserIsConnected(SENNEN);

        Future<String> disconnectionResponse = whenCommandAsyncSent("disconnect:Sennen");

        thenResponseOKAndUserIsDisconnected(SENNEN, disconnectionResponse);

    }

    private void givenUserIsNotConnected(String user) {
        checkUserIsNotConnected(user);
    }

    private void givenUserIsConnected(String user) {
        usersManager.connect(user);
        checkUserIsConnected(user);
    }

    private Future<String> whenCommandAsyncSent(String command) throws IOException {
        Future<String> responseFuture = executor.submit((Callable<String>) this::getResponse);
        executor.submit(() -> sendCommand(command));
        return responseFuture;
    }

    private void thenResponseOKAndUserIsConnected(String user, Future<String> connectionResponse)
    throws IOException, ExecutionException, InterruptedException {
        Assertions.assertThat(connectionResponse.get()).isEqualTo("OK");
        checkUserIsConnected(user);
        usersManager.disconnect(user);
    }

    private void thenResponseOKAndUserIsDisconnected(String user, Future<String> disconnectionResponse)
    throws IOException, ExecutionException, InterruptedException {
        Assertions.assertThat(disconnectionResponse.get()).isEqualTo("OK");
        checkUserIsNotConnected(user);
    }

    private static void sendCommand(String command) {
        PrintWriter connexionSocketOutput = null;
        Socket connexionRequestSocket = null;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            connexionRequestSocket = new Socket(localHost.getHostAddress(), 4444);
            connexionSocketOutput = new PrintWriter(connexionRequestSocket.getOutputStream());
            connexionSocketOutput.println(command);
            connexionSocketOutput.flush();
            connexionSocketOutput.close();
            connexionRequestSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (connexionSocketOutput != null) {
                connexionSocketOutput.close();
            }
            if (connexionRequestSocket != null) {
                try {
                    connexionRequestSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private String getResponse() throws IOException {
        ServerSocket responseRecipient = new ServerSocket(5555);
        Socket connectionResponseSocket = responseRecipient.accept();
        InputStream clientInputStream = connectionResponseSocket.getInputStream();
        String connectionResponse;
        try (InputStreamReader inputStreamReader = new InputStreamReader(clientInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            connectionResponse = bufferedReader.readLine();
        }
        responseRecipient.close();
        return connectionResponse;
    }

    private void checkUserIsNotConnected(String user) {
        try {
            checkUserIsConnected(user);
        } catch (UserNotConnectedException e) {
        }
    }

    private void checkUserIsConnected(String user) {
        usersManager.executeIfConnected(user, () -> {
        });
    }
}
