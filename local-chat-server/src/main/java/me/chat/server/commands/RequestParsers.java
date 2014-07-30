package me.chat.server.commands;

/**
 * User: sennen
 * Date: 11/07/2014
 * Time: 18:37
 */
public class RequestParsers {
    public static boolean accept(String prefix, String request) {
        return request.startsWith(prefix);
    }

    public static String extractBody(String prefix, String request) {
        return request.substring(prefix.length());
    }
}
