package xdi2.core.features.roots;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

public interface Root extends Serializable, Comparable<Root> {

	/**
	 * Returns the underlying context node to which this XDI root is bound.
	 * @return A context node that represents the XDI root.
	 */
	public ContextNode getContextNode();

	/*
	 * Finding roots related to this root
	 */
	
	/**
	 * Finds and returns the XDI local root for this XDI root.
	 * @return The XDI local root.
	 */
	public LocalRoot findLocalRoot();

	/**
	 * Finds and returns an XDI peer root under this XDI root.
	 * @param xri The XRI whose XDI peer root to find.
	 * @param create Whether the XDI peer root should be created, if it does not exist.
	 * @return The XDI peer root.
	 */
	public PeerRoot findPeerRoot(XDI3Segment xri, boolean create);

	/**
	 * Finds and returns an XDI inner root under this XDI root.
	 * @param subject The subject XRI whose XDI inner root to find.
	 * @param predicate The predicate XRI whose XDI inner root to find.
	 * @param create Whether the XDI inner root should be created, if it does not exist.
	 * @return The XDI inner root.
	 */
	public InnerRoot findInnerRoot(XDI3Segment subject, XDI3Segment predicate, boolean create);

	/**
	 * Finds and returns an XDI root under this XDI root.
	 * @param xri The XRI contained in the XDI root.
	 * @param create Whether the XDI root should be created, if it does not exist.
	 * @return The XDI root.
	 */
	public Root findRoot(XDI3Segment xri, boolean create);

	/**
	 * Finds and returns an XDI root under this XDI root.
	 * @param arcXri The arc XRI whose XDI root to find.
	 * @param create Whether the XDI root should be created, if it does not exist.
	 * @return The XDI root.
	 */
	public Root findRoot(XDI3SubSegment arcXri, boolean create);

	/*
	 * Statements relative to this root.
	 */
	
	/**
	 * Given an XRI, returns the part of it that is relative to this XDI root.
	 * This returns null if the XRI is not contained in the XDI root.
	 * @param xri The XRI.
	 * @return The relative part of the XRI.
	 */
	public XDI3Segment getRelativePart(XDI3Segment xri);

	/**
	 * A simple way to create a relative statement in this XDI root.
	 */
	public Statement createRelativeStatement(XDI3Statement statementXri);

	/**
	 * A simple way to find a relative statement in this XDI root.
	 */
	public Statement findRelativeStatement(XDI3Statement statementXri);

	/**
	 * A simple way to check if a relative statement exists in this XDI root.
	 */
	public boolean containsRelativeStatement(XDI3Statement statementXri);

	/**
	 * Returns the relative statements under this XDI root.
	 * @param ignoreImplied Whether to ignore implied statements.
	 * @return The relative statements.
	 */
	public Iterator<XDI3Statement> getRelativeStatements(boolean ignoreImplied);
}
