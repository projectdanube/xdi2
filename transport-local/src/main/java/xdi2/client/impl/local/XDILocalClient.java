package xdi2.client.impl.local;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIAbstractClient;
import xdi2.client.XDIClient;
import xdi2.client.events.XDISendErrorEvent;
import xdi2.client.events.XDISendSuccessEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.ErrorMessagingResponse;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
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
	public MessagingResponse send(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// timestamp

		Date beginTimestamp = new Date();

		// send the messaging request and retrieve the messaging response

		try {

			LocalTransportRequest request = new LocalTransportRequest(messageEnvelope);
			LocalTransportResponse response = new LocalTransportResponse();

			new LocalTransport(this.getMessagingTarget()).execute(request, response);

			MessagingResponse messagingResponse = response.getMessagingResponse();

			// timestamp

			Date endTimestamp = new Date();

			// done

			this.fireSendEvent(new XDISendSuccessEvent(this, messageEnvelope, messagingResponse, beginTimestamp, endTimestamp));

			return messagingResponse;
		} catch (Exception ex) {

			// timestamp

			Date endTimestamp = new Date();

			// done

			ErrorMessagingResponse errorMessageResult = ErrorMessagingResponse.fromException(ex);

			log.warn("Error message result: " + errorMessageResult.getErrorString());

			this.fireSendEvent(new XDISendErrorEvent(this, messageEnvelope, errorMessageResult, beginTimestamp, endTimestamp));

			throw new Xdi2ClientException("Error message result: " + errorMessageResult.getErrorString(), ex, errorMessageResult);
		}
	}

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}
}
