package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.Relation;
import xdi2.core.Statement;

/**
 * A MappingIterator that maps XDI relations to their statements.
 * 
 * @author markus
 */
public class MappingRelationStatementIterator extends MappingIterator<Relation, Statement> {

	public MappingRelationStatementIterator(Iterator<Relation> relations) {

		super(relations);
	}

	@Override
	public Statement map(Relation relation) {

		return relation.getStatement();
	}
}
