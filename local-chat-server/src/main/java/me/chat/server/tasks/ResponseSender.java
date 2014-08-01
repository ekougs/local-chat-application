package me.chat.server.tasks;

import me.chat.common.Parsable;
import me.chat.server.tasks.util.Concurrencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * User: sennen
 * Date: 12/07/2014
 * Time: 14:35
 */
@Component
public class ResponseSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseSender.class);

    @Autowired
    private ExecutorService executor;

    private final ResponsesToSend responsesToSend = new ResponsesToSend();
    private boolean sending = false;

    public void sendWhenPossible(CommandCallable commandCallable, Future<Parsable> responseFuture) {
        ResponseToSend responseToSend = new ResponseToSend(commandCallable.getRequest(),
                                                           commandCallable.getAddress(),
                                                           responseFuture);
        responsesToSend.put(responseToSend);
        sendDoneResponses();
    }

    private void sendDoneResponses() {
        if (!sending) {
            executor.submit(() -> Concurrencies
                    .buildInterruptionReadyRun(this::launchResponsesShipment)
                    .whenInterruption(() -> sending = false)
                    .run());
        }
    }

    private void launchResponsesShipment() throws InterruptedException {
        sending = true;
        while (sending) {
            responsesToSend.executeForDoneResponses(this::sendResponse);
            Thread.sleep(100);
        }
    }

    private void sendResponse(ResponseToSend responseToSend) {
        Concurrencies.buildInterruptionReadyRun(() -> {
            executor.submit(new SendResponseTask(responseToSend));
        })
                     .whenInterruption(() -> this.reactToInterruption(responseToSend))
                     .run();
    }

    private void reactToInterruption(ResponseToSend responseToSend) {
        LOGGER.info("Request could not be handled : " + responseToSend.request + " " + responseToSend.clientAddress);
    }

    private class SendResponseTask implements Runnable {
        private final ResponseToSend responseToSend;

        private SendResponseTask(ResponseToSend responseToSend) {
            this.responseToSend = responseToSend;
        }

        @Override
        public void run() {
            Optional<Parsable> optionalResponse =
                    Concurrencies.buildInterruptionReadyCall(responseToSend.response::get)
                                 .whenInterruption(() -> ResponseSender.this.reactToInterruption(responseToSend))
                                 .whenExecutionException(this::sendErrorMessage)
                                 .call();
            if (!optionalResponse.isPresent()) {
                return;
            }
            sendToClient(optionalResponse.get());
        }

        private void sendErrorMessage() {
            sendToClient(new RequestInError(responseToSend.request));
        }

        private void sendToClient(Parsable response) {
            InetSocketAddress clientAddress = responseToSend.clientAddress;
            try {
                Socket toClientSocket = new Socket(clientAddress.getAddress(), 5555);
                PrintWriter toClientPrintWriter = new PrintWriter(toClientSocket.getOutputStream());
                toClientPrintWriter.println(response.parse());
                toClientPrintWriter.flush();
                toClientPrintWriter.close();
                toClientSocket.close();
            } catch (IOException e) {
                LOGGER.error("Unreachable client " + clientAddress);
            }
        }
    }
}
