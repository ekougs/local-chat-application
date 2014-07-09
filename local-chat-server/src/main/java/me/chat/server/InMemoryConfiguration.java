package me.chat.server;

import me.chat.server.messages.InMemoryMessageRetriever;
import me.chat.server.messages.MessageRetriever;
import me.chat.server.translation.RequestTranslator;
import me.chat.server.users.InMemoryUsersManager;
import me.chat.server.users.UsersManager;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 12:57
 */
@Configuration
@ComponentScan(basePackageClasses = {MessageRetriever.class, UsersManager.class, RequestTranslator.class})
public class InMemoryConfiguration {
    @Bean
    public UsersManager usersManager() {
        return new InMemoryUsersManager();
    }

    @Bean
    public MessageRetriever messageRetriever() {
        return new InMemoryMessageRetriever();
    }

    @Bean
    public RequestTranslator requestTranslator() {
        return new RequestTranslator();
    }
}
