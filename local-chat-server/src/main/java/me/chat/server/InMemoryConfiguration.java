package me.chat.server;

import me.chat.server.commands.GlobalCommand;
import me.chat.server.messages.MessageHandler;
import me.chat.server.server.RequestRecipient;
import me.chat.server.tasks.TasksManager;
import me.chat.server.users.UsersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:57
 */
@Configuration
@ComponentScan(basePackageClasses = {InMemoryConfiguration.class, MessageHandler.class, UsersManager.class, GlobalCommand.class, TasksManager.class, RequestRecipient.class})
public class InMemoryConfiguration {

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
