package me.chat.server.tasks;

import me.chat.common.Parsable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;

/**
* User: sennen
* Date: 31/07/2014
* Time: 00:43
*/
class ResponseToSend {
    final String request;
    final InetSocketAddress clientAddress;
    final Future<Parsable> response;

    ResponseToSend(String request, InetSocketAddress clientAddress, Future<Parsable> response) {
        this.request = request;
        this.clientAddress = clientAddress;
        this.response = response;
    }
}
