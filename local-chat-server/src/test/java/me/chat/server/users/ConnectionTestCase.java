package me.chat.server.users;

import me.chat.common.UserConstants;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

/**
 * User: sennen
 * Date: 03/08/2014
 * Time: 00:27
 */
public class ConnectionTestCase {
    @Autowired
    private UsersManager usersManager;

    protected InetSocketAddress localHost1;
    protected InetSocketAddress localHost2;

    @Before
    public void setUp() throws Exception {
        localHost1 = new InetSocketAddress(5555);
        localHost2 = new InetSocketAddress(5556);
    }

    @After
    public void tearDown() throws Exception {
        for (String user : UserConstants.getAllUsers()) {
            usersManager.disconnect(user);
        }
    }

    protected void connect(String user, InetSocketAddress address) {
        usersManager.connect(new UserConnection(user, address.getHostName(), address.getPort()));
    }
}
