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
 * An XDI $greater condition, represented as a statement.
 * 
 * @author markus
 */
public class GreaterCondition extends Condition {

	private static final long serialVersionUID = 2302071980940540935L;

	protected GreaterCondition(Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $greater condition.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $greater condition.
	 */
	public static boolean isValid(Statement statement) {

		if (! (statement instanceof RelationStatement)) return false;

		Relation relation = ((RelationStatement) statement).getRelation();

		if (! XDILinkContractConstants.XRI_S_GREATER.equals(relation.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $greater condition bound to a given statement.
	 * @param statement The statement that is an XDI $greater condition.
	 * @return The XDI $greater condition.
	 */
	public static GreaterCondition fromStatement(Statement statement) {

		if (! isValid(statement)) return null;

		return new GreaterCondition(statement);
	}

	public static GreaterCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(StatementUtil.fromComponents(subject, XDILinkContractConstants.XRI_S_GREATER, object));
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

			return Integer.parseInt(subjectLiteralData) > Integer.parseInt(objectLiteralData);
		}

		return false;
	}
}
