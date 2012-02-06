package old;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.xri3.impl.XRI3Segment;

/**
 * This interface represents a predicate in an XDI graph. Methods include
 * creating and finding references, literals and inner graphs of the predicate.
 * 
 * @author markus
 */
public interface Predicate extends GraphComponent, Serializable, Comparable<Predicate> {

	/*
	 * General methods
	 */

	/**
	 * Every predicate has a subject node from which it originates.
	 * @return The subject node of this predicate.
	 */
	public Subject getSubject();

	/**
	 * Deletes this predicate from the subject.
	 */
	public void deleteFromSubject();

	/**
	 * Clears the predicate. This is equivalent to calling deleteReferences(), deleteLiteral() and deleteInnerGraph().
	 */
	public void clear();

	/**
	 * Checks if the predicate is empty. 
	 * This is equivalent to calling ! ( containsReferences() || containsLiteral() || containsInnerGraph() ).
	 */
	public boolean isEmpty();

	/**
	 * Every predicate has an associated XRI.
	 * @return The XRI associated with the predicate.
	 */
	public XRI3Segment getPredicateXri();

	/*
	 * Methods related to references of the predicate
	 */

	/**
	 * Creates a new reference and adds it to the predicate.
	 * @param referenceXri The reference XRI of the reference.
	 * @return The newly created reference.
	 */
	public Reference createReference(XRI3Segment referenceXri);

	/**
	 * Returns the references of this predicate.
	 * @return An iterator over references.
	 */
	public Iterator<Reference> getReferences();

	/**
	 * Finds and returns a reference with a given XRI. 
	 * @param referenceXri The reference XRI to look for. 
	 * @return The reference with the given XRI, or null.
	 */
	public Reference getReference(XRI3Segment referenceXri);

	/**
	 * Finds and returns a single reference. 
	 * @return A reference, or null.
	 */
	public Reference getReference();

	/**
	 * Checks if the predicate has one or more references.
	 * @return True if this predicate has references.
	 */
	public boolean containsReferences();

	/**
	 * Checks if a reference with a given XRI exists in the graph.
	 * @param referenceXri The referenceXri to look for. 
	 * @return True if this graph has a predicate with the given XRI.
	 */
	public boolean containsReference(XRI3Segment referenceXri);

	/**
	 * Deletes all references from this predicate.
	 */
	public void deleteReferences();

	/**
	 * Deletes the reference with a given reference XRI from this predicate.
	 * @param referenceXri The reference XRI of the reference.
	 */
	public void deleteReference(XRI3Segment referenceXri);

	/**
	 * Returns the number of references of this predicate.
	 * @return The number of references.
	 */
	public int getReferenceCount();

	/*
	 * Methods related to the literal of the predicate
	 */

	/**
	 * Creates a new literal and adds it to the predicate.
	 * @param data The literal data of the literal.
	 * @return The newly created literal.
	 */
	public Literal createLiteral(String data);

	/**
	 * Returns the literal of this predicate.
	 * @return The literal of this predicate, or null.
	 */
	public Literal getLiteral();

	/**
	 * Checks if the predicate has a literal.
	 * @return True if this predicate has a literal.
	 */
	public boolean containsLiteral();

	/**
	 * Deletes the literal from this predicate.
	 */
	public void deleteLiteral();

	/*
	 * Methods related to the inner graph of the predicate
	 */

	/**
	 * Creates a new inner graph and adds it to the predicate.
	 * @param graph The inner graph of the literal.
	 * @return The newly created literal.
	 */
	public Graph createInnerGraph(Graph graph);

	/**
	 * Returns the inner graph of this predicate.
	 * @return The inner graph of this predicate, or null.
	 */
	public Graph getInnerGraph();

	/**
	 * Checks if the predicate has an inner graph.
	 * @return True if this predicate has an inner graph.
	 */
	public boolean containsInnerGraph();

	/**
	 * Deletes the inner graph from this predicate.
	 */
	public void deleteInnerGraph();

	/*
	 * Methods related to statements
	 */

	/**
	 * @param referenceXri The reference XRI of the statement.
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment referenceXri);

	/**
	 * Creates a new statement and adds it to the predicate.
	 * @param data The literal data of the statement.
	 * @return The newly created statement.
	 */
	public Statement createStatement(String data);

	/**
	 * Creates a new statement and adds it to the predicate.
	 * @param graph The inner graph of the statement. 
	 * This graph will be copied. If it is null, a blank inner graph will be created.
	 * @return The newly created statement.
	 */
	public Statement createStatement(Graph graph);

	/**
	 * Lists all statements rooted in this predicate.
	 * @return An iterator over statements.
	 */
	public Iterator<Statement> getStatements();

	/**
	 * Gets a single statement with a given reference XRI.
	 * @param referenceXri The reference XRI of the statement.
	 * @return An iterator over all statements.
	 */
	public Statement getStatement(XRI3Segment referenceXri);

	/**
	 * Gets a single statement with a given literal data.
	 * @param data The literal data of the statement.
	 * @return A statement.
	 */
	public Statement getStatement(String data);

	/**
	 * Gets a single statement with a given inner graph.
	 * @param graph The inner graph of the statement. 
	 * @return A statement.
	 */
	public Statement getStatement(Graph graph);

	/**
	 * Checks if a statement with a given reference XRI exists in the predicate.
	 * @param referenceXri The reference XRI of the statement.
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment referenceXri);

	/**
	 * Checks if a statement with a given literal data exists in the predicate.
	 * @param data The literal data of the statement. 
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(String data);

	/**
	 * Checks if a statement with a given inner graph exists in the predicate.
	 * @param graph The inner graph of the statement. 
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(Graph graph);

	/**
	 * Deletes all statements.
	 */
	public void deleteStatements();

	/**
	 * Deletes a statement with a given reference XRI.
	 * @param referenceXri The reference XRI of the statement.
	 */
	public void deleteStatement(XRI3Segment referenceXri);

	/**
	 * Deletes a statement with a given literal data.
	 * @param data The literal data of the statement.
	 */
	public void deleteStatement(String data);

	/**
	 * Deletes a statement with a given inner graph
	 * @param graph The inner graph of the statement.
	 */
	public void deleteStatement(Graph graph);

	/**
	 * Returns the total number of statements this subject is part of. 
	 * @return The number of statements.
	 */
	public int getStatementCount();

	/*
	 * Methods related to comments
	 */

	/**
	 * Gets all comments in the predicate.
	 * @return An iterator over comments.
	 */
	public Iterator<String> getComments();

	/**
	 * Returns the total number of comments in the predicate. 
	 * @return The number of comments.
	 */
	public int getCommentCount();
}
