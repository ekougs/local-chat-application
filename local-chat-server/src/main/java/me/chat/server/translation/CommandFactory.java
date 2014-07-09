package me.chat.server.translation;

/**
 * User: sennen
 * Date: 09/07/2014
 * Time: 12:59
 */
public interface CommandFactory {
    boolean accept(String request);

    Command get(String request);
}
