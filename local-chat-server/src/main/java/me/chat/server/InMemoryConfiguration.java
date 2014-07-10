package me.chat.server;

import me.chat.server.command.GlobalCommand;
import me.chat.server.messages.MessageHandler;
import me.chat.server.users.UsersManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:57
 */
@Configuration
@ComponentScan(basePackageClasses = {InMemoryConfiguration.class, MessageHandler.class, UsersManager.class, GlobalCommand.class})
public class InMemoryConfiguration {
}
