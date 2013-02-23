package xdi2.core.util;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.features.roots.InnerRoot;
import xdi2.core.features.roots.Roots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * Various utility methods for working with context nodes, relations and literals.
 * 
 * @author markus
 */
public final class GraphUtil {

	private GraphUtil() { }

	/**
	 * Creates a context node from its components.
	 * @param contextNodeXri The XRI of the context node.
	 * @return A context node.
	 */
	public static ContextNode contextNodeFromComponents(XDI3Segment contextNodeXri) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.findContextNode(contextNodeXri, true);
	}

	/**
	 * Creates a relation from its components.
	 * @param contextNodeXri The relation XRI of the context node containing the relation.
	 * @param arcXri The arc XRI of the relation.
	 * @param targetContextNodeXri The target context node XRI of the relation.
	 * @return A relation.
	 */
	public static Relation relationFromComponents(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.findContextNode(contextNodeXri, true).createRelation(arcXri, targetContextNodeXri);
	}

	/**
	 * Creates a literal from its components.
	 * @param contextNodeXri The XRI of the context node containing the literal.
	 * @param literalData The literal data of the literal.
	 * @return A literal.
	 */
	public static Literal literalFromComponents(XDI3Segment contextNodeXri, String literalData) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph.findContextNode(contextNodeXri, true).createLiteral(literalData);
	}

	/**
	 * Creates an XDI inner root from its components.
	 * @param subject The subject XRI of the XDI inner root.
	 * @param predicate The predicate XRI of the XDI inner root.
	 * @pram statementXris
	 * @return An XDI inner root.
	 */
	public static InnerRoot innerRootFromComponents(XDI3Segment subject, XDI3Segment predicate, Iterator<XDI3Statement> statementXris) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		InnerRoot innerRoot = Roots.findLocalRoot(graph).findInnerRoot(subject, predicate, true);
		while (statementXris.hasNext()) innerRoot.createRelativeStatement(statementXris.next());

		return innerRoot;
	}
}
