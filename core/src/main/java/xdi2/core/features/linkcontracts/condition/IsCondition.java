package xdi2.core.features.linkcontracts.condition;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * An XDI $is condition, represented as a statement.
 * 
 * @author markus
 */
public class IsCondition extends Condition {

	private static final long serialVersionUID = 7506322819724395818L;

	protected IsCondition(XDI3Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $is condition.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $is condition.
	 */
	public static boolean isValid(XDI3Statement statement) {

		if (! statement.isRelationStatement()) return false;

		if (! XDILinkContractConstants.XRI_S_IS.equals(statement.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $is condition bound to a given statement.
	 * @param statement The statement that is an XDI $is condition.
	 * @return The XDI $is condition.
	 */
	public static IsCondition fromStatement(XDI3Statement statement) {

		if (! isValid(statement)) return null;

		return new IsCondition(statement);
	}

	public static IsCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(StatementUtil.fromComponents(subject, XDILinkContractConstants.XRI_S_IS, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		// check if subject XRI and object XRI are the same

		XDI3Segment subjectXri = policyEvaluationContext.getContextNodeXri(this.getStatement().getSubject());
		XDI3Segment objectXri = policyEvaluationContext.getContextNodeXri(this.getStatement().getObject());

		if (subjectXri != null && subjectXri.equals(objectXri)) return true;

		// check if the statement exists

		ContextNode subject = policyEvaluationContext.getContextNode(this.getStatement().getSubject());
		ContextNode object = policyEvaluationContext.getContextNode(this.getStatement().getObject());

		Iterator<ContextNode> equivalenceContextNodes = subject == null ? new EmptyIterator<ContextNode> () : Equivalence.getEquivalenceContextNodes(subject);
		if (new IteratorContains<ContextNode> (equivalenceContextNodes, object).contains()) return true;

		// done

		return false;
	}
}
