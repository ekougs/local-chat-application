package me.chat.common;

/**
 * User: sennen
 * Date: 09/07/2014
 * Time: 12:59
 */
public interface Parsable {
    String parse();

    public static final Parsable OK_PARSABLE = () -> "OK";
}
