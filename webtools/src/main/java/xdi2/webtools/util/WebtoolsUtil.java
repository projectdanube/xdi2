package xdi2.webtools.util;

import java.util.Iterator;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.EdgeType;

public class WebtoolsUtil {

	private WebtoolsUtil() { }

	public static DirectedGraph<Object, Statement> JUNGDirectedGraphFromGraph(Graph graph) {

		DirectedGraph<Object, Statement> directedGraph = new DirectedSparseGraph<Object, Statement> ();

		for (Iterator<Statement> statements = graph.getRootContextNode().getAllStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();

			if (statement instanceof ContextNodeStatement) {

				directedGraph.addVertex(((ContextNodeStatement) statement).getContextNode().getContextNode());
				directedGraph.addVertex(((ContextNodeStatement) statement).getContextNode());
				directedGraph.addEdge(statement, ((ContextNodeStatement) statement).getContextNode().getContextNode(), ((ContextNodeStatement) statement).getContextNode(), EdgeType.DIRECTED);
			}

			if (statement instanceof RelationStatement) {

				directedGraph.addVertex(((RelationStatement) statement).getRelation().getContextNode());
				directedGraph.addVertex(((RelationStatement) statement).getRelation().follow());
				directedGraph.addEdge(statement, ((RelationStatement) statement).getRelation().getContextNode(), ((RelationStatement) statement).getRelation().follow(), EdgeType.DIRECTED);
			}

			if (statement instanceof LiteralStatement) {

				directedGraph.addVertex(((LiteralStatement) statement).getLiteral().getContextNode());
				directedGraph.addVertex(((LiteralStatement) statement).getLiteral());
				directedGraph.addEdge(statement, ((LiteralStatement) statement).getLiteral().getContextNode(), ((LiteralStatement) statement).getLiteral(), EdgeType.DIRECTED);
			}
		}

		return directedGraph;
	}

	public static DelegateTree<Object, Statement> JUNGDelegateTreeFromGraph(Graph graph) {

		DirectedGraph<Object, Statement> directedGraph = JUNGDirectedGraphFromGraph(graph);

		DelegateTree<Object, Statement> delegateTree = new DelegateTree<Object, Statement> (directedGraph);

		return delegateTree;
	}
}
