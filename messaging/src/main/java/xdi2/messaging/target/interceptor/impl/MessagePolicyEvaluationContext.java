package xdi2.messaging.target.interceptor.impl;

import java.util.Iterator;

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

			XDI3Segment reducedXri = XRIUtil.reduceXri(xri, XRI_MSG);

			return XRIUtil.expandXri(reducedXri, this.getMessage().getContextNode().getXri());
		}

		if (XRIUtil.startsWith(xri, XRI_FROM)) {

			XDI3Segment reducedXri = XRIUtil.reduceXri(xri, XRI_FROM);

			return XRIUtil.expandXri(reducedXri, this.getMessage().getSender());
		}

		return super.getContextNodeXri(xri);
	}

	@Override
	public ContextNode getContextNode(XDI3Segment xri) {

		if (XRIUtil.startsWith(xri, XRI_MSG)) {

			XDI3Segment reducedXri = XRIUtil.reduceXri(xri, XRI_MSG);

			ContextNode contextNode = this.getMessage().getContextNode();
			if (reducedXri != null) contextNode = contextNode.findContextNode(reducedXri, false);

			return contextNode;
		}

		if (XRIUtil.startsWith(xri, XRI_FROM)) {

			XDI3Segment reducedXri = XRIUtil.reduceXri(xri, XRI_FROM);

			ContextNode contextNode = this.getGraph().findContextNode(this.getMessage().getSender(), false);
			if (reducedXri != null) contextNode = contextNode.findContextNode(reducedXri, false);

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
