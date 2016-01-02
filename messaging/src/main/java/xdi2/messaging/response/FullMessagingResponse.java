package xdi2.messaging.response;

import java.util.Iterator;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContracts;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

/**
 * A message envelope as an XDI messaging response.
 * 
 * @author markus
 */
public class FullMessagingResponse extends TransportMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -150908814464607155L;

	private MessageEnvelope messageEnvelope;
	private Graph resultGraph;

	private FullMessagingResponse(MessageEnvelope messageEnvelope, Graph resultGraph) {

		this.messageEnvelope = messageEnvelope;
		this.resultGraph = resultGraph;
	}

	/*
	 * Static methods
	 */

	public static boolean isValid(Graph graph) {

		MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(graph);
		if (messageEnvelope == null) return false;

		if (! messageEnvelope.getMessages().hasNext()) return false;

		Message message = messageEnvelope.getMessages().next();

		if (message.getOperationsContextNode() == null) return false;
		if (message.getFromPeerRootXDIArc() == null) return false;
		if (message.getToPeerRootXDIArc() == null) return false;

		return true;
	}

	public static FullMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return fromMessageEnvelope(MessageEnvelope.fromGraph(graph));
	}

	public static FullMessagingResponse fromMessageEnvelope(MessageEnvelope messageEnvelope) {

		Graph resultGraph = MemoryGraphFactory.getInstance().openGraph();

		for (XdiInnerRoot xdiInnerRoot : messageEnvelope.getOperationResults()) {

			CopyUtil.copyContextNodeContents(xdiInnerRoot.getContextNode(), resultGraph, null);
		}

		FullMessagingResponse messageEnvelopeMessagingResponse = new FullMessagingResponse(messageEnvelope, resultGraph);

		return messageEnvelopeMessagingResponse;
	}

	/*
	 * Overrides
	 */

	@Override
	public Graph getGraph() {

		return this.getMessageEnvelope().getGraph();
	}

	@Override
	public Graph getResultGraph() {

		return this.resultGraph;
	}

	/*
	 * Instance methods
	 */

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	/*
	 * Helper methods
	 */

	public static ReadOnlyIterator<LinkContract> getDeferredPushLinkContracts(MessagingResponse messagingResponse) {

		if (! (messagingResponse instanceof FullMessagingResponse)) return new EmptyIterator<LinkContract> ();

		return new DescendingIterator<Message, LinkContract> (((FullMessagingResponse) messagingResponse).getMessageEnvelope().getMessages()) {

			@Override
			public Iterator<LinkContract> descend(Message message) {

				XdiInnerRoot messageDeferredPushResult = message.getMessageDeferredPushResult();
				if (messageDeferredPushResult == null) return new EmptyIterator<LinkContract> ();

				// TODO: fix this, not all link contracts are push link contracts
				// maybe also need strict criteria, e.g. only return contract from/to the correct peers
				// TODO: and also only for responses to the requested operation, rather than ALL push contracts?
				// TODO: and don't get the nested ones, in case we have a response to $send
				return new SelectingIterator<LinkContract> (LinkContracts.getAllLinkContracts(messageDeferredPushResult.getInnerGraph())) {

					@Override
					public boolean select(LinkContract linkContract) {

						if (! (linkContract instanceof GenericLinkContract)) return false;
						if (! (((GenericLinkContract) linkContract).getXdiInnerRoot().getXdiContext() instanceof XdiCommonRoot)) return false;

						if (linkContract.getPermissionTargetXDIAddresses(XDILinkContractConstants.XDI_ADD_PUSH).hasNext()) return true;
						if (linkContract.getPermissionTargetXDIStatements(XDILinkContractConstants.XDI_ADD_PUSH).hasNext()) return true;

						return false;
					}
				};
			}
		};
	}

	public static boolean hasDeferredPushLinkContracts(MessagingResponse messagingResponse) {

		return getDeferredPushLinkContracts(messagingResponse).hasNext();
	}
}
