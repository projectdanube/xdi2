package xdi2.client.impl.udp;

import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.FutureMessagingResponse;
import xdi2.messaging.response.TransportMessagingResponse;

/**
 * An XDI client that can send XDI messages over UDP and receive results.
 * It supports the following parameters (passed to the init method):
 * <ul>
 * <li>host - The host of the XDI endpoint to talk to.</li>
 * <li>port - The port of the XDI endpoint to talk to.</li>
 * <li>sendMimeType - The mime type to use to send the XDI messages to the endpoint.</li>
 * </ul> 
 * 
 * @author markus
 */
public class XDIUDPClient extends XDIAbstractClient<FutureMessagingResponse> implements XDIClient<FutureMessagingResponse> {

	public static final String KEY_HOST = "host";
	public static final String KEY_PORT = "port";
	public static final String KEY_SENDMIMETYPE = "sendmimetype";

	public static final String DEFAULT_SENDMIMETYPE = "application/xdi+json;implied=0";

	private static final Logger log = LoggerFactory.getLogger(XDIUDPClient.class);

	private DatagramSocket datagramSocket;
	private String host;
	private int port;

	private MimeType sendMimeType;

	private Callback callback;
	private Map<XDIAddress, FutureMessagingResponse> futureMessagingResponses;

	public XDIUDPClient(DatagramSocket datagramSocket, String host, int port, MimeType sendMimeType) {

		super();

		this.datagramSocket = datagramSocket;
		this.host = host;
		this.port = port;

		this.sendMimeType = (sendMimeType != null) ? sendMimeType : new MimeType(DEFAULT_SENDMIMETYPE);

		this.callback = null;
		this.futureMessagingResponses = new HashMap<XDIAddress, FutureMessagingResponse> ();
	}

	public XDIUDPClient(DatagramSocket datagramSocket, String host, int port) {

		this(datagramSocket, host, port, null);
	}

	public XDIUDPClient(DatagramSocket datagramSocket, Properties parameters) {

		this(datagramSocket, null, -1, null);

		if (parameters != null) {

			if (parameters.containsKey(KEY_HOST)) this.host = parameters.getProperty(KEY_HOST);
			if (parameters.containsKey(KEY_PORT)) this.port = Integer.parseInt(parameters.getProperty(KEY_PORT));
			if (parameters.containsKey(KEY_SENDMIMETYPE)) this.sendMimeType = new MimeType(parameters.getProperty(KEY_SENDMIMETYPE));

			if (log.isDebugEnabled()) log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

	public XDIUDPClient(DatagramSocket datagramSocket) {

		this(datagramSocket, null, -1, null);
	}

	public XDIUDPClient(String host, int port, MimeType sendMimeType) {

		this((DatagramSocket) null, host, port, sendMimeType);
	}

	public XDIUDPClient(String host, int port) {

		this((DatagramSocket) null, host, port);
	}

	public XDIUDPClient(Properties parameters) {

		this((DatagramSocket) null, parameters);
	}

	public XDIUDPClient() {

		this((DatagramSocket) null);
	}

	@Override
	protected FutureMessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// find out which XDIWriter we want to use

		MimeType sendMimeType = this.sendMimeType;
		XDIWriter writer = XDIWriterRegistry.forMimeType(sendMimeType);

		if (writer == null) {

			sendMimeType = new MimeType(DEFAULT_SENDMIMETYPE);
			writer = XDIWriterRegistry.forMimeType(sendMimeType);
		}

		if (writer == null) throw new Xdi2ClientException("Cannot find a suitable XDI writer.");
		if (log.isDebugEnabled()) log.debug("Using writer " + writer.getClass().getName() + ".");

		// find out which XDIReader we want to use

		XDIReader reader = XDIReaderRegistry.getAuto();

		if (reader == null) throw new Xdi2ClientException("Cannot find a suitable XDI reader.");
		if (log.isDebugEnabled()) log.debug("Using reader " + reader.getClass().getName() + ".");

		// connect

		DatagramSocket datagramSocket = null;

		try {

			datagramSocket = this.connect();
		} catch (Exception ex) {

			this.disconnect();

			throw new Xdi2ClientException("Cannot open UDP connection: " + ex.getMessage(), ex);
		}

		// send the message envelope

		if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(null, null));

		try {

			StringWriter buffer = new StringWriter();
			writer.write(messageEnvelope.getGraph(), buffer);

			byte[] bytes = buffer.getBuffer().toString().getBytes("UTF-8");
			final DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
			datagramSocket.send(datagramPacket);
		} catch (Exception ex) {

			this.disconnect();

			throw new Xdi2ClientException("Cannot send message envelope: " + ex.getMessage(), ex);
		}

		// we return a future messaging response

		FutureMessagingResponse futureMessagingResponse = FutureMessagingResponse.fromMessageEnvelope(messageEnvelope);

		for (Message message : messageEnvelope.getMessages()) {

			this.putFutureMessagingResponse(message.getContextNode().getXDIAddress(), futureMessagingResponse);
		}

		// done

		return futureMessagingResponse;
	}

	@Override
	public void close() {

		this.disconnect();
	}

	private DatagramSocket connect() throws Exception {

		if (this.getDatagramSocket() != null) return this.getDatagramSocket();

		if (this.getHost() == null) throw new Xdi2ClientException("No host to connect to.");
		if (this.getPort() <= 0) throw new Xdi2ClientException("No port to connect to.");

		// connect

		if (log.isDebugEnabled()) log.debug("Connecting to " + this.getHost() + ":" + this.getPort());

		DatagramSocket datagramSocket = new DatagramSocket();
		datagramSocket.connect(new InetSocketAddress(this.getHost(), this.getPort()));

		// done

		if (log.isDebugEnabled()) log.debug("Connected successfully.");

		this.setDatagramSocket(datagramSocket);
		return datagramSocket;
	}

	private void disconnect() {

		try {

			if (this.getDatagramSocket() != null) {

				if (this.getDatagramSocket().isConnected()) this.getDatagramSocket().disconnect();
				if (! this.getDatagramSocket().isClosed()) this.getDatagramSocket().close();
			}
		} catch (Exception ex) {

			log.error("Cannot disconnect: " + ex.getMessage(), ex);
		} finally {

			this.setDatagramSocket(null);
		}

		if (log.isDebugEnabled()) log.debug("Disconnected successfully.");
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

	public MimeType getSendMimeType() {

		return this.sendMimeType;
	}

	public void setSendMimeType(MimeType sendMimeType) {

		this.sendMimeType = sendMimeType;
	}

	public Callback getCallback() {

		return this.callback;
	}

	public void setCallback(Callback callback) {

		this.callback = callback;
	}

	public Map<XDIAddress, FutureMessagingResponse> getFutureMessagingResponses() {

		return this.futureMessagingResponses;
	}

	public void putFutureMessagingResponse(XDIAddress messageXDIaddress, FutureMessagingResponse futureMessagingResponse) {

		if (log.isDebugEnabled()) log.debug("Putting future messaging response for message " + messageXDIaddress);

		this.futureMessagingResponses.put(messageXDIaddress, futureMessagingResponse);
	}

	public void removeFutureMessagingResponse(XDIAddress messageXDIaddress) {

		if (log.isDebugEnabled()) log.debug("Removing future messaging response for message " + messageXDIaddress);

		this.futureMessagingResponses.remove(messageXDIaddress);
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getHost() + ":" + Integer.toString(this.getPort());
	}

	/*
	 * Helper classes
	 */

	public static interface Callback {

		public void onMessageEnvelope(MessageEnvelope messageEnvelope);
		public void onMessagingResponse(TransportMessagingResponse messagingResponse);
	}
}
