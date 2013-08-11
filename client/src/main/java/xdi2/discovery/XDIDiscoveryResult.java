package xdi2.discovery;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.MessageResult;

public class XDIDiscoveryResult implements Serializable {

	private static final long serialVersionUID = -1141807747864855392L;

	private XDI3Segment xri;
	private MessageResult messageResult;
	private XDI3Segment cloudNumber;
	private String endpointUri;

	private XDIDiscoveryResult(XDI3Segment xri, MessageResult messageResult, XDI3Segment cloudNumber, String endpointUri) {

		this.xri = xri;
		this.messageResult = messageResult;
		this.cloudNumber = cloudNumber;
		this.endpointUri = endpointUri;
	}

	/**
	 * Parses a XDIDiscoveryResult from an XDI2 message result.
	 * @return The XDIDiscoveryResult.
	 */
	public static XDIDiscoveryResult fromXriAndMessageResult(XDI3Segment xri, MessageResult messageResult) {

		Graph graph = messageResult.getGraph();

		XDI3SubSegment peerRootArcXri = XdiPeerRoot.createPeerRootArcXri(xri);

		// find Cloud Number

		ContextNode contextNode = graph.getDeepContextNode(XDI3Segment.create(peerRootArcXri));
		ContextNode referenceContextNode = contextNode == null ? null : Equivalence.getReferenceContextNode(contextNode);

		XDI3SubSegment cloudNumberPeerRootArcXri = referenceContextNode == null ? null : referenceContextNode.getXri().getFirstSubSegment();
		XDI3Segment cloudNumber = cloudNumberPeerRootArcXri == null ? null : XdiPeerRoot.getXriOfPeerRootArcXri(cloudNumberPeerRootArcXri);

		// find URI

		ContextNode endpointUriContextNode;

		if (cloudNumberPeerRootArcXri != null) {

			endpointUriContextNode = graph.getDeepContextNode(XDI3Segment.create(cloudNumberPeerRootArcXri + "$xdi<$uri>"));
		} else {

			endpointUriContextNode = graph.getDeepContextNode(XDI3Segment.create(peerRootArcXri + "$xdi<$uri>"));
		}

		ContextNode referenceEndpointUriContextNode = endpointUriContextNode == null ? null : Equivalence.getReferenceContextNode(endpointUriContextNode);
		XdiValue endpointUriXdiValue = null;

		if (referenceEndpointUriContextNode != null) endpointUriXdiValue = XdiAbstractAttribute.fromContextNode(referenceEndpointUriContextNode).getXdiValue(false);
		else if (endpointUriContextNode != null) endpointUriXdiValue = XdiAbstractAttribute.fromContextNode(endpointUriContextNode).getXdiValue(false);

		Literal endpointUriLiteral = endpointUriXdiValue == null ? null : endpointUriXdiValue.getContextNode().getLiteral();
		String endpointUri = endpointUriLiteral == null ? null : endpointUriLiteral.getLiteralDataString();

		// done

		return new XDIDiscoveryResult(xri, messageResult, cloudNumber, endpointUri);
	}

	public XDI3Segment getXri() {

		return this.xri;
	}

	public MessageResult getMessageResult() {

		return this.messageResult;
	}

	public XDI3Segment getCloudNumber() {

		return this.cloudNumber;
	}

	public String getEndpointUri() {

		return this.endpointUri;
	}

	@Override
	public String toString() {

		return this.cloudNumber + " (" + this.endpointUri + ")";
	}
}
