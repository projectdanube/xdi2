package xdi2.messaging.target.contributor.impl.proxy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.client.local.XDILocalClient;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;
import xdi2.messaging.target.contributor.impl.proxy.manipulator.ProxyManipulator;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.transport.Transport;
import xdi2.messaging.util.MessagingCloneUtil;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.MessagingTargetMount;
import xdi2.server.transport.HttpTransport;

/**
 * This contributor can answer requests by forwarding them to another XDI endpoint.
 */
@ContributorXri(addresses={""})
public class ProxyContributor extends AbstractContributor implements MessageInterceptor, Prototype<ProxyContributor> {

	private static final Logger log = LoggerFactory.getLogger(ProxyContributor.class);

	private XDI3Segment toPeerRootXri;
	private XDIClient xdiClient;

	private XDIDiscoveryClient xdiDiscoveryClient;

	private List<ProxyManipulator> proxyManipulators;

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

		// if we don't have an XDI discovery client, use the default one

		if (this.xdiDiscoveryClient == null) {

			this.xdiDiscoveryClient = new XDIDiscoveryClient();
		}

		// if we have a static forwarding target, but no XDI client, use XDI discovery to create one

		if (this.toPeerRootXri != null && this.xdiClient == null) {

			XDIDiscoveryResult xdiDiscoveryResult = this.getXdiDiscoveryClient().discoverFromRegistry(XdiPeerRoot.getXriOfPeerRootArcXri(this.toPeerRootXri.getFirstSubSegment()), null);

			if (xdiDiscoveryResult.getXdiEndpointUri() == null) throw new RuntimeException("Could not discover XDI endpoint URI for " + this.toPeerRootXri);

			this.xdiClient = new XDIHttpClient(xdiDiscoveryResult.getXdiEndpointUri());
		}
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// if there is a static forwarding target, we use it

		if (this.getToPeerRootXri() != null && this.getXdiClient() != null) {

			XDI3Segment staticForwardingTargetToPeerRootXri = this.getToPeerRootXri();
			XDIClient staticForwardingTargetXdiClient = this.getXdiClient();

			if (log.isDebugEnabled()) log.debug("Setting static forwarding target: " + staticForwardingTargetToPeerRootXri + " (" + staticForwardingTargetXdiClient + ")");

			putToPeerRootXri(executionContext, this.getToPeerRootXri());
			putXdiClient(executionContext, this.getXdiClient());

			return false;
		}

		// no static forwarding target, so we check if the target is self

		MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();
		XDI3Segment ownerPeerRootXri = messagingTarget.getOwnerPeerRootXri();
		XDI3Segment toPeerRootXri = message.getToPeerRootXri();

		if (toPeerRootXri == null || toPeerRootXri.equals(ownerPeerRootXri)) {

			if (log.isDebugEnabled()) log.debug("Not setting any forwarding target for self request to " + ownerPeerRootXri);

			return false;
		}

		// no static forwarding target, and target is not self, so we check if the target is local

		Transport<?, ?> transport = executionContext.getTransport();

		if (transport instanceof HttpTransport) {

			HttpTransport httpTransport = (HttpTransport) transport;

			MessagingTargetMount messagingTargetMount;

			try {

				messagingTargetMount = httpTransport.getHttpMessagingTargetRegistry().lookup(toPeerRootXri);
			} catch (Xdi2ServerException ex) {

				throw new Xdi2MessagingException("Unable to locally look up messaging target for peer root XRI " + toPeerRootXri, ex, executionContext);
			}

			if (messagingTargetMount != null) {

				XDI3Segment dynamicForwardingTargetToPeerRootXri = toPeerRootXri;
				XDIClient dynamicForwardingTargetXdiClient = new XDILocalClient(messagingTargetMount.getMessagingTarget());

				if (log.isDebugEnabled()) log.debug("Setting dynamic local forwarding target: " + dynamicForwardingTargetToPeerRootXri + " (" + dynamicForwardingTargetXdiClient + ")");

				putToPeerRootXri(executionContext, dynamicForwardingTargetToPeerRootXri);
				putXdiClient(executionContext, dynamicForwardingTargetXdiClient);

				return false;
			}
		}

		// no static forwarding target, and target is not self, and target is not local, so we discover the remote forwarding target dynamically

		XDIDiscoveryResult xdiDiscoveryResult;

		try {

			xdiDiscoveryResult = this.getXdiDiscoveryClient().discoverFromRegistry(XdiPeerRoot.getXriOfPeerRootArcXri(toPeerRootXri.getFirstSubSegment()), null);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("XDI Discovery failed on " + toPeerRootXri + ": " + ex.getMessage(), ex, executionContext);
		}

		if (xdiDiscoveryResult.getCloudNumber() == null) throw new Xdi2MessagingException("Could not discover Cloud Number for forwarding target at " + toPeerRootXri, null, executionContext);
		if (xdiDiscoveryResult.getXdiEndpointUri() == null) throw new Xdi2MessagingException("Could not discover XDI endpoint URI for forwarding target at " + toPeerRootXri, null, executionContext);

		XDI3Segment dynamicForwardingTargetToPeerRootXri = toPeerRootXri;
		XDIClient dynamicForwardingTargetXdiClient = new XDIHttpClient(xdiDiscoveryResult.getXdiEndpointUri());

		if (log.isDebugEnabled()) log.debug("Setting dynamic remote forwarding target: " + dynamicForwardingTargetToPeerRootXri + " (" + dynamicForwardingTargetXdiClient + ")");

		putToPeerRootXri(executionContext, dynamicForwardingTargetToPeerRootXri);
		putXdiClient(executionContext, dynamicForwardingTargetXdiClient);

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	/*
	 * Contributor methods
	 */

	@Override
	public boolean executeOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check forwarding target

		XDI3Segment toPeerRootXri = getToPeerRootXri(executionContext);
		XDIClient xdiClient = getXdiClient(executionContext);

		if (toPeerRootXri == null || xdiClient == null) return false;

		// prepare the forwarding message envelope

		XDI3Segment targetAddress = XDI3Util.concatXris(contributorsXri, relativeTargetAddress);

		Message forwardingMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());

		forwardingMessage.setToPeerRootXri(toPeerRootXri);

		forwardingMessage.deleteOperations();
		forwardingMessage.createOperation(operation.getOperationXri(), targetAddress);

		MessageEnvelope forwardingMessageEnvelope = forwardingMessage.getMessageEnvelope();

		// manipulate the forwarding message envelope

		for (ProxyManipulator proxyManipulator : this.proxyManipulators) {

			if (log.isDebugEnabled()) log.debug("Executing proxy manipulator " + proxyManipulator.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on address " + targetAddress + " (message envelope).");

			proxyManipulator.manipulate(forwardingMessageEnvelope, executionContext);
		}

		// prepare the forwarding message result

		MessageResult forwardingMessageResult = new MessageResult();

		// send the forwarding message envelope

		try {

			if (log.isDebugEnabled() && this.getXdiClient() instanceof XDIHttpClient) log.debug("Forwarding operation " + operation.getOperationXri() + " on target address " + targetAddress + " to " + ((XDIHttpClient) this.getXdiClient()).getEndpointUri() + ".");

			xdiClient.send(forwardingMessageEnvelope, forwardingMessageResult);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while forwarding XDI request: " + ex.getMessage(), ex, executionContext);
		}

		// manipulate the forwarding message result

		for (ProxyManipulator proxyManipulator : this.proxyManipulators) {

			if (log.isDebugEnabled()) log.debug("Executing proxy manipulator " + proxyManipulator.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on address " + targetAddress + " (message result).");

			proxyManipulator.manipulate(forwardingMessageResult, executionContext);
		}

		// done

		CopyUtil.copyGraph(forwardingMessageResult.getGraph(), messageResult.getGraph(), null);

		return true;
	}

	@Override
	public boolean executeOnStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check forwarding target

		XDI3Segment toPeerRootXri = getToPeerRootXri(executionContext);
		XDIClient xdiClient = getXdiClient(executionContext);

		if (toPeerRootXri == null || xdiClient == null) return false;

		// prepare the forwarding message envelope

		XDI3Statement targetStatement = StatementUtil.concatXriStatement(contributorsXri, relativeTargetStatement, true);

		Message forwardingMessage = MessagingCloneUtil.cloneMessage(operation.getMessage());

		forwardingMessage.setToPeerRootXri(toPeerRootXri);

		forwardingMessage.deleteOperations();
		forwardingMessage.createOperation(operation.getOperationXri(), targetStatement);

		MessageEnvelope forwardingMessageEnvelope = forwardingMessage.getMessageEnvelope();

		// manipulate the forwarding message envelope

		for (ProxyManipulator proxyManipulator : this.proxyManipulators) {

			if (log.isDebugEnabled()) log.debug("Executing message result manipulator " + proxyManipulator.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on statement " + targetStatement + " (message envelope).");

			proxyManipulator.manipulate(forwardingMessageEnvelope, executionContext);
		}

		// prepare the forwarding message result

		MessageResult forwardingMessageResult = new MessageResult();

		// send the forwarding message envelope

		try {

			if (log.isDebugEnabled() && this.getXdiClient() instanceof XDIHttpClient) log.debug("Forwarding operation " + operation.getOperationXri() + " on target statement " + targetStatement + " to " + ((XDIHttpClient) this.getXdiClient()).getEndpointUri() + ".");

			xdiClient.send(forwardingMessageEnvelope, forwardingMessageResult);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while forwarding XDI request: " + ex.getMessage(), ex, executionContext);
		}

		// manipulate the forwarding message result

		for (ProxyManipulator proxyManipulator : this.proxyManipulators) {

			if (log.isDebugEnabled()) log.debug("Executing message result manipulator " + proxyManipulator.getClass().getSimpleName() + " with operation " + operation.getOperationXri() + " on statement " + targetStatement + " (message result).");

			proxyManipulator.manipulate(forwardingMessageResult, executionContext);
		}

		// done

		CopyUtil.copyGraph(forwardingMessageResult.getGraph(), messageResult.getGraph(), null);

		return true;
	}

	/*
	 * Getters and setters
	 */

	public XDI3Segment getToPeerRootXri() {

		return this.toPeerRootXri;
	}

	public void setToPeerRootXri(XDI3Segment toPeerRootXri) {

		this.toPeerRootXri = toPeerRootXri;
	}

	public XDIClient getXdiClient() {

		return this.xdiClient;
	}

	public void setXdiClient(XDIClient xdiClient) {

		this.xdiClient = xdiClient;
	}

	public XDIDiscoveryClient getXdiDiscoveryClient() {

		return this.xdiDiscoveryClient;
	}

	public void setXdiDiscoveryClient(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_TO_PEER_ROOT_XRI_PER_MESSAGE = ProxyContributor.class.getCanonicalName() + "#topeerrootxripermessage";
	private static final String EXECUTIONCONTEXT_KEY_XDI_CLIENT_PER_MESSAGE = ProxyContributor.class.getCanonicalName() + "#xdiclientpermessage";

	public static XDI3Segment getToPeerRootXri(ExecutionContext executionContext) {

		return (XDI3Segment) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_TO_PEER_ROOT_XRI_PER_MESSAGE);
	}

	public static void putToPeerRootXri(ExecutionContext executionContext, XDI3Segment toPeerRootXri) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_TO_PEER_ROOT_XRI_PER_MESSAGE, toPeerRootXri);
	}

	public static XDIClient getXdiClient(ExecutionContext executionContext) {

		return (XDIClient) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_XDI_CLIENT_PER_MESSAGE);
	}

	public static void putXdiClient(ExecutionContext executionContext, XDIClient xdiClient) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_XDI_CLIENT_PER_MESSAGE, xdiClient);
	}
}
