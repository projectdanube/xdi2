package xdi2.transport.impl.udp;


import java.net.DatagramPacket;

import xdi2.transport.TransportRequest;
import xdi2.transport.impl.AbstractTransportRequest;

/**
 * This class represents a UDP request to the server.
 * This is used by the UDPTransport.
 * 
 * @author markus
 */
public class UDPTransportRequest extends AbstractTransportRequest implements TransportRequest {

	private DatagramPacket datagramPacket;

	private UDPTransportRequest(DatagramPacket datagramPacket) {

		this.datagramPacket = datagramPacket;
	}

	public static UDPTransportRequest create(DatagramPacket datagramPacket) {

		return new UDPTransportRequest(datagramPacket);
	}

	/*
	 * Getters and setters
	 */

	public DatagramPacket getDatagramPacket() {

		return this.datagramPacket;
	}

}
