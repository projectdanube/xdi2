package xdi2.core.features.linkcontracts.evaluation;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * This interface provides a context for evaluating an XDI policy.
 * It is able to look up a context node XRI, context node, and relation.
 * This is used to determine if policies, policy statements, and conditions evaluate to true or false.
 */
public interface PolicyEvaluationContext {

	public XDI3Segment getContextNodeXri(XDI3Segment xri);
	public ContextNode getContextNode(XDI3Segment xri);
	public Statement getStatement(XDI3Statement statementXri);
}
