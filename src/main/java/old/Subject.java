package old;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;

import xdi2.xri3.impl.XRI3Segment;

/**
 * This interface represents a subject in an XDI graph. Methods include
 * creating and finding predicates of the subject.
 * 
 * @author markus
 */
public interface Subject extends GraphComponent, Serializable, Comparable<Subject> {

	/*
	 * General methods
	 */

	/**
	 * Deletes this subject from the graph.
	 */
	public void deleteFromGraph();
	
	/**
	 * Clears the subject. 
	 * This is equivalent to calling deletePredicates().
	 */
	public void clear();

	/**
	 * Checks if the subject is empty. 
	 * This is equivalent to calling ! containsPredicates().
	 */
	public boolean isEmpty();

	/**
	 * Every subject has an associated XRI.
	 * @return The XRI associated with the subject.
	 */
	public XRI3Segment getSubjectXri();

	/**
	 * A subject XRI can be changed, if the new one does not exist already in the graph.
	 * @param subjectXri The new XRI associated with the subject.
	 */
	public void setSubjectXri(XRI3Segment subjectXri);

	/**
	 * Converts the subject to a string in the given serialization format.
	 * @param format The serialization format.
	 * @param parameters Parameters for the serialization.
	 */
	public String toString(String format, Properties parameters);

	/*
	 * Methods related to predicates of the subject
	 */

	/**
	 * Creates a new predicate and adds it to the subject.
	 * @param predicateXri The predicate XRI of the predicate.
	 * @return The newly created predicate.
	 */
	public Predicate createPredicate(XRI3Segment predicateXri);

	/**
	 * Returns the predicates of this subject.
	 * @return An iterator over predicates.
	 */
	public Iterator<Predicate> getPredicates();

	/**
	 * Returns the predicates of this subject that start with a given XRI.
	 * @return An iterator over predicates.
	 */
	public Iterator<Predicate> getPredicatesStartingWith(XRI3Segment predicateXri);

	/**
	 * Finds and returns a predicate with a given XRI. 
	 * @param predicateXri The predicate XRI to look for. 
	 * @return The predicate with the given XRI, or null.
	 */
	public Predicate getPredicate(XRI3Segment predicateXri);

	/**
	 * Returns the references of this subject.
	 * @return An iterator over references.
	 */
	public Iterator<Reference> getReferences();

	/**
	 * Returns the literals of this subject.
	 * @return An iterator over literals.
	 */
	public Iterator<Literal> getLiterals();

	/**
	 * Returns the inner graphs of this subject.
	 * @return An iterator over graphs.
	 */
	public Iterator<Graph> getInnerGraphs();

	/**
	 * Checks if the subject has one or more predicates.
	 * @return True if this subject has predicates.
	 */
	public boolean containsPredicates();

	/**
	 * Checks if a predicate with a given XRI exists in the graph.
	 * @param predicateXri The predicateXri to look for. 
	 * @return True if this graph has a subject with the given XRI.
	 */
	public boolean containsPredicate(XRI3Segment predicateXri);

	/**
	 * Deletes all predicates from this subject.
	 */
	public void deletePredicates();

	/**
	 * Deletes the predicate with a given predicate XRI from this subject.
	 * @param predicateXri The predicate XRI of the predicate.
	 */
	public void deletePredicate(XRI3Segment predicateXri);

	/**
	 * Returns the number of predicates of this subject.
	 * @return The number of predicates.
	 */
	public int getPredicateCount();

	/**
	 * Returns the number of references of this subject.
	 * @return The number of references.
	 */
	public int getReferenceCount();

	/**
	 * Returns the number of literals of this subject.
	 * @return The number of literals.
	 */
	public int getLiteralCount();

	/**
	 * Returns the number of inner graphs of this subject.
	 * @return The number of inner graphs.
	 */
	public int getInnerGraphCount();

	/*
	 * Methods related to statements
	 */

	/**
	 * Creates a new statement and adds it to the subject.
	 * Note: If the predicate exists already and has references or a literal,
	 * the returned statement will contain one of these references or the literal.
	 * @param predicateXri The predicate XRI of the statement. 
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment predicateXri);

	/**
	 * Creates a new statement and adds it to the subject.
	 * @param predicateXri The predicate XRI of the statement. 
	 * @param referenceXri The reference XRI of the statement.
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment predicateXri, XRI3Segment referenceXri);

	/**
	 * Creates a new statement and adds it to the subject.
	 * @param predicateXri The predicate XRI of the statement. 
	 * @param data The literal data of the statement.
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment predicateXri, String data);

	/**
	 * Creates a new statement and adds it to the subject.
	 * @param predicateXri The predicate XRI of the statement. 
	 * @param graph The inner graph of the statement. 
	 * This graph will be copied. If it is null, a blank inner graph will be created.
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment predicateXri, Graph graph);
	
	/**
	 * Lists all statements rooted in this subject.
	 * @return An iterator over statements.
	 */
	public Iterator<Statement> getStatements();

	/**
	 * Lists all statements with a given predicate XRI.
	 * @param predicateXri The predicate XRI of the statements.
	 * @return An iterator over all statements.
	 */
	public Iterator<Statement> getStatements(XRI3Segment predicateXri);

	/**
	 * Gets a single statement with a given predicate XRI.
	 * @param predicateXri The predicate XRI of the statements.
	 * @return An iterator over all statements.
	 */
	public Statement getStatement(XRI3Segment predicateXri);

	/**
	 * Gets a single statement with a given predicate XRI and reference XRI.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param referenceXri The reference XRI of the statement.
	 * @return An iterator over all statements.
	 */
	public Statement getStatement(XRI3Segment predicateXri, XRI3Segment referenceXri);

	/**
	 * Gets a single statement with a given predicate XRI and literal data.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param data The literal data of the statement.
	 * @return A statement.
	 */
	public Statement getStatement(XRI3Segment predicateXri, String data);

	/**
	 * Gets a single statement with a given predicate XRI and inner graph.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param graph The inner graph of the statement. 
	 * @return A statement.
	 */
	public Statement getStatement(XRI3Segment predicateXri, Graph graph);

	/**
	 * Checks if a statement with a given predicate XRI exists in the subject.
	 * @param predicateXri The predicate XRI of the statement.
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment predicateXri);

	/**
	 * Checks if a statement with a given predicate XRI and reference XRI exists in the subject.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param referenceXri The reference XRI of the statement.
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment predicateXri, XRI3Segment referenceXri);

	/**
	 * Checks if a statement with a given predicate XRI and literal data exists in the subject.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param data The literal data of the statement. 
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment predicateXri, String data);

	/**
	 * Checks if a statement with a given predicate XRI and inner graph exists in the subject.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param graph The inner graph of the statement. 
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment predicateXri, Graph graph);

	/**
	 * Deletes all statements.
	 */
	public void deleteStatements();

	/**
	 * Deletes all statements with a given predicate XRI.
	 * @param predicateXri The predicate XRI of the statements.
	 */
	public void deleteStatements(XRI3Segment predicateXri);

	/**
	 * Deletes a statement with a given predicate XRI and reference XRI.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param referenceXri The reference XRI of the statement.
	 */
	public void deleteStatement(XRI3Segment predicateXri, XRI3Segment referenceXri);

	/**
	 * Deletes a statement with a given predicate XRI and literal data.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param data The literal data of the statement.
	 */
	public void deleteStatement(XRI3Segment predicateXri, String data);

	/**
	 * Deletes a statement with a given predicate XRI and inner graph
	 * @param predicateXri The predicate XRI of the statement.
	 * @param innerGraph The inner graph of the statement.
	 */
	public void deleteStatement(XRI3Segment predicateXri, Graph innerGraph);

	/**
	 * Returns the total number of statements this subject is part of. 
	 * @return The number of statements.
	 */
	public int getStatementCount();

	/**
	 * Returns the total number of statements in the graph with a given predicate XRI.
	 * @param predicateXri The predicate XRI of the statement.
	 * @return The number of statements.
	 */
	public int getStatementCount(XRI3Segment predicateXri);

	/*
	 * Methods related to comments
	 */

	/**
	 * Gets all comments in the subject.
	 * @return An iterator over comments.
	 */
	public Iterator<String> getComments();

	/**
	 * Returns the total number of comments in the subject. 
	 * @return The number of comments.
	 */
	public int getCommentCount();
}
