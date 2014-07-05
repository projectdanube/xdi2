package xdi2.core.features.nodetypes;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public interface XdiContext<EQ extends XdiContext<EQ>> extends Serializable, Comparable<XdiContext<?>> {

	public ContextNode getContextNode();
	public Graph getGraph();
	public XDI3SubSegment getArcXri();
	public XDI3Segment getXri();
	public XDI3SubSegment getBaseArcXri();

	public EQ dereference();
	public EQ getReferenceXdiContext();
	public EQ getReplacementXdiContext();
	public Iterator<EQ> getIdentityXdiContexts();

	public XdiRoot findRoot();
	public XdiLocalRoot findLocalRoot();
	public XdiInnerRoot getXdiInnerRoot(XDI3Segment innerRootPredicateXri, boolean create);
	public XdiEntityCollection getXdiEntityCollection(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiEntityCollection getXdiEntityCollection(XDI3Segment contextNodeXri, boolean create);
	public XdiAttributeCollection getXdiAttributeCollection(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiAttributeCollection getXdiAttributeCollection(XDI3Segment contextNodeXri, boolean create);
	public XdiEntitySingleton getXdiEntitySingleton(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiEntitySingleton getXdiEntitySingleton(XDI3Segment contextNodeXri, boolean create);
	public XdiAttributeSingleton getXdiAttributeSingleton(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiAttributeSingleton getXdiAttributeSingleton(XDI3Segment contextNodeXri, boolean create);
	public XdiEntity getXdiEntity(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiEntity getXdiEntity(XDI3Segment contextNodeXri, boolean create);
	public XdiAttribute getXdiAttribute(XDI3SubSegment contextNodeArcXri, boolean create);
	public XdiAttribute getXdiAttribute(XDI3Segment contextNodeXri, boolean create);
}
