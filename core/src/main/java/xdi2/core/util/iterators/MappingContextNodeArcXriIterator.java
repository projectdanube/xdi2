package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3SubSegment;

/**
 * A MappingIterator that maps XDI context nodes to their arc XRIs.
 * 
 * @author markus
 */
public class MappingContextNodeArcXriIterator extends MappingIterator<ContextNode, XRI3SubSegment> {

	public MappingContextNodeArcXriIterator(Iterator<ContextNode> contextNodes) {

		super(contextNodes);
	}

	@Override
	public XRI3SubSegment map(ContextNode contextNode) {

		return contextNode.getArcXri();
	}
}
