package xdi2.client.impl.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.local.LocalTransport;
import xdi2.transport.impl.local.LocalTransportRequest;
import xdi2.transport.impl.local.LocalTransportResponse;

/**
 * An XDI client that can apply XDI messages locally on messaging targets.
 * 
 * @author markus
 */
public class XDILocalClient extends XDIAbstractClient implements XDIClient {

	protected static final Logger log = LoggerFactory.getLogger(XDILocalClient.class);

	private MessagingTarget messagingTarget;
	private Graph graph;

	public XDILocalClient(MessagingTarget messagingTarget, Graph graph) {

		super();

		this.messagingTarget = messagingTarget;
		this.graph = graph;
	}

	public XDILocalClient(MessagingTarget messagingTarget) {

		this(messagingTarget, null);
	}

	public XDILocalClient(Graph graph) {

		this(null, graph);
	}

	@Override
	public void close() {

		this.disconnect();
	}

	private MessagingTarget connect() throws Exception {

		if (this.getMessagingTarget() != null) return this.getMessagingTarget();

		if (this.getGraph() == null) throw new Xdi2ClientException("No graph to connect to.");

		// connect

		if (log.isDebugEnabled()) log.debug("Connecting to " + this.getGraph().getClass().getSimpleName());

		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(this.getGraph());
		messagingTarget.init();

		this.messagingTarget = messagingTarget;

		// done

		this.setMessagingTarget(messagingTarget);
		return messagingTarget;
	}

	private void disconnect() {

		try {

			if (this.getMessagingTarget() != null) {

				this.getMessagingTarget().shutdown();
			}
		} catch (Exception ex) {

			log.error("Cannot disconnect: " + ex.getMessage(), ex);
		} finally {

			this.setMessagingTarget(null);
		}
	}

	@Override
	protected MessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// connect

		MessagingTarget messagingTarget;

		try {

			messagingTarget = this.connect();
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot open messaging target: " + ex.getMessage(), ex);
		}

		// execute the transport

		LocalTransportRequest request = new LocalTransportRequest(messageEnvelope);
		LocalTransportResponse response = new LocalTransportResponse();

		try {

			new LocalTransport(messagingTarget).execute(request, response);
		} catch (Xdi2TransportException ex) {

			throw new Xdi2ClientException("Unable to send message envelope to local messaging target: " + ex.getMessage(), ex);
		}

		MessagingResponse messagingResponse = response.getMessagingResponse();

		// done

		return messagingResponse;
	}

	/*
	 * Getters and setters
	 */

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}

	public void setMessagingTarget(MessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
	}

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
	}
}
