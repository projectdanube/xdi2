package xdi2.client.impl.udp;

import java.net.DatagramSocket;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;

public class XDIUDPClientRoute extends XDIAbstractClientRoute<XDIUDPClient> implements XDIClientRoute<XDIUDPClient> {

	private DatagramSocket datagramSocket;
	private String host;
	private int port;

	public XDIUDPClientRoute(XDIArc toPeerRootXDIArc, DatagramSocket datagramSocket, String host, int port) {

		super(toPeerRootXDIArc);

		this.datagramSocket = datagramSocket;
		this.host = host;
		this.port = port;
	}

	public XDIUDPClientRoute(XDIArc toPeerRootXDIArc, DatagramSocket datagramSocket) {

		this(toPeerRootXDIArc, datagramSocket, null, -1);
	}

	public XDIUDPClientRoute(XDIArc toPeerRootXDIArc, String host, int port) {

		this(toPeerRootXDIArc, null, host, port);
	}

	public XDIUDPClientRoute(DatagramSocket datagramSocket) {

		this(null, datagramSocket, null, -1);
	}

	public XDIUDPClientRoute(String host, int port) {

		this(null, null, host, port);
	}

	public XDIUDPClientRoute() {

		this(null, null, null, -1);
	}

	@Override
	public XDIUDPClient constructXDIClientInternal() {

		return new XDIUDPClient(this.getDatagramSocket(), this.getHost(), this.getPort());
	}

	/*
	 * Getters and setters
	 */

	public DatagramSocket getDatagramSocket() {

		return this.datagramSocket;
	}

	public void setDatagramSocket(DatagramSocket datagramSocket) {

		this.datagramSocket = datagramSocket;
	}

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
