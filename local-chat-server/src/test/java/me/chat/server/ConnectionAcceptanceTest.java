package me.chat.server;

import me.chat.common.UserConstants;
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

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static me.chat.common.UserConstants.EKOUGS;
import static me.chat.common.UserConstants.PASCAL;
import static me.chat.common.UserConstants.SENNEN;

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
        for (String user : UserConstants.getAllUsers()) {
            usersManager.disconnect(user);
        }
        super.tearDown();
    }

    @Test
    public void testConnectionCommand() throws Exception {
        givenUserIsNotConnected(SENNEN);

        Future<String> connectionResponse = whenCommandAsyncSent("connect:Sennen");

        thenResponseOKAndUserIsConnected(connectionResponse, SENNEN);
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
            usersManager.connect(user);
            checkUserIsConnected(user);
        }
    }

    private void thenResponseOKAndUserIsConnected(Future<String> connectionResponse,
                                                  String user)
    throws IOException, ExecutionException, InterruptedException {
        Assertions.assertThat(connectionResponse.get()).isEqualTo("OK");
        checkUserIsConnected(user);
        usersManager.disconnect(user);
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
