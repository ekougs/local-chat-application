package me.chat.server.command;

import me.chat.common.Parsable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: sennen
 * Date: 09/07/2014
 * Time: 12:38
 */
@Component
@Qualifier("global")
public class GlobalCommand implements Command {
    @Autowired
    @Qualifier("childCommand")
    private List<Command> commands;

    @Override
    public boolean accept(String request) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Parsable get(String request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
