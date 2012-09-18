package xdi2.client.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
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
public class XDILocalClient implements XDIClient {

	protected static final Logger log = LoggerFactory.getLogger(XDILocalClient.class);

	private MessagingTarget messagingTarget;

	public XDILocalClient(MessagingTarget messagingTarget) {

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

		// create an execution context

		ExecutionContext executionContext = new ExecutionContext();

		// send the message envelope

		try {

			if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));
			this.messagingTarget.execute(messageEnvelope, messageResult, executionContext);
			if (log.isDebugEnabled()) log.debug("MessageResult: " + messageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));
		} catch (Exception ex) {

			log.error("Exception: " + ex.getMessage(), ex);
			throw new Xdi2ClientException("Cannot execute message envelope: " + ex.getMessage(), ex, ErrorMessageResult.fromException(ex));
		}

		// done

		log.debug("Successfully received result, " + messageResult.getGraph().getRootContextNode().getAllStatementCount() + " result statements.");

		return messageResult;
	}

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}
}
