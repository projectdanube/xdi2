package xdi2.core.features.linkcontracts.operator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.roots.InnerRoot;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.XDI3Statement;

/**
 * An XDI $false operator, represented as a relation.
 * 
 * @author markus
 */
public class FalseOperator extends ConditionOperator {

	private static final long serialVersionUID = -7397004800836677763L;

	protected FalseOperator(Relation relation) {

		super(relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a relation is a valid XDI $false operator.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $false operator.
	 */
	public static boolean isValid(Relation relation) {

		if (! XDIConstants.XRI_S_FALSE.equals(relation.getArcXri())) return false;
		if (! InnerRoot.isValid(relation.follow())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $false operator bound to a given relation.
	 * @param relation The relation that is an XDI $false operator.
	 * @return The XDI $false operator.
	 */
	public static FalseOperator fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new FalseOperator(relation);
	}

	public static FalseOperator fromCondition(Condition condition) {

		InnerRoot innerRoot = GraphUtil.innerRootFromComponents(XDIConstants.XRI_S_ROOT, XDIConstants.XRI_S_FALSE, new SingleItemIterator<XDI3Statement> (condition.getStatement()));

		return fromRelation(innerRoot.getPredicateRelation());
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean[] evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		Iterator<Condition> conditions = this.getConditions();
		if (conditions == null) throw new Xdi2RuntimeException("Missing or invalid condition in $false operator.");

		List<Boolean> values = new ArrayList<Boolean> ();
		while (conditions.hasNext()) values.add(Boolean.valueOf(Boolean.FALSE.equals(conditions.next().evaluate(policyEvaluationContext))));

		return values.toArray(new Boolean[values.size()]);
	}
}
