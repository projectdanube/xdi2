package xdi2.core.util.iterators;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;

/**
 * A MappingIterator that maps XDI context nodes to their addresses.
 * 
 * @author markus
 */
public class MappingContextNodeAddressIterator extends MappingIterator<ContextNode, XDIAddress> {

	public MappingContextNodeAddressIterator(Iterator<ContextNode> contextNodes) {

		super(contextNodes);
	}

	@Override
	public XDIAddress map(ContextNode contextNode) {

		return contextNode.getAddress();
	}
}
