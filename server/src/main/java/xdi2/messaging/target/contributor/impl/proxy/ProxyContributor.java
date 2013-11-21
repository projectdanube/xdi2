package xdi2.messaging.target.contributor.impl.proxy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This contributor can answer request by forwarding them to another XDI endpoint.
 */
@ContributorXri(addresses={"{{=@+*!$}}$keypair", "{{(=@+*!$)}}$keypair", "$keypair", "{{=@+*!$}}<$key>", "{{(=@+*!$)}}<$key>", "<$key>"})
public class ProxyContributor extends AbstractContributor implements Prototype<ProxyContributor> {

	private static final Logger log = LoggerFactory.getLogger(ProxyContributor.class);

	private XDIClient xdiClient;
	private List<MessageEnvelopeManipulator> messageEnvelopeManipulators;
	private List<MessageResultManipulator> messageResultManipulators;

	public ProxyContributor() {

	}

	/*
	 * Prototype
	 */

	@Override
	public ProxyContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// done

		return this;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);
	}

	/*
	 * Contributor methods
	 */

	@Override
	public boolean executeOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// prepare the proxy message envelope

		XDI3Segment targetAddress = XDI3Util.concatXris(contributorsXri, relativeTargetAddress);

		Message proxyMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());

		proxyMessage.deleteOperations();
		proxyMessage.createOperation(operation.getOperationXri(), targetAddress);

		MessageEnvelope proxyMessageEnvelope = proxyMessage.getMessageEnvelope();

		// manipulate the proxy message envelope

		for (MessageEnvelopeManipulator messageEnvelopeManipulator : this.messageEnvelopeManipulators) {

			messageEnvelopeManipulator.manipulate(proxyMessageEnvelope, executionContext);
		}

		// prepare the proxy message result

		MessageResult proxyMessageResult = new MessageResult();

		// send the proxy message envelope

		try {

			if (log.isDebugEnabled() && this.getXdiClient() instanceof XDIHttpClient) log.debug("Forwarding operation " + operation.getOperationXri() + " on target address " + targetAddress + " to " + ((XDIHttpClient) this.getXdiClient()).getEndpointUri() + ".");

			this.getXdiClient().send(proxyMessageEnvelope, proxyMessageResult);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while forwarding XDI request: " + ex.getMessage(), ex, executionContext);
		}

		// manipulate the proxy message result

		for (MessageResultManipulator messageResultManipulator : this.messageResultManipulators) {

			if (log.isDebugEnabled()) log.debug("Executing message result manipulator " + messageResultManipulator.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on address " + targetAddress + ".");

			messageResultManipulator.manipulate(proxyMessageResult, executionContext);
		}

		// done

		CopyUtil.copyGraph(proxyMessageResult.getGraph(), messageResult.getGraph(), null);

		return true;
	}

	@Override
	public boolean executeOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// prepare the proxy message envelope

		XDI3Statement targetStatement = StatementUtil.concatXriStatement(contributorsXri, relativeTargetStatement, true);

		Message proxyMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());

		proxyMessage.deleteOperations();
		proxyMessage.createOperation(operation.getOperationXri(), targetStatement);

		MessageEnvelope proxyMessageEnvelope = proxyMessage.getMessageEnvelope();

		// manipulate the proxy message envelope

		for (MessageEnvelopeManipulator messageEnvelopeManipulator : this.messageEnvelopeManipulators) {

			messageEnvelopeManipulator.manipulate(proxyMessageEnvelope, executionContext);
		}

		// prepare the proxy message result

		MessageResult proxyMessageResult = new MessageResult();

		// send the proxy message envelope

		try {

			if (log.isDebugEnabled() && this.getXdiClient() instanceof XDIHttpClient) log.debug("Forwarding operation " + operation.getOperationXri() + " on target statement " + targetStatement + " to " + ((XDIHttpClient) this.getXdiClient()).getEndpointUri() + ".");

			this.getXdiClient().send(proxyMessageEnvelope, proxyMessageResult);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while forwarding XDI request: " + ex.getMessage(), ex, executionContext);
		}

		// manipulate the proxy message result

		for (MessageResultManipulator messageResultManipulator : this.messageResultManipulators) {

			if (log.isDebugEnabled()) log.debug("Executing message result manipulator " + messageResultManipulator.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on statement " + targetStatement + ".");

			messageResultManipulator.manipulate(proxyMessageResult, executionContext);
		}

		// done

		CopyUtil.copyGraph(proxyMessageResult.getGraph(), messageResult.getGraph(), null);

		return true;
	}

	/*
	 * Getters and setters
	 */

	public XDIClient getXdiClient() {

		return this.xdiClient;
	}

	public void setXdiClient(XDIClient xdiClient) {

		this.xdiClient = xdiClient;
	}
}
