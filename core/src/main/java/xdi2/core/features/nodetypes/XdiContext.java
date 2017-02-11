package xdi2.core.features.nodetypes;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;

public interface XdiContext<EQ extends XdiContext<EQ>> extends Serializable, Comparable<XdiContext<?>> {

	public ContextNode getContextNode();
	public Graph getGraph();
	public XDIArc getXDIArc();
	public XDIAddress getXDIAddress();
	public XDIArc getBaseXDIArc();

	public XdiContext<?> getXdiContext();
	public EQ dereference(boolean reference, boolean replacement, boolean identity);
	public EQ dereference();

	public XdiRoot findRoot();
	public XdiCommonRoot findCommonRoot();
	public XdiInnerRoot getXdiInnerRoot(XDIAddress innerRootPredicateXDIAddress, boolean create);
	public XdiEntityCollection getXdiEntityCollection(XDIArc contextNodeXDIArc, boolean create);
	public XdiEntityCollection getXdiEntityCollection(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiAttributeCollection getXdiAttributeCollection(XDIArc contextNodeXDIArc, boolean create);
	public XdiAttributeCollection getXdiAttributeCollection(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiEntitySingleton getXdiEntitySingleton(XDIArc contextNodeXDIArc, boolean create);
	public XdiEntitySingleton getXdiEntitySingleton(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiAttributeSingleton getXdiAttributeSingleton(XDIArc contextNodeXDIArc, boolean create);
	public XdiAttributeSingleton getXdiAttributeSingleton(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiEntity getXdiEntity(XDIArc contextNodeXDIArc, boolean create);
	public XdiEntity getXdiEntity(XDIAddress contextNodeXDIAddress, boolean create);
	public XdiAttribute getXdiAttribute(XDIArc contextNodeXDIArc, boolean create);
	public XdiAttribute getXdiAttribute(XDIAddress contextNodeXDIAddress, boolean create);

	public static class MappingXdiContextContextNodeIterator extends MappingIterator<XdiContext<?>, ContextNode> {

		public MappingXdiContextContextNodeIterator(Iterator<? extends XdiContext<?>> iterator) {

			super(iterator);
		}

		@Override
		public ContextNode map(XdiContext<?> xdiContext) {

			return xdiContext.getContextNode();
		}
	}
}
