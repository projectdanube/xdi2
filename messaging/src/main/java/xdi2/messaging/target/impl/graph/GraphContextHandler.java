package xdi2.messaging.target.impl.graph;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.VariableUtil;
import xdi2.core.util.XDIAddressUtil;
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

		for (XdiInnerRoot xdiInnerRoot : XdiCommonRoot.findCommonRoot(messageResult.getGraph()).getInnerRoots()) {

			contextNode = this.getGraph().getDeepContextNode(xdiInnerRoot.getContextNode().getXDIAddress(), true);

			CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
		}
	}

	@Override
	public void executeSetOnAddress(XDIAddress contextNodeXDIAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().setDeepContextNode(contextNodeXDIAddress);
	}

	@Override
	public void executeDelOnAddress(XDIAddress contextNodeXDIAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (XDIConstants.XDI_ADD_ROOT.equals(contextNodeXDIAddress)) {

			this.getGraph().clear();
		} else if (contextNodeXDIAddress.getNumXDIArcs() == 1) {

			this.getGraph().getRootContextNode(false).delContextNode(contextNodeXDIAddress.getFirstXDIArc());
		} else {

			XDIAddress parentcontextNodeXDIAddress = XDIAddressUtil.parentXDIAddress(contextNodeXDIAddress, -1);
			XDIArc XDIarc = XDIAddressUtil.localXDIAddress(contextNodeXDIAddress, 1).getFirstXDIArc();

			ContextNode parentContextNode = this.getGraph().getDeepContextNode(parentcontextNodeXDIAddress, false);
			if (parentContextNode == null) return;

			parentContextNode.delContextNode(XDIarc);
		}
	}

	/*
	 * Operations on context node statements
	 */

	@Override
	public void executeGetOnContextNodeStatement(XDIStatement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress targetContextNodeXDIAddress = contextNodeStatement.getTargetContextNodeXDIAddress();

		ContextNode contextNode = this.getGraph().getDeepContextNode(targetContextNodeXDIAddress, false);
		if (contextNode == null) return;

		CopyUtil.copyStatement(contextNode.getStatement(), messageResult.getGraph(), null);
	}

	/*
	 * Operations on relation statements
	 */

	@Override
	public void executeGetOnRelationStatement(XDIStatement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeXDIAddress = relationStatement.getContextNodeXDIAddress();
		XDIAddress XDIaddress = relationStatement.getRelationXDIAddress();
		XDIAddress targetContextNodeXDIAddress = relationStatement.getTargetContextNodeXDIAddress();

		if (VariableUtil.isVariable(targetContextNodeXDIAddress)) {

			Iterator<Relation> relations;

			if (VariableUtil.isVariable(XDIaddress)) {

				relations = this.getGraph().getDeepRelations(contextNodeXDIAddress);
			} else {

				relations = this.getGraph().getDeepRelations(contextNodeXDIAddress, XDIaddress);
			}

			while (relations.hasNext()) CopyUtil.copyStatement(relations.next().getStatement(), messageResult.getGraph(), null);
		} else {

			Relation relation = this.getGraph().getDeepRelation(contextNodeXDIAddress, XDIaddress, targetContextNodeXDIAddress);
			if (relation == null) return;

			CopyUtil.copyStatement(relation.getStatement(), messageResult.getGraph(), null);
		}
	}

	@Override
	public void executeSetOnRelationStatement(XDIStatement relationStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeXDIAddress = relationStatement.getContextNodeXDIAddress();
		XDIAddress XDIaddress = relationStatement.getRelationXDIAddress();
		XDIAddress targetContextNodeXDIAddress = relationStatement.getTargetContextNodeXDIAddress();

		this.getGraph().setDeepRelation(contextNodeXDIAddress, XDIaddress, targetContextNodeXDIAddress);
	}

	@Override
	public void executeDelOnRelationStatement(XDIStatement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeXDIAddress = relationStatement.getContextNodeXDIAddress();
		XDIAddress XDIaddress = relationStatement.getRelationXDIAddress();
		XDIAddress targetContextNodeXDIAddress = relationStatement.getTargetContextNodeXDIAddress();

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXDIAddress, false);
		if (contextNode == null) return;

		if (VariableUtil.isVariable(targetContextNodeXDIAddress)) {

			if (VariableUtil.isVariable(XDIaddress)) {

				contextNode.delRelations();
			} else {

				contextNode.delRelations(XDIaddress);
			}
		} else {

			contextNode.delRelation(XDIaddress, targetContextNodeXDIAddress);
		}
	}

	/*
	 * Operations on literal statements
	 */

	@Override
	public void executeGetOnLiteralStatement(XDIStatement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeXDIAddress = literalStatement.getContextNodeXDIAddress();
		Object literalData = literalStatement.getLiteralData();

		Literal literal = this.getGraph().getDeepLiteral(contextNodeXDIAddress, literalData);
		if (literal == null) return;

		CopyUtil.copyStatement(literal.getStatement(), messageResult.getGraph(), null);
	}

	@Override
	public void executeSetOnLiteralStatement(XDIStatement literalStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeXDIAddress = literalStatement.getContextNodeXDIAddress();
		Object literalData = literalStatement.getLiteralData();

		this.getGraph().setDeepLiteral(contextNodeXDIAddress, literalData);
	}

	@Override
	public void executeDelOnLiteralStatement(XDIStatement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeXDIAddress = literalStatement.getContextNodeXDIAddress();
		Object literalData = literalStatement.getLiteralData();

		Literal literal = this.getGraph().getDeepLiteral(contextNodeXDIAddress, literalData);
		if (literal == null) return;

		literal.delete();
	}
}
