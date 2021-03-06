package me.chat.common;

import java.util.Arrays;

/**
 * User: sennen
 * Date: 08/07/2014
 * Time: 13:46
 */
public class UserConstants {
    public static final String SENNEN = "Sennen";
    public static final String PASCAL = "Pascal";
    public static final String NGUEMA = "Nguema";
    public static final String EKOUGS = "Ekougs";
    public static final String DISCONNECTED = "disconnectedUser";

    public static Iterable<String> getAllUsers() {
        return Arrays.asList(SENNEN, PASCAL, NGUEMA, EKOUGS);
    }
}
