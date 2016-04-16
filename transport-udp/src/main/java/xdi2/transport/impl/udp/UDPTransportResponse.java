package xdi2.transport.impl.udp;


import java.net.DatagramSocket;

import xdi2.transport.TransportResponse;
import xdi2.transport.impl.AbstractTransportResponse;

/**
 * This class represents a UDP response from the server.
 * This is used by the UDPTransport.
 * 
 * @author markus
 */
public class UDPTransportResponse extends AbstractTransportResponse implements TransportResponse {

	private DatagramSocket datagramSocket;

	private UDPTransportResponse(DatagramSocket datagramSocket) {

		this.datagramSocket = datagramSocket;
	}

	public static UDPTransportResponse create(DatagramSocket datagramSocket) {

		return new UDPTransportResponse(datagramSocket);
	}

	/*
	 * Getters and setters
	 */

	public DatagramSocket getDatagramSocket() {

		return this.datagramSocket;
	}
}
