package me.chat.server.server;

import me.chat.common.Parsable;

import java.util.concurrent.Future;

/**
 * User: sennen
 * Date: 31/07/2014
 * Time: 00:43
 */
class ResponseToSend {
    final String request;
    final String user;
    final Future<Parsable> response;

    ResponseToSend(String request, String user, Future<Parsable> response) {
        this.request = request;
        this.user = user;
        this.response = response;
    }
}
