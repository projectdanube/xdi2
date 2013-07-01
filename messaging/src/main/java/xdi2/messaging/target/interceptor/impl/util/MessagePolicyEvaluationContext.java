package xdi2.messaging.target.interceptor.impl.util;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.features.linkcontracts.evaluation.GraphPolicyEvaluationContext;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;

public class MessagePolicyEvaluationContext extends GraphPolicyEvaluationContext {

	public static final XDI3Segment XRI_FROM = XDI3Segment.create("{$from}");
	public static final XDI3Segment XRI_MSG = XDI3Segment.create("{$msg}");

	private Message message;

	public MessagePolicyEvaluationContext(Graph graph, Message message) {

		super(graph);

		this.message = message;
	}

	@Override
	public XDI3Segment getContextNodeXri(XDI3Segment xri) {

		if (XDI3Util.startsWith(xri, XRI_MSG) != null) {

			XDI3Segment endXri = XDI3Util.removeStartXri(xri, XRI_MSG);

			return XDI3Util.concatXris(this.getMessage().getContextNode().getXri(), endXri);
		}

		if (XDI3Util.startsWith(xri, XRI_FROM) != null) {

			XDI3Segment endXri = XDI3Util.removeStartXri(xri, XRI_FROM);

			return XDI3Util.concatXris(this.getMessage().getSender(), endXri);
		}

		return super.getContextNodeXri(xri);
	}

	@Override
	public ContextNode getContextNode(XDI3Segment xri) {

		if (XDI3Util.startsWith(xri, XRI_MSG) != null) {

			XDI3Segment endXri = XDI3Util.removeStartXri(xri, XRI_MSG);

			ContextNode contextNode = this.getMessage().getContextNode();
			if (contextNode != null && endXri != null) contextNode = contextNode.getDeepContextNode(endXri);

			return contextNode;
		}

		if (XDI3Util.startsWith(xri, XRI_FROM) != null) {

			XDI3Segment endXri = XDI3Util.removeStartXri(xri, XRI_FROM);

			ContextNode contextNode = this.getGraph().getDeepContextNode(this.getMessage().getSender());
			if (contextNode != null && endXri != null) contextNode = contextNode.getDeepContextNode(endXri);

			return contextNode;
		}

		return super.getContextNode(xri);
	}

	@Override
	public Iterator<Relation> getRelations(XDI3Segment arcXri) {

		return this.getMessage().getOperationsContextNode().getRelations(arcXri);
	}

	public Message getMessage() {

		return this.message;
	}
}
