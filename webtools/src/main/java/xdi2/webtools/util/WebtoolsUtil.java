package xdi2.webtools.util;

import java.util.Iterator;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.features.linkcontracts.policy.Policy;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.util.EdgeType;

public class WebtoolsUtil {

	private WebtoolsUtil() { }

	public static DelegateTree<Object, Statement> JUNGDelegateTreeFromGraph(Graph graph) {

		DelegateTree<Object, Statement> delegateTree = new DelegateTree<Object, Statement> ();

		delegateTree.setRoot(graph.getRootContextNode());

		for (Iterator<Statement> statements = graph.getRootContextNode().getAllStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();

			if (statement instanceof ContextNodeStatement) {

				delegateTree.addChild(statement, ((ContextNodeStatement) statement).getContextNode().getContextNode(), ((ContextNodeStatement) statement).getContextNode(), EdgeType.DIRECTED);
			}

			if (statement instanceof RelationStatement) {

				delegateTree.addChild(statement, ((RelationStatement) statement).getRelation().getContextNode(), ((RelationStatement) statement).getRelation().follow(), EdgeType.DIRECTED);
			}

			if (statement instanceof LiteralStatement) {

				delegateTree.addChild(statement, ((LiteralStatement) statement).getLiteral().getContextNode(), ((LiteralStatement) statement).getLiteral(), EdgeType.DIRECTED);
			}
		}

		return delegateTree;
	}

	private static void addPolicy(DelegateTree<Object, Object> delegateTree, Policy policy) {

		for (Iterator<Policy> subPolicies = policy.getPolicies(); subPolicies.hasNext(); ) {

			Policy subPolicy = subPolicies.next();

			delegateTree.addChild(subPolicy, policy, subPolicy, EdgeType.DIRECTED);
			addPolicy(delegateTree, subPolicy);
		}

		for (Iterator<PolicyStatement> policyStatements = policy.getPolicyStatements(); policyStatements.hasNext(); ) {

			PolicyStatement policyStatement = policyStatements.next();

			delegateTree.addChild(policyStatement, policy, policyStatement, EdgeType.DIRECTED);
		}
	}
	
	public static DelegateTree<Object, Object> JUNGDelegateTreeFromPolicy(PolicyRoot policyRoot) {

		DelegateTree<Object, Object> delegateTree = new DelegateTree<Object, Object> ();

		delegateTree.setRoot(policyRoot);
		addPolicy(delegateTree, policyRoot);

		return delegateTree;
	}
}
