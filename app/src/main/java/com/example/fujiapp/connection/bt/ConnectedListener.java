package com.example.fujiapp.connection.bt;

public interface ConnectedListener<T> {
	public void Connected(ConnectedEvent<T> eventArgs);
}
