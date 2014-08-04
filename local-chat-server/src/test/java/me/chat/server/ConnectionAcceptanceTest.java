package me.chat.server;

import me.chat.common.exception.UserNotConnectedException;
import me.chat.server.users.UsersManager;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static me.chat.common.UserConstants.*;

/**
 * User: sennen
 * Date: 31/07/2014
 * Time: 22:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class ConnectionAcceptanceTest extends AcceptanceTestCase {
    @Autowired
    private UsersManager usersManager;

    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testConnectionCommand() throws Exception {
        givenUserIsNotConnected(SENNEN);

        Future<String> connectionResponse = whenCommandAsyncSent(
                "connect:{\"user\":\"Sennen\",\"address\":\"127.0.0.1\",\"port\":5555}");

        thenResponseOKAndUserIsConnected(connectionResponse, SENNEN);
    }

    @Test
    public void testConnectionOnAlreadyConnectedUser() throws Exception {
        givenUserIsConnected(SENNEN);

        Future<String> connectionResponse = whenCommandAsyncSent(
                "connect:{\"user\":\"Sennen\",\"address\":\"127.0.0.1\",\"port\":5555}");

        thenResponseUserAlreadyConnected(connectionResponse);
    }

    @Test
    public void testDisconnectionCommand() throws Exception {
        givenUserIsConnected(SENNEN);

        Future<String> disconnectionResponse = whenCommandAsyncSent("disconnect:Sennen");

        thenResponseOKAndUserIsDisconnected(disconnectionResponse, SENNEN);
    }

    @Test
    public void testOtherUsersCommand() throws Exception {
        givenUsersAreConnected(SENNEN, PASCAL, EKOUGS);

        Future<String> otherUsersResponse = whenCommandAsyncSent("others:Sennen");

        thenResponseIsOtherUsers(otherUsersResponse, PASCAL, EKOUGS);
    }

    private void givenUserIsNotConnected(String user) {
        checkUserIsNotConnected(user);
    }

    private void givenUserIsConnected(String user) {
        givenUsersAreConnected(user);
    }

    private void givenUsersAreConnected(String... users) {
        for (String user : users) {
            connect(user, localHost1);
            checkUserIsConnected(user);
        }
    }

    private void thenResponseOKAndUserIsConnected(Future<String> connectionResponse,
                                                  String user)
    throws IOException, ExecutionException, InterruptedException {
        Assertions.assertThat(connectionResponse.get()).isEqualTo("OK");
        checkUserIsConnected(user);
        Assertions.assertThat(usersManager.getAddress(user)).isEqualTo(new InetSocketAddress("127.0.0.1", 5555));
        usersManager.disconnect(user);
    }

    private void thenResponseUserAlreadyConnected(Future<String> connectionResponse)
    throws ExecutionException, InterruptedException {
        Assertions.assertThat(connectionResponse.get()).isEqualTo(
                "{\"requestInError\":\"connect:{\\\"user\\\":\\\"Sennen\\\",\\\"address\\\":\\\"127.0.0.1\\\",\\\"port\\\":5555}\",\"error\":\"UserNameAlreadyUsedException\"}");
    }

    private void thenResponseOKAndUserIsDisconnected(Future<String> disconnectionResponse,
                                                     String user)
    throws IOException, ExecutionException, InterruptedException {
        Assertions.assertThat(disconnectionResponse.get()).isEqualTo("OK");
        checkUserIsNotConnected(user);
    }

    private void thenResponseIsOtherUsers(Future<String> otherUsersResponse,
                                          String... otherUsers) throws ExecutionException, InterruptedException {
        StringBuilder otherUsersString = new StringBuilder();
        for (String otherUser : otherUsers) {
            otherUsersString.append("\"").append(otherUser).append("\"").append(";");
        }
        String expectedOtherUsers = otherUsersString.toString().substring(0, otherUsersString.length() - 1);
        Assertions.assertThat(otherUsersResponse.get()).isEqualTo(expectedOtherUsers);
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
