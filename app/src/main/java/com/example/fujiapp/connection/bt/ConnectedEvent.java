package com.example.fujiapp.connection.bt;

import java.util.EventObject;

public class ConnectedEvent<T> extends EventObject 
{
	public ConnectedEvent(T source) {
		super(source);
	}	
	
	@SuppressWarnings("unchecked")
	public T getSource() { return (T)this.source; }
}

