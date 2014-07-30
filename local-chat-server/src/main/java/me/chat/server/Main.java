package me.chat.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 14:25
 */
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(InMemoryConfiguration.class);
        context.registerShutdownHook();
        RequestRecipient requestRecipient = context.getBean(RequestRecipient.class);
        requestRecipient.listen();
    }
}
