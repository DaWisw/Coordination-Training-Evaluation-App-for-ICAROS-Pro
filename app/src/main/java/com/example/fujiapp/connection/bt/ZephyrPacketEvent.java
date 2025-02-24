package com.example.fujiapp.connection.bt;

import java.util.EventObject;

public class ZephyrPacketEvent extends EventObject{
	private ZephyrPacketArgs _packet;
	public ZephyrPacketArgs getPacket() {return _packet;}
	
	public ZephyrPacketEvent(Object source, ZephyrPacketArgs packet)
	{
		super(source);
		_packet = packet;
	}
}
