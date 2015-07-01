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

	public XDILocalClient(MessagingTarget messagingTarget) {

		super();

		this.messagingTarget = messagingTarget;
	}

	public XDILocalClient(Graph graph) {

		try {

			GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
			messagingTarget.setGraph(graph);
			messagingTarget.init();

			this.messagingTarget = messagingTarget;
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize messaging target: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void close() {

		try {

			this.messagingTarget.shutdown();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	@Override
	protected MessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// use the local transport

		LocalTransportRequest request = new LocalTransportRequest(messageEnvelope);
		LocalTransportResponse response = new LocalTransportResponse();

		try {

			new LocalTransport(this.getMessagingTarget()).execute(request, response);
		} catch (Xdi2TransportException ex) {

			throw new Xdi2ClientException("Unable to send message envelope to local messaging target: " + ex.getMessage(), ex);
		}

		MessagingResponse messagingResponse = response.getMessagingResponse();

		// done

		return messagingResponse;
	}

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}
}
