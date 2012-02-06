package old;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.higgins.xdi4j.exceptions.MessagingException;

import xdi2.xri3.impl.XRI3;
import xdi2.xri3.impl.XRI3Segment;

/**
 * This interface represents a whole XDI graph.
 * XDI graphs consist of subjects and statements between them.
 * Operations on the graph include creating new statements and subjects, finding and
 * manipulating them.
 * 
 * @author markus
 */
public interface Graph extends GraphComponent, Serializable, Comparable<Graph> {

	/*
	 * General methods
	 */

	/**
	 * Gets the predicate that has this inner graph as its target.
	 * @return A predicate.
	 */
	public Predicate getPredicate();

	/**
	 * Checks if this graph is an inner graph.
	 * @return True, if this graph is an inner graph.
	 */
	public boolean isInnerGraph();

	/**
	 * Deletes this inner graph from the its predicate.
	 */
	public void deleteFromPredicate();

	/**
	 * Clears the graph. 
	 * This is equivalent to calling deleteSubjects().
	 */
	public void clear();

	/**
	 * Checks if the graph is empty. 
	 * This is equivalent to calling ! containsSubjects().
	 */
	public boolean isEmpty();

	/**
	 * Closes the graph. This should be called when work on the graph is done.
	 */
	public void close();

	/**
	 * Converts the graph to a string in the given serialization format.
	 * @param parameters Parameters for the serialization.
	 */
	public String toString(String format);

	/**
	 * Converts the graph to a string in the given serialization format.
	 * @param format The serialization format.
	 * @param parameters Parameters for the serialization.
	 */
	public String toString(String format, Properties parameters);

	/*
	 * Methods related to subjects of the graph
	 */

	/**
	 * Creates a new subject and adds it to the graph (if not already present).
	 * @param subjectXri The XRI of the new subject.
	 * @return The newly created (or already existing) subject.
	 */
	public Subject createSubject(XRI3Segment subjectXri);

	/**
	 * Returns the subjects of this graph.
	 * @return An iterator over subjects.
	 */
	public Iterator<Subject> getSubjects();

	/**
	 * Returns the subjects of this graph that start with a given XRI.
	 * @return An iterator over subjects.
	 */
	public Iterator<Subject> getSubjectsStartingWith(XRI3Segment subjectXri);

	/**
	 * Finds and returns a subject with a given XRI. 
	 * @param subjectXri The subject XRI to look for. 
	 * @return The subject with the given XRI, or null.
	 */
	public Subject getSubject(XRI3Segment subjectXri);

	/**
	 * Returns the predicates of this graph.
	 * @return An iterator over predicates.
	 */
	public Iterator<Predicate> getPredicates();

	/**
	 * Returns the references of this graph.
	 * @return An iterator over references.
	 */
	public Iterator<Reference> getReferences();

	/**
	 * Returns the literals of this graph.
	 * @return An iterator over literals.
	 */
	public Iterator<Literal> getLiterals();

	/**
	 * Returns the inner graphs of this graph.
	 * @return An iterator over inner graphs.
	 */
	public Iterator<Graph> getInnerGraphs();

	/**
	 * Returns all graph components in this graph.
	 * @param True, if the graph components inside inner graphs should also be considered.
	 * @return An iterator over graph components.
	 */
	public Iterator<GraphComponent> getGraphComponents(boolean deep);

	/**
	 * Returns all graph components of a certain kind (class) in this graph.
	 * @param True, if the graph components inside inner graphs should also be considered.
	 * @return An iterator over graph components.
	 */
	public Iterator<GraphComponent> getGraphComponents(boolean deep, final Class<? extends GraphComponent> clazz);

	/**
	 * Returns all leaf graph components in this graph.
	 * @param True, if the graph components inside inner graphs should also be considered.
	 * @return An iterator over graph components.
	 */
	public Iterator<GraphComponent> getLeafGraphComponents(boolean deep);

	/**
	 * Returns all leaf graph components of a certain kind (class) in this graph.
	 * @param True, if the graph components inside inner graphs should also be considered.
	 * @return An iterator over graph components.
	 */
	public Iterator<GraphComponent> getLeafGraphComponents(boolean deep, final Class<? extends GraphComponent> clazz);

	/**
	 * Checks if the graph has one or more subjects.
	 * @return True if this graph has subjects.
	 */
	public boolean containsSubjects();

	/**
	 * Checks if a subject with a given XRI exists in the graph.
	 * @param subjectXri The subject XRI to look for. 
	 * @return True if this graph has a subject with the given XRI.
	 */
	public boolean containsSubject(XRI3Segment subjectXri);

	/**
	 * Deletes all subjects from this graph.
	 */
	public void deleteSubjects();

	/**
	 * Deletes the subject with a given subject XRI from this subject.
	 * @param subjectXri The subject XRI of the subject.
	 */
	public void deleteSubject(XRI3Segment subjectXri);

	/**
	 * Returns the number of subjects of this graph.
	 * @return The number of subjects.
	 */
	public int getSubjectCount();

	/**
	 * Returns the number of predicates of this graph.
	 * @return The number of predicates.
	 */
	public int getPredicateCount();

	/**
	 * Returns the number of references of this graph.
	 * @return The number of references.
	 */
	public int getReferenceCount();

	/**
	 * Returns the number of literals of this graph.
	 * @return The number of literals.
	 */
	public int getLiteralCount();

	/**
	 * Returns the number of inner graphs of this graph.
	 * @return The number of inner graphs.
	 */
	public int getInnerGraphCount();

	/**
	 * Returns the number of subjects, predicates, references, literals and inner graphs.
	 * @param True, if the graph components inside inner graphs should also be considered.
	 * @return The number of all graph components combined.
	 */
	public int getGraphComponentCount(boolean deep);

	/*
	 * Methods related to statements
	 */

	/**
	 * Creates a new statement and adds it to the graph.
	 * Note: If the subject exists already and has predicates,
	 * the returned statement will contain one of these predicates.
	 * @param subjectXri The subject XRI of the statement.
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment subjectXri);

	/**
	 * Creates a new statement and adds it to the graph.
	 * Note: If the subject/predicate exists already and has references or a literal,
	 * the returned statement will contain one of these references or the literal.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement. 
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment subjectXri, XRI3Segment predicateXri);

	/**
	 * Creates a new statement and adds it to the graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement. 
	 * @param referenceXri The reference XRI of the statement.
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, XRI3Segment referenceXri);

	/**
	 * Creates a new statement and adds it to the graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement. 
	 * @param data The literal data of the statement.
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, String data);

	/**
	 * Creates a new statement and adds it to the graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement. 
	 * @param innerGraph The inner graph of the statement. 
	 * This graph will be copied. If it is null, a blank inner graph will be created.
	 * @return The newly created statement.
	 */
	public Statement createStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, Graph innerGraph);

	/**
	 * Lists all statements rooted in this graph.
	 * @return An iterator over statements.
	 */
	public Iterator<Statement> getStatements();

	/**
	 * Lists all statements with a given subject XRI.
	 * @param subjectXri The subject XRI of the statements.
	 * @return An iterator over all statements.
	 */
	public Iterator<Statement> getStatements(XRI3Segment subjectXri);

	/**
	 * Gets a single statement with a given subject XRI.
	 * @param subjectXri The subject XRI of the statements.
	 * @return A statement.
	 */
	public Statement getStatement(XRI3Segment subjectXri);

	/**
	 * Lists all statements with a given subject XRI and predicate XRI.
	 * @param subjectXri The subject XRI of the statements.
	 * @param predicateXri The predicate XRI of the statements.
	 * @return An iterator over all statements.
	 */
	public Iterator<Statement> getStatements(XRI3Segment subjectXri, XRI3Segment predicateXri);

	/**
	 * Gets a single statement with a given subject XRI and predicate XRI.
	 * @param subjectXri The subject XRI of the statements.
	 * @param predicateXri The predicate XRI of the statements.
	 * @return A statement.
	 */
	public Statement getStatement(XRI3Segment subjectXri, XRI3Segment predicateXri);

	/**
	 * Gets a single statement with a given subject XRI, predicate XRI and reference XRI.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param referenceXri The reference XRI of the statement.
	 * @return A statement.
	 */
	public Statement getStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, XRI3Segment referenceXri);

	/**
	 * Gets a single statement with a given subject XRI, predicate XRI and literal data.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param data The literal data of the statement.
	 * @return A statement.
	 */
	public Statement getStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, String data);

	/**
	 * Gets a single statement with a given subject XRI, predicate XRI and inner graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param innerGraph The inner graph of the statement. 
	 * @return A statement.
	 */
	public Statement getStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, Graph innerGraph);

	/**
	 * Checks if this statement (or one equals to it) exists in the graph.
	 * @param statement The statement to look for.
	 * @return True if this graph has the given or an equal statement.
	 */
	public boolean containsStatement(Statement statement);

	/**
	 * Checks if a statement with a given subject XRI exists in the graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment subjectXri);

	/**
	 * Checks if a statement with a given subject XRI and predicate XRI exists in the graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment subjectXri, XRI3Segment predicateXri);

	/**
	 * Checks if a statement with a given subject XRI, predicate XRI and reference XRI exists in the graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param referenceXri The reference XRI of the statement.
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, XRI3Segment referenceXri);

	/**
	 * Checks if a statement with a given subject XRI, predicate XRI and literal data exists in the graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param data The literal data of the statement. 
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, String data);

	/**
	 * Checks if a statement with a given subject XRI, predicate XRI and inner graph exists in the graph.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param innerGraph The inner graph of the statement. 
	 * @return True if a statement exists in the graph.
	 */
	public boolean containsStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, Graph innerGraph);

	/**
	 * Deletes all statements.
	 */
	public void deleteStatements();

	/**
	 * Deletes all statements with a given subject XRI.
	 * This is functionally equivalent to deleteSubject(subjectXri).
	 * @param subjectXri The subject XRI of the statements.
	 */
	public void deleteStatements(XRI3Segment subjectXri);

	/**
	 * Deletes all statements with a given subject XRI and predicate XRI.
	 * @param subjectXri The subject XRI of the statements.
	 * @param predicateXri The predicate XRI of the statements.
	 */
	public void deleteStatements(XRI3Segment subjectXri, XRI3Segment predicateXri);

	/**
	 * Deletes a statement with a given subject XRI, predicate XRI and reference XRI.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param referenceXri The reference XRI of the statement.
	 */
	public void deleteStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, XRI3Segment referenceXri);

	/**
	 * Deletes a statement with a given subject XRI, predicate XRI and literal data.
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param data The literal data of the statement.
	 */
	public void deleteStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, String data);

	/**
	 * Deletes a statement with a given subject XRI, predicate XRI and inner graph
	 * @param subjectXri The subject XRI of the statement.
	 * @param predicateXri The predicate XRI of the statement.
	 * @param innerGraph The inner graph of the statement.
	 */
	public void deleteStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, Graph innerGraph);

	/**
	 * Returns the total number of statements in the graph. 
	 * @return The number of statements.
	 */
	public int getStatementCount();

	/**
	 * Returns the total number of statements in the graph with a given subject XRI.
	 * @param subjectXri The subject XRI of the statements.
	 * @return The number of statements.
	 */
	public int getStatementCount(XRI3Segment subjectXri);

	/**
	 * Returns the total number of statements in the graph with a given subject XRI and predicate XRI.
	 * @param subjectXri The subject XRI of the statements.
	 * @param predicateXri The predicate XRI of the statement.
	 * @return The number of statements.
	 */
	public int getStatementCount(XRI3Segment subjectXri, XRI3Segment predicateXri);

	/**
	 * Gets the statement that contains this graph.
	 * @return A statement.
	 */
	public Statement getStatement();

	/*
	 * Methods related to messages
	 */

	/**
	 * A simple way to apply an XDI operation to this graph,
	 * based on a given input graph.
	 */
	public Graph applyOperation(Graph operationGraph, XRI3Segment operationXri) throws MessagingException;

	/**
	 * A simple way to apply an XDI operation to this graph,
	 * based on a given input address.
	 */
	public Graph applyOperation(XRI3 address, XRI3Segment operationXri) throws MessagingException;

	/*
	 * Methods related to comments
	 */

	/**
	 * Gets all comments in the graph.
	 * @return An iterator over comments.
	 */
	public Iterator<String> getComments();

	/**
	 * Returns the total number of comments in the graph. 
	 * @return The number of comments.
	 */
	public int getCommentCount();

	/*
	 * Methods related to transactions
	 */

	/**
	 * Starts a new transaction.
	 */
	public void beginTransaction();

	/**
	 * Commits the changes made by the transaction.
	 */
	public void commitTransaction();

	/**
	 * Rolls back the changes made by the transaction.
	 */
	public void rollbackTransaction();
}
