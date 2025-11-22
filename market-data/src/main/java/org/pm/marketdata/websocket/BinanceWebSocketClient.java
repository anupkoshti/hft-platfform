package org.pm.marketdata.websocket;


import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.netty.http.client.HttpClient;

import java.util.function.Consumer;

@Component
public class BinanceWebSocketClient {

    private Disposable connection;

    public void connect(String uri, Consumer<String> onMessage) {
        connection = HttpClient.create()
                .websocket()
                .uri(uri)
                .handle((inbound, outbound) -> 
                    inbound.receive()
                            .asString()
                            .doOnNext(msg -> {
                                System.out.println("Received message: " + msg.substring(0, Math.min(100, msg.length())));
                                onMessage.accept(msg);
                            })
                            .doOnError(err -> System.err.println("WebSocket error: " + err.getMessage()))
                            .doFinally(sig -> System.out.println("WebSocket closed: " + sig))
                            .then()
                )
                .subscribe(
                    unused -> System.out.println("WebSocket connected successfully"),
                    error -> System.err.println("Failed to connect: " + error.getMessage())
                );
    }

    public void disconnect() {
        if (connection != null && !connection.isDisposed()) {
            connection.dispose();
        }
    }
}
