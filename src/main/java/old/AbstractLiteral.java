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
package old;

import java.util.Iterator;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.GraphComponent;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Statement;
import org.eclipse.higgins.xdi4j.Subject;

import xdi2.util.iterators.SingleItemIterator;

public abstract class AbstractLiteral implements Literal {

	private static final long serialVersionUID = -3376866498591508078L;

	protected Graph containingGraph;

	public AbstractLiteral(Graph containingGraph) {

		this.containingGraph = containingGraph;
	}

	/*
	 * GraphComponent methods
	 */

	public Graph getContainingGraph() {

		return(this.containingGraph);
	}

	public Graph getTopLevelGraph() {

		Graph topLevelGraph = this.getContainingGraph();

		while (topLevelGraph != null && topLevelGraph.getContainingGraph() != null) topLevelGraph = topLevelGraph.getContainingGraph();

		return(topLevelGraph);
	}

	public GraphComponent getParentGraphComponent() {
		
		return(this.getPredicate());
	}
	
	public boolean isLeaf() {
		
		return(true);
	}

	public boolean containsPreComment() {
		
		return(this.getPreComment() != null);
	}

	public boolean containsPostComment() {
		
		return(this.getPostComment() != null);
	}

	/*
	 * General methods
	 */

	public synchronized void deleteFromPredicate() {

		this.getPredicate().deleteLiteral();
	}
	
	/*
	 * Methods related to statements
	 */

	public Iterator<Statement> getStatements() {

		return(new SingleItemIterator<Statement> (this.getStatement()));
	}

	public int getStatementCount() {
		
		return(1);
	}
	
	public Statement getStatement() {

		return(this.statement);
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return(this.getData());
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Literal)) return(false);
		if (object == this) return(true);

		Literal other = (Literal) object;

		// compare data

		return(this.getData().equals(other.getData()));
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getData().hashCode();

		return(hashCode);
	}

	public int compareTo(Literal other) {

		if (other == null || other == this) return(0);

		// compare data

		return(this.getData().compareTo(other.getData()));
	}

	/**
	 * A class representing a statement that contains a subject, predicate and literal.
	 */
	private Statement statement = new AbstractStatement() {

		private static final long serialVersionUID = -9009447702840932713L;

		public Graph getContainingGraph() {

			return(AbstractLiteral.this.getContainingGraph());
		}

		public Subject getSubject() {

			return(AbstractLiteral.this.getPredicate().getSubject());
		}

		public Predicate getPredicate() {

			return(AbstractLiteral.this.getPredicate());
		}

		public Reference getReference() {

			return(null);
		}

		public Literal getLiteral() {

			return(AbstractLiteral.this);
		}
		
		public Graph getInnerGraph() {
			
			return(null);
		}
	};
}
