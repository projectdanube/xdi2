package xdi2.core.features.policy.operator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot.MappingAbsoluteToRelativeXDIStatementIterator;
import xdi2.core.features.policy.condition.Condition;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;

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

		XdiInnerRoot innerRoot = contextNode == null ? null : XdiInnerRoot.fromContextNode(contextNode);
		if (innerRoot == null) throw new Xdi2RuntimeException("Missing condition in operator: " + this.getRelation());

		return new MappingIterator<XDIStatement, Condition> (
				new MappingAbsoluteToRelativeXDIStatementIterator(
						innerRoot,
						new MappingXDIStatementIterator(
								new SelectingNotImpliedStatementIterator(
										innerRoot.getContextNode().getAllStatements())))) {

			@Override
			public Condition map(XDIStatement statementXri) {

				return Condition.fromStatement(statementXri);
			}
		};
	}
}
