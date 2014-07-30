package me.chat.server.commands;

import me.chat.common.Parsable;

/**
 * User: sennen
 * Date: 09/07/2014
 * Time: 13:03
 */
public interface Command<T extends Parsable> {
    boolean accept(String request);

    T execute(String request);
}
