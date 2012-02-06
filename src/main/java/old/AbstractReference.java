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
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import xdi2.util.iterators.SingleItemIterator;

public abstract class AbstractReference implements Reference {

	private static final long serialVersionUID = -9055773010138710261L;

	protected Graph containingGraph;

	public AbstractReference(Graph containingGraph) {

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

		this.getPredicate().deleteReference(this.getReferenceXri());
	}

	/*
	 * Methods for dereferencing the reference
	 */

	public Subject dereference() {

		XRI3Segment xriReference = this.getReferenceXri();

		if (xriReference.getFirstSubSegment().isGlobal()) {

			return(dereferenceAbsolute());
		} else {

			return(dereferenceRelative());
		}
	}

	public Subject dereferenceAbsolute() {

		XRI3Segment subjectXri = this.getReferenceXri();

		return(this.getContainingGraph().getSubject(subjectXri));
	}

	public Subject dereferenceRelative() {

		XRI3Segment subjectXri = new XRI3Segment(this.getPredicate().getSubject().getSubjectXri().toString() + this.getReferenceXri().toString());

		return(this.getContainingGraph().getSubject(subjectXri));
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

		return(this.getReferenceXri().toString());
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Reference)) return(false);
		if (object == this) return(true);

		Reference other = (Reference) object;

		// two references are equal if their XRIs are equal

		if (this.getReferenceXri() == null && other.getReferenceXri() != null) return(false);
		if (this.getReferenceXri() != null && other.getReferenceXri() == null) return(false);
		if (this.getReferenceXri() != null && other.getReferenceXri() != null && ! this.getReferenceXri().equals(other.getReferenceXri())) return(false);

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.getReferenceXri() == null ? 0 : this.getReferenceXri().hashCode());

		return(hashCode);
	}

	public int compareTo(Reference other) {

		if (other == null || other == this) return(0);

		return(this.getReferenceXri().compareTo(other.getReferenceXri()));
	}

	/**
	 * A class representing a statement that contains a subject, predicate and reference.
	 */
	private Statement statement = new AbstractStatement() {

		private static final long serialVersionUID = -642673750900733179L;

		public Graph getContainingGraph() {

			return(AbstractReference.this.getContainingGraph());
		}

		public Subject getSubject() {

			return(AbstractReference.this.getPredicate().getSubject());
		}

		public Predicate getPredicate() {

			return(AbstractReference.this.getPredicate());
		}

		public Reference getReference() {

			return(AbstractReference.this);
		}

		public Literal getLiteral() {

			return(null);
		}

		public Graph getInnerGraph() {

			return(null);
		}
	};
}
