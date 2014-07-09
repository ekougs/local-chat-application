package me.chat.server.translation;

import me.chat.common.Parsable;

/**
 * User: sennen
 * Date: 09/07/2014
 * Time: 13:03
 */
public interface Command<T extends Parsable> {
    T get();
}
