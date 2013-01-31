package xdi2.messaging.target.interceptor.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.features.linkcontracts.evaluation.GraphPolicyEvaluationContext;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;

public class MessagePolicyEvaluationContext extends GraphPolicyEvaluationContext {

	public static final XDI3Segment XRI_FROM = XDI3Segment.create("($from)");
	public static final XDI3Segment XRI_MSG = XDI3Segment.create("($msg)");

	private Message message;

	public MessagePolicyEvaluationContext(Graph graph, Message message) {

		super(graph);

		this.message = message;
	}

	@Override
	public XDI3Segment getContextNodeXri(XDI3Segment xri) {

		if (XRIUtil.startsWith(xri, XRI_MSG)) {

			XDI3Segment relativeXri = XRIUtil.relativeXri(xri, XRI_MSG);

			return XDI3Segment.create("" + this.getMessage().getContextNode().getXri() + (relativeXri == null ? "" : relativeXri));
		}

		if (XRIUtil.startsWith(xri, XRI_FROM)) {

			XDI3Segment relativeXri = XRIUtil.relativeXri(xri, XRI_FROM);

			return XDI3Segment.create("" + this.getMessage().getSender() + (relativeXri == null ? "" : relativeXri));
		}

		return super.getContextNodeXri(xri);
	}

	@Override
	public ContextNode getContextNode(XDI3Segment xri) {

		if (XRIUtil.startsWith(xri, XRI_MSG)) {

			XDI3Segment relativeXri = XRIUtil.relativeXri(xri, XRI_MSG);

			ContextNode contextNode = this.getMessage().getContextNode();
			if (relativeXri != null) contextNode = contextNode.findContextNode(relativeXri, false);

			return contextNode;
		}

		if (XRIUtil.startsWith(xri, XRI_FROM)) {

			XDI3Segment relativeXri = XRIUtil.relativeXri(xri, XRI_FROM);

			ContextNode contextNode = this.getGraph().findContextNode(this.getMessage().getSender(), false);
			if (relativeXri != null) contextNode = contextNode.findContextNode(relativeXri, false);

			return contextNode;
		}

		return super.getContextNode(xri);
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.getMessage().getOperationsContextNode().getRelation(arcXri, targetContextNodeXri);
	}

	public Message getMessage() {

		return this.message;
	}
}
