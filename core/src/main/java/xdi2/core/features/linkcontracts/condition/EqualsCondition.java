package xdi2.core.features.linkcontracts.condition;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.locator.ContextNodeLocator;
import xdi2.core.xri3.impl.XDI3Segment;

/**
 * An XDI $equals condition, represented as a statement.
 * 
 * @author markus
 */
public class EqualsCondition extends Condition {

	private static final long serialVersionUID = 613463446651165139L;

	protected EqualsCondition(Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $equals condition.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $equals condition.
	 */
	public static boolean isValid(Statement statement) {

		if (! (statement instanceof RelationStatement)) return false;

		Relation relation = ((RelationStatement) statement).getRelation();

		if (! XDILinkContractConstants.XRI_S_EQUALS.equals(relation.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $equals condition bound to a given statement.
	 * @param statement The statement that is an XDI $equals condition.
	 * @return The XDI $equals condition.
	 */
	public static EqualsCondition fromStatement(Statement statement) {

		if (! isValid(statement)) return null;

		return new EqualsCondition(statement);
	}

	public static EqualsCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(StatementUtil.fromComponents(subject, XDILinkContractConstants.XRI_S_EQUALS, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(ContextNodeLocator contextNodeLocator) {

		ContextNode subject = contextNodeLocator.locateContextNode(this.getStatement().getSubject());
		ContextNode object = contextNodeLocator.locateContextNode(this.getStatement().getObject());

		if (subject == null || object == null) return false;

		if (subject.containsLiteral()) {

			if (! object.containsLiteral()) return false;

			String subjectLiteralData = subject.getLiteral().getLiteralData();
			String objectLiteralData = object.getLiteral().getLiteralData();

			return subjectLiteralData.equals(objectLiteralData);
		}

		return false;
	}
}
