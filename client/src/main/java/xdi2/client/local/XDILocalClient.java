package xdi2.client.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * An XDI client that can apply XDI messages locally on Graph objects.
 * This is done with a GraphMessagingTarget that handles the XDI messages.
 * 
 * @author markus
 */
public class XDILocalClient implements XDIClient {

	protected static final Logger log = LoggerFactory.getLogger(XDILocalClient.class);

	private GraphMessagingTarget messagingTarget;

	public XDILocalClient(Graph graph) {

		try {

			this.messagingTarget = new GraphMessagingTarget();
			this.messagingTarget.setGraph(graph);
			this.messagingTarget.init();
		} catch (Exception ex) {

			throw new RuntimeException("Cannot initialize messaging target: " + ex.getMessage(), ex);
		}
	}

	public void close() {

		try {

			this.messagingTarget.shutdown();
		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}

	public MessageResult send(MessageEnvelope messageEnvelope, MessageResult messageResult) throws Xdi2MessagingException {

		if (messageResult == null) messageResult = MessageResult.newInstance();

		if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));
		this.messagingTarget.execute(messageEnvelope, messageResult, new ExecutionContext());
		if (log.isDebugEnabled()) log.debug("MessageResult: " + messageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));

		return messageResult;
	}

	public GraphMessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}

	public void setMessagingTarget(GraphMessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
	}
}
