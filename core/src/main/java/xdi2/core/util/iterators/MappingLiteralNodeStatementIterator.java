package xdi2.core.util.iterators;

import java.util.Arrays;
import java.util.Iterator;

import xdi2.core.LiteralNode;
import xdi2.core.Statement.LiteralStatement;

/**
 * A MappingIterator that maps XDI literals to their statements.
 * 
 * @author markus
 */
public class MappingLiteralNodeStatementIterator extends MappingIterator<LiteralNode, LiteralStatement> {

	public MappingLiteralNodeStatementIterator(Iterator<LiteralNode> literals) {

		super(literals);
	}

	public MappingLiteralNodeStatementIterator(LiteralNode literal) {

		super(Arrays.asList(new LiteralNode[] { literal }).iterator());
	}

	@Override
	public LiteralStatement map(LiteralNode literal) {

		return literal.getStatement();
	}
}
