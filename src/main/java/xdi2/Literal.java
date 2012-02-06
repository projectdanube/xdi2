package xdi2;

import java.io.Serializable;

import xdi2.xri3.impl.XRI3SubSegment;

/**
 * This interface represents a literal in an XDI graph.
 * 
 * @author markus
 */
public interface Literal extends Serializable, Comparable<Literal> {

	/*
	 * General methods
	 */

	/**
	 * Every literal has a context node from which it originates.
	 * @return The context node of this literal.
	 */
	public ContextNode getContextNode();

	/**
	 * Deletes this literal.
	 */
	public void delete();

	/**
	 * Every literal has an associated arc XRI.
	 * @return The arc XRI associated with the literal.
	 */
	public XRI3SubSegment getArcXri();

	/**
	 * Get the literal data.
	 * @return The literal data associated with the literal.
	 */
	public String getLiteralData();

	/**
	 * Set the literal data.
	 * @param literalData The literal data associated with the literal.
	 */
	public void setLiteralData(String literalData);
}
