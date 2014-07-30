package me.chat.server;

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
import java.util.concurrent.ExecutorService;

import static me.chat.common.UserConstants.*;

/**
 * User: sennen
 * Date: 31/07/2014
 * Time: 22:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class ServerAcceptanceTest {
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
        givenCommandAsyncSent("connect:Sennen");

        String connectionResponse = whenGettingResponse();

        Assertions.assertThat(connectionResponse).isEqualTo("OK");
        usersManager.executeIfConnected(SENNEN, () -> {});
        usersManager.disconnect(SENNEN);
    }

    private String whenGettingResponse() throws IOException {
        ServerSocket responseRecipient = new ServerSocket(5555);
        Socket connectionResponseSocket = responseRecipient.accept();
        InputStream clientInputStream = connectionResponseSocket.getInputStream();
        String connectionResponse;
        try (InputStreamReader inputStreamReader = new InputStreamReader(clientInputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
             connectionResponse = bufferedReader.readLine();
        }
        return connectionResponse;
    }

    private void givenCommandAsyncSent(String command) throws IOException {
        executor.submit(() -> sendCommand(command));
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
        }
        finally {
            if(connexionSocketOutput != null) {
                connexionSocketOutput.close();
            }
            if(connexionRequestSocket != null) {
                try {
                    connexionRequestSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
