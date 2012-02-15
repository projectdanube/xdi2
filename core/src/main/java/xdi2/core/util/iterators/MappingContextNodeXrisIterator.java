package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3Segment;

/**
 * A MappingIterator that maps XDI context nodes to their XRIs.
 * 
 * @author markus
 */
public class MappingContextNodeXrisIterator extends MappingIterator<ContextNode, XRI3Segment> {

	public MappingContextNodeXrisIterator(Iterator<ContextNode> contextNodes) {

		super(contextNodes);
	}

	@Override
	public XRI3Segment map(ContextNode contextNode) {

		return contextNode.getXri();
	}
}
