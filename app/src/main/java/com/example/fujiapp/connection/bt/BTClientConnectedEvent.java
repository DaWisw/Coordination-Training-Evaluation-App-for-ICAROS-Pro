package com.example.fujiapp.connection.bt;

@FunctionalInterface
public interface BTClientConnectedEvent {
    void onBTClientConnected(BTClient btClient);
}
