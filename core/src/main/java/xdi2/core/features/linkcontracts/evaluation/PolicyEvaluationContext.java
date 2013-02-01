package xdi2.core.features.linkcontracts.evaluation;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.xri3.XDI3Segment;

/**
 * This interface provides a context for evaluating an XDI policy.
 * It is able to look up a context node XRI, context node, and relation.
 * This is used to determine if policies, policy statements, and conditions evaluate to true or false.
 */
public interface PolicyEvaluationContext {

	public XDI3Segment getContextNodeXri(XDI3Segment xri);
	public ContextNode getContextNode(XDI3Segment xri);
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri);
}
