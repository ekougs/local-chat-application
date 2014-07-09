package me.chat.server.users;

import me.chat.server.InMemoryConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.chat.common.UserConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: sennen
 * Date: 06/07/2014
 * Time: 14:47
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InMemoryConfiguration.class)
public class InMemoryUsersManagerTest {
    @Autowired
    private UsersManager usersManager = new InMemoryUsersManager();

    //@Test
    public void testConcurrentAdditions() throws Exception {
        Thread sennenAdditionThread = new Thread(() -> usersManager.connect(SENNEN));
        Thread pascalAdditionThread = new Thread(() -> usersManager.connect(PASCAL));
        sennenAdditionThread.start();
        pascalAdditionThread.start();
        usersManager.connect(NGUEMA);
        sennenAdditionThread.join();
        pascalAdditionThread.join();
        assertThat(usersManager.getOtherUsers(SENNEN)).containsOnly(PASCAL, NGUEMA);
        assertThat(usersManager.getOtherUsers(PASCAL)).containsOnly(SENNEN, NGUEMA);
    }

    @Test
    public void testRemoval() throws Exception {
        usersManager.connect(SENNEN);
        usersManager.connect(PASCAL);
        usersManager.connect(NGUEMA);
        usersManager.connect(EKOUGS);
        assertThat(usersManager.getOtherUsers(SENNEN)).containsOnly(PASCAL, NGUEMA, EKOUGS);
        Thread sennenRemovalThread = new Thread(() -> usersManager.disconnect(SENNEN));
        Thread ekougsRemovalThread = new Thread(() -> usersManager.disconnect(EKOUGS));
        sennenRemovalThread.start();
        ekougsRemovalThread.start();
        ekougsRemovalThread.join();
        sennenRemovalThread.join();
        assertThat(usersManager.getOtherUsers(SENNEN)).containsOnly(PASCAL, NGUEMA);
        assertThat(usersManager.getOtherUsers(PASCAL)).containsOnly(NGUEMA);
    }

    @Test
    public void testExecutionIfConnected() throws Exception {
        usersManager.connect(SENNEN);
        usersManager.executeIfConnected(SENNEN, () -> {
        });
    }

    // TODO does not work well with Spring
    @Ignore
    @Test(expected = UserNotConnectedException.class)
    public void testExecutionIfNotConnected() throws Exception {
        usersManager.executeIfConnected(SENNEN, () -> {
        });
    }
}
