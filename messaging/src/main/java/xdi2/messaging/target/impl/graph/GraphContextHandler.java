package xdi2.messaging.target.impl.graph;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.VariableUtil;
import xdi2.core.util.AddressUtil;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.impl.AbstractContextHandler;

public class GraphContextHandler extends AbstractContextHandler {

	private Graph graph;

	public GraphContextHandler(Graph graph) {

		super();

		this.graph = graph;
	}

	public Graph getGraph() {

		return this.graph;
	}

	/*
	 * Operations on statements
	 */

	@Override
	public void executeSetOnStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().setStatement(targetStatement);
	}

	/*
	 * Operations on addresses
	 */

	@Override
	public void executeGetOnAddress(XDIAddress targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().getDeepContextNode(targetAddress, true);
		if (contextNode == null) return;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);

		for (XdiInnerRoot xdiInnerRoot : XdiLocalRoot.findLocalRoot(messageResult.getGraph()).getInnerRoots()) {

			contextNode = this.getGraph().getDeepContextNode(xdiInnerRoot.getContextNode().getAddress(), true);

			CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
		}
	}

	@Override
	public void executeSetOnAddress(XDIAddress contextNodeAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().setDeepContextNode(contextNodeAddress);
	}

	@Override
	public void executeDelOnAddress(XDIAddress contextNodeAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (XDIConstants.XDI_ADD_ROOT.equals(contextNodeAddress)) {

			this.getGraph().clear();
		} else if (contextNodeAddress.getNumArcs() == 1) {

			this.getGraph().getRootContextNode(false).delContextNode(contextNodeAddress.getFirstArc());
		} else {

			XDIAddress parentcontextNodeAddress = AddressUtil.parentAddress(contextNodeAddress, -1);
			XDIArc arc = AddressUtil.localAddress(contextNodeAddress, 1).getFirstArc();

			ContextNode parentContextNode = this.getGraph().getDeepContextNode(parentcontextNodeAddress, false);
			if (parentContextNode == null) return;

			parentContextNode.delContextNode(arc);
		}
	}

	/*
	 * Operations on context node statements
	 */

	@Override
	public void executeGetOnContextNodeStatement(XDIStatement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetContextNodeAddress = contextNodeStatement.getTargetContextNodeAddress();

		ContextNode contextNode = this.getGraph().getDeepContextNode(targetContextNodeAddress, false);
		if (contextNode == null) return;

		CopyUtil.copyStatement(contextNode.getStatement(), messageResult.getGraph(), null);
	}

	/*
	 * Operations on relation statements
	 */

	@Override
	public void executeGetOnRelationStatement(XDIStatement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeAddress = relationStatement.getContextNodeAddress();
		XDIAddress arc = relationStatement.getRelationAddress();
		XDIAddress targetContextNodeAddress = relationStatement.getTargetContextNodeAddress();

		if (VariableUtil.isVariable(targetContextNodeAddress)) {

			Iterator<Relation> relations;

			if (VariableUtil.isVariable(arc)) {

				relations = this.getGraph().getDeepRelations(contextNodeAddress);
			} else {

				relations = this.getGraph().getDeepRelations(contextNodeAddress, arc);
			}

			while (relations.hasNext()) CopyUtil.copyStatement(relations.next().getStatement(), messageResult.getGraph(), null);
		} else {

			Relation relation = this.getGraph().getDeepRelation(contextNodeAddress, arc, targetContextNodeAddress);
			if (relation == null) return;

			CopyUtil.copyStatement(relation.getStatement(), messageResult.getGraph(), null);
		}
	}

	@Override
	public void executeSetOnRelationStatement(XDIStatement relationStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeAddress = relationStatement.getContextNodeAddress();
		XDIAddress arc = relationStatement.getRelationAddress();
		XDIAddress targetContextNodeAddress = relationStatement.getTargetContextNodeAddress();

		this.getGraph().setDeepRelation(contextNodeAddress, arc, targetContextNodeAddress);
	}

	@Override
	public void executeDelOnRelationStatement(XDIStatement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeAddress = relationStatement.getContextNodeAddress();
		XDIAddress arc = relationStatement.getRelationAddress();
		XDIAddress targetContextNodeAddress = relationStatement.getTargetContextNodeAddress();

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeAddress, false);
		if (contextNode == null) return;

		if (VariableUtil.isVariable(targetContextNodeAddress)) {

			if (VariableUtil.isVariable(arc)) {

				contextNode.delRelations();
			} else {

				contextNode.delRelations(arc);
			}
		} else {

			contextNode.delRelation(arc, targetContextNodeAddress);
		}
	}

	/*
	 * Operations on literal statements
	 */

	@Override
	public void executeGetOnLiteralStatement(XDIStatement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeAddress = literalStatement.getContextNodeAddress();
		Object literalData = literalStatement.getLiteralData();

		Literal literal = this.getGraph().getDeepLiteral(contextNodeAddress, literalData);
		if (literal == null) return;

		CopyUtil.copyStatement(literal.getStatement(), messageResult.getGraph(), null);
	}

	@Override
	public void executeSetOnLiteralStatement(XDIStatement literalStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeAddress = literalStatement.getContextNodeAddress();
		Object literalData = literalStatement.getLiteralData();

		this.getGraph().setDeepLiteral(contextNodeAddress, literalData);
	}

	@Override
	public void executeDelOnLiteralStatement(XDIStatement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeAddress = literalStatement.getContextNodeAddress();
		Object literalData = literalStatement.getLiteralData();

		Literal literal = this.getGraph().getDeepLiteral(contextNodeAddress, literalData);
		if (literal == null) return;

		literal.delete();
	}
}
