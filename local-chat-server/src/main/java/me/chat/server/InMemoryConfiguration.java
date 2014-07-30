package me.chat.server;

import me.chat.server.commands.GlobalCommand;
import me.chat.server.messages.MessageHandler;
import me.chat.server.tasks.TasksManager;
import me.chat.server.users.UsersManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:57
 */
@Configuration
@ComponentScan(basePackageClasses = {InMemoryConfiguration.class, MessageHandler.class, UsersManager.class, GlobalCommand.class, TasksManager.class})
public class InMemoryConfiguration {

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
