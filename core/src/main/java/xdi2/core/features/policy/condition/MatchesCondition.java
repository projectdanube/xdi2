package xdi2.core.features.policy.condition;

import java.util.regex.Pattern;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

/**
 * An XDI $matches condition, represented as a statement.
 * 
 * @author markus
 */
public class MatchesCondition extends Condition {

	private static final long serialVersionUID = -3144452704386786096L;

	protected MatchesCondition(XDIStatement XDIstatement) {

		super(XDIstatement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $matches condition.
	 * @param XDIstatement The statement to check.
	 * @return True if the statement is a valid XDI $matches condition.
	 */
	public static boolean isValid(XDIStatement XDIstatement) {

		if (! XDIstatement.isRelationStatement()) return false;

		if (! XDIPolicyConstants.XDI_ADD_MATCHES.equals(XDIstatement.getRelationXDIAddress())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $matches condition bound to a given statement.
	 * @param XDIstatement The statement that is an XDI $matches condition.
	 * @return The XDI $matches condition.
	 */
	public static MatchesCondition fromStatement(XDIStatement XDIstatement) {

		if (! isValid(XDIstatement)) return null;

		return new MatchesCondition(XDIstatement);
	}

	public static MatchesCondition fromSubjectAndObject(XDIAddress subject, XDIAddress object) {

		return fromStatement(XDIStatement.fromRelationComponents(subject, XDIPolicyConstants.XDI_ADD_MATCHES, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		ContextNode subject = policyEvaluationContext.getContextNode(this.getXDIStatement().getSubject());
		ContextNode object = policyEvaluationContext.getContextNode((XDIAddress) this.getXDIStatement().getObject());

		if (subject == null || object == null) return false;

		if (subject.containsLiteralNode()) {

			if (! (object.containsLiteralNode())) return false;

			String subjectLiteralData = subject.getLiteralNode().getLiteralDataString();
			String objectLiteralData = object.getLiteralNode().getLiteralDataString();

			if (subjectLiteralData == null || objectLiteralData == null) return false;

			return Pattern.matches(objectLiteralData, subjectLiteralData);
		}

		return false;
	}
}
