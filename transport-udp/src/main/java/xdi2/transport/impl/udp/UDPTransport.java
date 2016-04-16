package xdi2.transport.impl.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.TransportMessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.AbstractTransport;

public class UDPTransport extends AbstractTransport<UDPTransportRequest, UDPTransportResponse> {

	private static final Logger log = LoggerFactory.getLogger(UDPTransport.class);

	public static final String DEFAULT_HOST = null;
	public static final int DEFAULT_PORT = 8888;
	public static final int DEFAULT_BUFFER_SIZE = 512;
	public static final Integer DEFAULT_THREADS = null;

	private String host;
	private int port;
	private int bufferSize;
	private Integer threads;
	private MessagingTarget messagingTarget;

	private InetSocketAddress inetSocketAddress;
	private DatagramSocket datagramSocket;
	private ExecutorService executorService;

	public UDPTransport(String host, int port, int bufferSize, Integer threads) {

		this.host = host;
		this.port = port;
		this.bufferSize = bufferSize;
		this.threads = threads;

		this.messagingTarget = null;
	}

	public UDPTransport(String host, int port, int bufferSize, Integer threads, MessagingTarget messagingTarget) {

		this.host = host;
		this.port = port;
		this.bufferSize = bufferSize;
		this.threads = threads;

		this.messagingTarget = messagingTarget;
	}

	public UDPTransport() {

		this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_BUFFER_SIZE, DEFAULT_THREADS);
	}

	@Override
	public void init() throws Exception {

		super.init();

		// open socket

		this.inetSocketAddress = new InetSocketAddress(this.getHost(), this.getPort());
		this.datagramSocket = new DatagramSocket();
		this.datagramSocket.bind(this.inetSocketAddress);

		if (log.isInfoEnabled()) log.info("Socket opened at " + this.inetSocketAddress);

		// create executor service

		if (this.getThreads() != null) {

			this.executorService = Executors.newFixedThreadPool(this.getThreads().intValue());
		} else {

			this.executorService = Executors.newCachedThreadPool();
		}

		// listen

		if (log.isInfoEnabled()) log.info("Listening for datagram packets.");

		byte[] bytes = new byte[this.getBufferSize()];
		final DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
		this.datagramSocket.receive(datagramPacket);

		// spawn thread to handle the request

		this.executorService.execute(new Executor(datagramPacket));
	}

	@Override
	public void shutdown() throws Exception {

		// shutdown current requests

		this.executorService.shutdown();
		this.executorService.awaitTermination(-1, null);

		// close socket

		this.inetSocketAddress = null;

		try {

			if (this.datagramSocket != null) {

				if (this.datagramSocket.isConnected()) this.datagramSocket.disconnect();
				if (! this.datagramSocket.isClosed()) this.datagramSocket.close();
			}
		} catch (Exception ex) {

			log.error("Cannot disconnect: " + ex.getMessage(), ex);
		} finally {

			this.datagramSocket = null;
		}
	}

	@Override
	public void execute(UDPTransportRequest request, UDPTransportResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming message: " + request.getDatagramPacket().getLength() + " to port " + request.getDatagramPacket().getPort());

		try {

			MessagingTarget messagingTarget = this.getMessagingTarget();

			this.processDatagram(request, response, messagingTarget);
		} catch (IOException ex) {

			throw ex;
		} catch (Exception ex) {

			log.error("Problem while processing datagram: " + ex.getMessage(), ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed message.");
	}

	protected void processDatagram(UDPTransportRequest request, UDPTransportResponse response, MessagingTarget messagingTarget) throws Xdi2TransportException, IOException {

		MessageEnvelope messageEnvelope;
		TransportMessagingResponse messagingResponse;

		// execute interceptors

		boolean result = InterceptorExecutor.executeUDPTransportInterceptorsDatagram(this.getInterceptors(), this, request, response, messagingTarget);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (GET).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			log.error("No messaging target.");
			return;
		}

		// construct message envelope from reader

		try {

			messageEnvelope = read(request);
			if (messageEnvelope == null) return;
		} catch (IOException ex) {

			throw new Xdi2TransportException("Invalid message envelope: " + ex.getMessage(), ex);
		}

		// execute the message envelope against our message target, save result

		messagingResponse = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messagingResponse == null || messagingResponse.getGraph() == null) return;

		// done

		sendDatagram(request, response, messagingResponse);
	}

	/*
	 * Helper methods
	 */

	private static MessageEnvelope read(UDPTransportRequest request) throws IOException {

		InputStream inputStream = new ByteArrayInputStream(request.getDatagramPacket().getData());

		// use the default reader

		XDIReader xdiReader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		if (log.isDebugEnabled()) log.debug("Reading datagram with reader " + xdiReader.getClass().getSimpleName() + ".");

		MessageEnvelope messageEnvelope;

		try {

			Graph graph = MemoryGraphFactory.getInstance().openGraph();

			xdiReader.read(graph, inputStream);
			messageEnvelope = MessageEnvelope.fromGraph(graph);
		} catch (IOException ex) {

			throw ex;
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			throw new IOException("Cannot parse XDI graph: " + ex.getMessage(), ex);
		} finally {

			inputStream.close();
		}

		if (log.isDebugEnabled()) log.debug("Message envelope received (" + messageEnvelope.getMessageCount() + " messages). Executing...");

		// done

		return messageEnvelope;
	}

	private static void sendDatagram(UDPTransportRequest request, UDPTransportResponse response, TransportMessagingResponse messagingResponse) throws IOException {

		// use default writer

		XDIWriter writer = null;

		MimeType sendMimeType = null;
		writer = sendMimeType != null ? XDIWriterRegistry.forMimeType(sendMimeType) : null;

		if (writer == null) writer = XDIWriterRegistry.getDefault();

		// send out the message result

		if (log.isDebugEnabled()) log.debug("Sending result in " + sendMimeType + " with writer " + writer.getClass().getSimpleName() + ".");

		StringWriter buffer = new StringWriter();
		writer.write(messagingResponse.getGraph(), buffer);

		byte[] bytes = buffer.getBuffer().toString().getBytes("UTF-8");
		final DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
		response.getDatagramSocket().send(datagramPacket);

		if (log.isDebugEnabled()) log.debug("Output complete.");
	}

	/*
	 * Executor class
	 */

	private class Executor implements Runnable {

		private DatagramPacket datagramPacket;

		private Executor(DatagramPacket datagramPacket) {

			this.datagramPacket = datagramPacket;
		}

		@Override
		public void run() {

			// execute the transport

			UDPTransportRequest request = UDPTransportRequest.create(this.datagramPacket);
			UDPTransportResponse response = UDPTransportResponse.create(UDPTransport.this.datagramSocket);

			try {

				UDPTransport.this.execute(request, response);
			} catch (Exception ex) {

				log.error("Execution error exception: " + ex.getMessage(), ex);
			}
		}
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

	public int getBufferSize() {

		return this.bufferSize;
	}

	public void setBufferSize(int bufferSize) {

		this.bufferSize = bufferSize;
	}

	public Integer getThreads() {

		return this.threads;
	}

	public void setThreads(Integer threads) {

		this.threads = threads;
	}

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}

	public void setMessagingTarget(MessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
	}
}
