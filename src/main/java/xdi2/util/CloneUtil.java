/*******************************************************************************
 * Copyright (c) 2008 Parity Communications, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Sabadello - Initial API and implementation
 *******************************************************************************/
package xdi2.util;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.impl.memory.MemoryGraphFactory;
import org.eclipse.higgins.xdi4j.messaging.Message;
import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;
import org.eclipse.higgins.xdi4j.messaging.Operation;
import org.eclipse.higgins.xdi4j.util.CopyUtil;

/**
 * Various utility methods for cloning graph components.
 * 
 * @author msabadello at parityinc dot net
 */
public final class CloneUtil {

	protected static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private CloneUtil() { }

	/**
	 * Creates a copy of the given graph containing the same statements.
	 * @param graph The graph to clone.
	 * @return The cloned graph.
	 */
	public static Graph cloneGraph(Graph graph) {

		Graph temp = graphFactory.openGraph();

		return((Graph) CopyUtil.copyStatements(graph, temp, null));
	}

	/**
	 * Creates a copy of the given subject containing the same statements.
	 * @param subject The subject to clone.
	 * @return The cloned subject.
	 */
	public static Subject cloneSubject(Subject subject) {

		Graph temp = graphFactory.openGraph();

		return((Subject) CopyUtil.copyStatements(subject, temp, null));
	}

	/**
	 * Creates a copy of the given predicate containing the same statements.
	 * @param predicate The predicate to clone.
	 * @return The cloned predicate.
	 */
	public static Predicate clonePredicate(Predicate predicate) {

		Graph temp = graphFactory.openGraph();

		return((Predicate) CopyUtil.copyStatements(predicate, temp, null));
	}

	/**
	 * Creates a copy of the given reference containing the same statements.
	 * @param reference The reference to clone.
	 * @return The cloned reference.
	 */
	public static Reference cloneReference(Reference reference) {

		Graph temp = graphFactory.openGraph();

		return((Reference) CopyUtil.copyStatements(reference, temp, null));
	}

	/**
	 * Creates a copy of the given literal containing the same statements.
	 * @param literal The literal to clone.
	 * @return The cloned literal.
	 */
	public static Literal cloneLiteral(Literal literal) {

		Graph temp = graphFactory.openGraph();

		return((Literal) CopyUtil.copyStatements(literal, temp, null));
	}

	/**
	 * Creates a copy of the given message envelope containing the same messages.
	 * @param messageEnvelope The message envelope to clone.
	 * @return The cloned message envelope.
	 */
	public static MessageEnvelope cloneMessageEnvelope(MessageEnvelope messageEnvelope) {

		return MessageEnvelope.fromGraph(cloneGraph(messageEnvelope.getGraph()));
	}

	/**
	 * Creates a copy of the given message containing the same operations.
	 * @param message The message to clone.
	 * @return The cloned message.
	 */
	public static Message cloneMessage(Message message) {

		return Message.fromSubject(cloneSubject(message.getSubject()));
	}

	/**
	 * Creates a copy of the given operation containing the same operation graph.
	 * @param operation The operation to clone.
	 * @return The cloned operation.
	 */
	public static Operation cloneOperation(Operation operation) {

		return Operation.fromPredicate(clonePredicate(operation.getPredicate()));
	}
}
