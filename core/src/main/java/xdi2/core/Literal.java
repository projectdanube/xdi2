package xdi2.core;

import java.io.Serializable;

import xdi2.core.Statement.LiteralStatement;

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
	 * Get the graph of this literal.
	 * @return The graph of this literal.
	 */
	public Graph getGraph();
	
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
	 * Get the literal data.
	 * @return The literal data associated with the literal.
	 */
	public Object getLiteralData();

	/**
	 * Get the literal data string.
	 * @return The literal data string associated with the literal.
	 */
	public String getLiteralDataString();

	/**
	 * Get the literal data number.
	 * @return The literal data number associated with the literal.
	 */
	public Double getLiteralDataNumber();

	/**
	 * Get the literal data boolean.
	 * @return The literal data boolean associated with the literal.
	 */
	public Boolean getLiteralDataBoolean();

	/**
	 * Set the literal data.
	 * @param literalData The literal data associated with the literal.
	 */
	public void setLiteralData(Object literalData);

	/**
	 * Set the literal data string.
	 * @param literalData The literal data string associated with the literal.
	 */
	public void setLiteralDataString(String literalData);

	/**
	 * Set the literal data number.
	 * @param literalData The literal data number associated with the literal.
	 */
	public void setLiteralDataNumber(Double literalData);

	/**
	 * Set the literal data boolean.
	 * @param literalData The literal data boolean associated with the literal.
	 */
	public void setLiteralDataBoolean(Boolean literalData);

	/*
	 * Methods related to statements
	 */

	/**
	 * Gets the statement that represents this literal.
	 * @return A statement.
	 */
	public LiteralStatement getStatement();
}
