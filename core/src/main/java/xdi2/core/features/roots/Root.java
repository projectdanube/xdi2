package xdi2.core.features.roots;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

public interface Root extends Serializable, Comparable<Root> {

	public ContextNode getContextNode();

	public LocalRoot findLocalRoot();
	public RemoteRoot findRemoteRoot(XDI3Segment xri, boolean create);
	public InnerRoot findInnerRoot(XDI3Segment subject, XDI3Segment predicate, boolean create);

	public Root findRoot(XDI3Segment xri, boolean create);
	public Root findRoot(XDI3SubSegment arcXri, boolean create);

	public XDI3Segment getRelativePart(XDI3Segment xri);

	public Statement createRelativeStatement(XDI3Statement statementXri);
	public Statement findRelativeStatement(XDI3Statement statementXri);
	public boolean containsRelativeStatement(XDI3Statement statementXri);
	public Iterator<XDI3Statement> getRelativeStatements(boolean ignoreImplied);

	/**
	 * A simple way to find a statement in this graph.
	 */

	/**
	 * A simple way to check if a statement exists in this graph.
	 */
}
