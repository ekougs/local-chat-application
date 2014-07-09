package me.chat.common;

/**
 * User: sennen
 * Date: 09/07/2014
 * Time: 12:59
 */
public interface Parsable {
    default String parse() {
       throw new NoResponseExpectedException();
    }
}
