package xdi2.discovery;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.MessageResult;

public class XDIDiscoveryResult implements Serializable {

	private static final long serialVersionUID = -1141807747864855392L;

	private String xri;
	private MessageResult messageResult;
	private String cloudNumber;
	private String endpointUri;

	private XDIDiscoveryResult(String xri, MessageResult messageResult, String cloudNumber, String endpointUri) {

		this.xri = xri;
		this.messageResult = messageResult;
		this.cloudNumber = cloudNumber;
		this.endpointUri = endpointUri;
	}

	/**
	 * Parses a XDIDiscoveryResult from an XDI2 message result.
	 * @return The XDIDiscoveryResult.
	 */
	public static XDIDiscoveryResult fromXriAndMessageResult(String xri, MessageResult messageResult) {

		Graph graph = messageResult.getGraph();

		// find Cloud Number

		ContextNode contextNode = graph.getDeepContextNode(XDI3Segment.create(xri));
		ContextNode referenceContextNode = contextNode == null ? null : Equivalence.getReferenceContextNode(contextNode);

		String cloudNumber = referenceContextNode == null ? null : referenceContextNode.getXri().toString();

		// find URI

		ContextNode endpointUriContextNode;

		if (cloudNumber != null) {

			endpointUriContextNode = graph.getDeepContextNode(XDI3Segment.create("(" + cloudNumber + ")" + "$xdi<$uri>"));
		} else {

			endpointUriContextNode = graph.getDeepContextNode(XDI3Segment.create("(" + xri + ")" + "$xdi<$uri>"));
		}

		ContextNode referenceEndpointUriContextNode = Equivalence.getReferenceContextNode(endpointUriContextNode);
		XdiValue endpointUriXdiValue = null;

		if (referenceEndpointUriContextNode != null) endpointUriXdiValue = XdiAbstractAttribute.fromContextNode(referenceEndpointUriContextNode).getXdiValue(false);
		else if (endpointUriContextNode != null) endpointUriXdiValue = XdiAbstractAttribute.fromContextNode(endpointUriContextNode).getXdiValue(false);

		String endpointUri = endpointUriXdiValue == null ? null : endpointUriXdiValue.getContextNode().getLiteral().getLiteralData();

		// done

		return new XDIDiscoveryResult(xri, messageResult, cloudNumber, endpointUri);
	}

	public String getXri() {

		return this.xri;
	}

	public MessageResult getMessageResult() {

		return this.messageResult;
	}

	public String getCloudNumber() {

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
