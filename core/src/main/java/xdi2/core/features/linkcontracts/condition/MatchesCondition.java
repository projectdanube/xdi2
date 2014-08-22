package xdi2.core.features.linkcontracts.condition;

import java.util.regex.Pattern;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

/**
 * An XDI $matches condition, represented as a statement.
 * 
 * @author markus
 */
public class MatchesCondition extends Condition {

	private static final long serialVersionUID = -3144452704386786096L;

	protected MatchesCondition(XDIStatement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $matches condition.
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI $matches condition.
	 */
	public static boolean isValid(XDIStatement statement) {

		if (! statement.isRelationStatement()) return false;

		if (! XDIPolicyConstants.XDI_ADD_MATCHES.equals(statement.getRelationAddress())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $matches condition bound to a given statement.
	 * @param statement The statement that is an XDI $matches condition.
	 * @return The XDI $matches condition.
	 */
	public static MatchesCondition fromStatement(XDIStatement statement) {

		if (! isValid(statement)) return null;

		return new MatchesCondition(statement);
	}

	public static MatchesCondition fromSubjectAndObject(XDIAddress subject, XDIAddress object) {

		return fromStatement(XDIStatement.fromRelationComponents(subject, XDIPolicyConstants.XDI_ADD_MATCHES, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		ContextNode subject = policyEvaluationContext.getContextNode(this.getStatementXri().getSubject());
		ContextNode object = policyEvaluationContext.getContextNode((XDIAddress) this.getStatementXri().getObject());

		if (subject == null || object == null) return Boolean.FALSE;

		if (subject.containsLiteral()) {

			if (! object.containsLiteral()) return Boolean.FALSE;

			String subjectLiteralData = subject.getLiteral().getLiteralDataString();
			String objectLiteralData = object.getLiteral().getLiteralDataString();

			if (subjectLiteralData == null || objectLiteralData == null) return Boolean.FALSE;

			return Boolean.valueOf(Pattern.matches(objectLiteralData, subjectLiteralData));
		}

		return Boolean.FALSE;
	}
}
