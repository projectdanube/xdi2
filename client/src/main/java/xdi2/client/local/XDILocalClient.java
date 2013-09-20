package xdi2.client.local;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIAbstractClient;
import xdi2.client.XDIClient;
import xdi2.client.events.XDISendErrorEvent;
import xdi2.client.events.XDISendSuccessEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

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
	public MessageResult send(MessageEnvelope messageEnvelope, MessageResult messageResult) throws Xdi2ClientException {

		if (messageResult == null) messageResult = new MessageResult();

		// timestamp

		Date beginTimestamp = new Date();

		// create an execution context

		ExecutionContext executionContext = new ExecutionContext();

		// send the message envelope

		try {

			if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));
			this.messagingTarget.execute(messageEnvelope, messageResult, executionContext);
			if (log.isDebugEnabled()) log.debug("MessageResult: " + messageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));

			// timestamp

			Date endTimestamp = new Date();

			// done

			this.fireSendEvent(new XDISendSuccessEvent(this, messageEnvelope, messageResult, beginTimestamp, endTimestamp));

			return messageResult;
		} catch (Exception ex) {

			// timestamp

			Date endTimestamp = new Date();

			// done

			ErrorMessageResult errorMessageResult = ErrorMessageResult.fromException(ex);

			log.warn("Error message result: " + errorMessageResult.getErrorString());

			this.fireSendEvent(new XDISendErrorEvent(this, messageEnvelope, errorMessageResult, beginTimestamp, endTimestamp));

			throw new Xdi2ClientException("Error message result: " + errorMessageResult.getErrorString(), ex, errorMessageResult);
		}
	}

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}
}
