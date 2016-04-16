package xdi2.transport.impl.udp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.AbstractTransport;

public class UDPTransport extends AbstractTransport<UDPTransportRequest, UDPTransportResponse> {

	private String host;
	private int port;

	private InetSocketAddress inetSocketAddress;
	private DatagramSocket datagramSocket;

	public void execute(UDPTransportRequest request, UDPTransportResponse response) throws Xdi2TransportException, IOException {

	}

	@Override
	public void init() throws Exception {

		super.init();

		this.inetSocketAddress = new InetSocketAddress(this.getHost(), this.getPort());
	}

	@Override
	public void shutdown() throws Exception {

	}

	/*
	 * Getters and setters
	 */
	
	public String getHost() {

		return this.host;
	}

	public void setHost(String host) {

		this.host = host;
	}

	public int getPort() {

		return this.port;
	}

	public void setPort(int port) {

		this.port = port;
	}
}
