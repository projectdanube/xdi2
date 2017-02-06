package xdi2.core.features.policy.operator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot.MappingAbsoluteToRelativeXDIStatementIterator;
import xdi2.core.features.policy.condition.Condition;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;
import xdi2.core.util.iterators.SingleItemIterator;

public abstract class ConditionOperator extends Operator {

	private static final long serialVersionUID = 8705917224635642985L;

	protected ConditionOperator(Relation relation) {

		super(relation);
	}

	/**
	 * Returns the XDI conditions of the XDI policy statement.
	 * @return The XDI conditions of the XDI policy statement.
	 */
	public IterableIterator<Condition> getConditions() {

		ContextNode contextNode = this.getRelation().followContextNode();

		XdiInnerRoot innerRoot = XdiInnerRoot.fromContextNode(contextNode);

		if (innerRoot == null) {

			Condition condition = Condition.fromAddress(contextNode.getXDIAddress());
			if (condition == null) return new EmptyIterator<Condition> ();

			return new SingleItemIterator<Condition> (condition);
		} else {

			return new MappingIterator<XDIStatement, Condition> (
					new MappingAbsoluteToRelativeXDIStatementIterator(
							innerRoot,
							new MappingXDIStatementIterator(
									new SelectingNotImpliedStatementIterator(
											innerRoot.getContextNode().getAllStatements())))) {

				@Override
				public Condition map(XDIStatement XDIstatement) {

					return Condition.fromStatement(XDIstatement);
				}
			};
		}
	}
}
