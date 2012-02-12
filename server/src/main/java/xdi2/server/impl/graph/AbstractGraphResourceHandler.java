package xdi2.server.impl.graph;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.messaging.Operation;
import xdi2.server.impl.AbstractResourceHandler;

public abstract class AbstractGraphResourceHandler extends AbstractResourceHandler {

	protected Graph graph;

	public AbstractGraphResourceHandler(Operation operation, ContextNode operationContextNode, Graph graph) {

		super(operation, operationContextNode);

		this.graph = graph;
	}

	public AbstractGraphResourceHandler(Operation operation, Relation operationRelation, Graph graph) {

		super(operation, operationRelation);

		this.graph = graph;
	}

	public AbstractGraphResourceHandler(Operation operation, Literal operationLiteral, Graph graph) {

		super(operation, operationLiteral);

		this.graph = graph;
	}
}
