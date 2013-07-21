package xdi2.core.features.linkcontracts.condition;

import xdi2.core.ContextNode;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI generic condition, represented as a statement.
 * 
 * @author markus
 */
public class GenericCondition extends Condition {

	private static final long serialVersionUID = 3812888725775095575L;

	protected GenericCondition(XDI3Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI generic condition.
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI generic condition.
	 */
	public static boolean isValid(XDI3Statement statement) {

		return true;
	}

	/**
	 * Factory method that creates an XDI generic condition bound to a given statement.
	 * @param statement The statement that is an XDI generic condition.
	 * @return The XDI generic condition.
	 */
	public static GenericCondition fromStatement(XDI3Statement statement) {

		if (! isValid(statement)) return null;

		return new GenericCondition(statement);
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		if (this.getStatement().isContextNodeStatement()) {

			ContextNode contextNode = policyEvaluationContext.getContextNode(this.getStatement().getContextNodeXri());
			XDI3SubSegment contextNodeArcXri = this.getStatement().getContextNodeArcXri();

			return Boolean.valueOf(contextNode != null && contextNode.containsContextNode(contextNodeArcXri));
		}

		if (this.getStatement().isRelationStatement()) {

			ContextNode contextNode = policyEvaluationContext.getContextNode(this.getStatement().getContextNodeXri());
			XDI3Segment arcXri = this.getStatement().getRelationArcXri();
			XDI3Segment targetContextNodeXri = policyEvaluationContext.getContextNodeXri(this.getStatement().getTargetContextNodeXri());

			return Boolean.valueOf(contextNode != null && contextNode.containsRelation(arcXri, targetContextNodeXri));
		}

		if (this.getStatement().isLiteralStatement()) {

			ContextNode contextNode = policyEvaluationContext.getContextNode(this.getStatement().getContextNodeXri());
			String literalData = this.getStatement().getLiteralData();

			return Boolean.valueOf(contextNode != null && contextNode.containsLiteral(literalData));
		}

		return Boolean.FALSE;
	}
}
