package xdi2.core;

import xdi2.core.Statement.LiteralStatement;

/**
 * This interface represents a literal node in an XDI graph.
 * 
 * @author markus
 */
public interface LiteralNode extends Node, Comparable<LiteralNode> {

	/*
	 * General methods
	 */

	/**
	 * Get the literal data.
	 * @return The literal data associated with the literal node.
	 */
	public Object getLiteralData();

	/**
	 * Get the literal data string.
	 * @return The literal data string associated with the literal node.
	 */
	public String getLiteralDataString();

	/**
	 * Get the literal data number.
	 * @return The literal data number associated with the literal node.
	 */
	public Double getLiteralDataNumber();

	/**
	 * Get the literal data boolean.
	 * @return The literal data boolean associated with the literal node.
	 */
	public Boolean getLiteralDataBoolean();

	/**
	 * Set the literal data.
	 * @param literalData The literal data associated with the literal node.
	 */
	public void setLiteralData(Object literalData);

	/**
	 * Set the literal data string.
	 * @param literalData The literal data string associated with the literal node.
	 */
	public void setLiteralDataString(String literalData);

	/**
	 * Set the literal data number.
	 * @param literalData The literal data number associated with the literal node.
	 */
	public void setLiteralDataNumber(Double literalData);

	/**
	 * Set the literal data boolean.
	 * @param literalData The literal data boolean associated with the literal node.
	 */
	public void setLiteralDataBoolean(Boolean literalData);

	/*
	 * Methods related to statements
	 */

	/**
	 * Gets the statement that represents this literal node.
	 * @return A statement.
	 */
	public LiteralStatement getStatement();
}
