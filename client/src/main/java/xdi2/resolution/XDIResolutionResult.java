package xdi2.resolution;

import java.io.Serializable;

import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.messaging.MessageResult;

public class XDIResolutionResult implements Serializable {

	private static final long serialVersionUID = -1141807747864855392L;

	private String xri;
	private MessageResult messageResult;
	private String inumber;
	private String uri;

	private XDIResolutionResult(String xri, MessageResult messageResult, String inumber, String uri) {

		this.xri = xri;
		this.messageResult = messageResult;
		this.inumber = inumber;
		this.uri = uri;
	}

	/**
	 * Parses a XdiResolutionResult from an XDI^2 message result.
	 * @return The XdiResolutionResult.
	 */
	public static XDIResolutionResult fromXriAndMessageResult(String xri, MessageResult messageResult) {

		Graph graph = messageResult.getGraph();

		// find I-Number

		String inumber;

		Relation relation = graph.findRelation(new XDI3Segment(xri), new XDI3Segment("$is"));		

		if (relation != null && isInumber(relation.getTargetContextNodeXri().toString())) { 

			inumber = relation.getTargetContextNodeXri().toString();
		} else if (isInumber(xri)) {

			inumber = xri;
		} else {

			inumber = null;
		}

		// find URI

		String uri;

		Literal literal;

		if (inumber != null) {

			literal = graph.findLiteral(new XDI3Segment("(" + inumber + ")" + "$!($uri)"));
		} else {

			literal = graph.findLiteral(new XDI3Segment("(" + xri + ")" + "$!($uri)"));
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

	public String getInumber() {

		return this.inumber;
	}

	public String getUri() {

		return this.uri;
	}

	@Override
	public String toString() {

		return this.inumber + " / " + this.uri;
	}

	private static boolean isInumber(String xri) {

		return new XRI3(xri).isINumber();
	}
}
