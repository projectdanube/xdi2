package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Statement;

/**
 * A MappingIterator that maps XDI context nodes to their statements.
 * 
 * @author markus
 */
public class MappingContextNodeStatementIterator extends MappingIterator<ContextNode, Statement> {

	public MappingContextNodeStatementIterator(Iterator<ContextNode> contextNodes) {

		super(contextNodes);
	}

	@Override
	public Statement map(ContextNode contextNode) {

		return contextNode.getStatement();
	}
}
