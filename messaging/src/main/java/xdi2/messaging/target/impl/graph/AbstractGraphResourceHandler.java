package xdi2.messaging.target.impl.graph;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.messaging.Operation;
import xdi2.messaging.target.impl.AbstractResourceHandler;

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
