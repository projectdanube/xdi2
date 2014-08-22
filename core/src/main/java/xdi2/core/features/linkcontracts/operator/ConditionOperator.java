package xdi2.core.features.linkcontracts.operator;

import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot.MappingAbsoluteToRelativeStatementIterator;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.MappingStatementIterator;
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

		XdiInnerRoot innerRoot = XdiInnerRoot.fromContextNode(this.getRelation().follow());
		if (innerRoot == null) throw new Xdi2RuntimeException("Missing condition in operator: " + this.getRelation());

		return new MappingIterator<XDIStatement, Condition> (
				new MappingAbsoluteToRelativeStatementIterator(
						innerRoot,
						new MappingStatementIterator(
								new SelectingNotImpliedStatementIterator(
										innerRoot.getContextNode().getAllStatements())))) {

			@Override
			public Condition map(XDIStatement statementAddress) {

				return Condition.fromStatement(statementAddress);
			}
		};
	}
}
