package xdi2.core.util.iterators;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiInnerRoot;

/**
 * A SelectingIterator that selects relations except the ones that establish an inner root.
 * 
 * @author markus
 */
public class SelectingNotXdiInnerRootRelationIterator extends SelectingIterator<Relation> {

	private static final Logger log = LoggerFactory.getLogger(SelectingNotXdiInnerRootRelationIterator.class);

	public SelectingNotXdiInnerRootRelationIterator(Iterator<? extends Relation> relations) {

		super(relations);
	}

	@Override
	public boolean select(Relation relation) {

		if (XdiInnerRoot.fromRelation(relation) != null) {

			if (log.isTraceEnabled()) log.trace("Skipping inner root relation: " + relation);

			return false;
		}

		return true;
	}
}
