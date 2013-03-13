package xdi2.core.util.iterators;

import java.util.Arrays;
import java.util.Iterator;

import xdi2.core.Relation;
import xdi2.core.Statement.RelationStatement;

/**
 * A MappingIterator that maps XDI relations to their statements.
 * 
 * @author markus
 */
public class MappingRelationStatementIterator extends MappingIterator<Relation, RelationStatement> {

	public MappingRelationStatementIterator(Iterator<Relation> relations) {

		super(relations);
	}

	public MappingRelationStatementIterator(Relation relation) {

		super(Arrays.asList(new Relation[] { relation }).iterator());
	}

	@Override
	public RelationStatement map(Relation relation) {

		return relation.getStatement();
	}
}
