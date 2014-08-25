package xdi2.core.features.policy.evaluation;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

/**
 * This interface provides a context for evaluating an XDI policy.
 * It is able to look up a context node address, context node, and statement.
 * This is used to determine if policies, policy statements, and conditions evaluate to true or false.
 */
public interface PolicyEvaluationContext {

	public XDIAddress resolveXDIAddress(XDIAddress contextNodeXDIAddress);
	public ContextNode getContextNode(XDIAddress contextNodeXDIAddress);
	public Statement getStatement(XDIStatement statement);
}
