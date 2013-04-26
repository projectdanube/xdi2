package xdi2.discovery;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.MessageResult;

public class XDIResolutionResult implements Serializable {

	private static final long serialVersionUID = -1141807747864855392L;

	private String xri;
	private MessageResult messageResult;
	private String cloudnumber;
	private String endpointUri;

	private XDIResolutionResult(String xri, MessageResult messageResult, String cloudnumber, String endpointUri) {

		this.xri = xri;
		this.messageResult = messageResult;
		this.cloudnumber = cloudnumber;
		this.endpointUri = endpointUri;
	}

	/**
	 * Parses a XdiResolutionResult from an XDI2 message result.
	 * @return The XdiResolutionResult.
	 */
	public static XDIResolutionResult fromXriAndMessageResult(String xri, MessageResult messageResult) {

		Graph graph = messageResult.getGraph();

		// find I-Number

		ContextNode contextNode = graph.getDeepContextNode(XDI3Segment.create(xri));
		ContextNode referenceContextNode = contextNode == null ? null : Equivalence.getReferenceContextNode(contextNode);
		
		String inumber = referenceContextNode == null ? null : referenceContextNode.getXri().toString();

		// find URI

		String uri;

		Literal literal;

		if (inumber != null) {

			literal = graph.getDeepLiteral(XDI3Segment.create("(" + inumber + ")" + "$!($uri)"));
		} else {

			literal = graph.getDeepLiteral(XDI3Segment.create("(" + xri + ")" + "$!($uri)"));
		}

		if (literal != null) {

			uri = literal.getLiteralData();
		} else {

			uri = null;
		}

		// done

		return new XDIResolutionResult(xri, messageResult, inumber, uri);
	}

	public String getXri() {

		return this.xri;
	}

	public MessageResult getMessageResult() {

		return this.messageResult;
	}

	public String getCloudnumber() {

		return this.cloudnumber;
	}

	public String getEndpointUri() {

		return this.endpointUri;
	}

	@Override
	public String toString() {

		return this.cloudnumber + " (" + this.endpointUri + ")";
	}
}
