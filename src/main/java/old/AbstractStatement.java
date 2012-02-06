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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.GraphComponent;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Statement;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.io.XDIWriter;
import org.eclipse.higgins.xdi4j.io.XDIWriterRegistry;

public abstract class AbstractStatement implements Statement {

	private static final long serialVersionUID = -7920510665706916558L;

	/*
	 * General methods
	 */

	public void delete() {

		Subject subject = this.getSubject();

		if (this.containsPredicate()) {

			Predicate predicate = this.getPredicate();

			if (this.containsReference()) {

				Reference reference = this.getReference();

				reference.deleteFromPredicate();
				if (! predicate.containsReferences()) predicate.deleteFromSubject();
				if (! subject.containsPredicates()) subject.deleteFromGraph();
			} else if (this.containsLiteral()) {

				Literal literal = this.getLiteral();

				literal.deleteFromPredicate();
				predicate.deleteFromSubject();
				if (! subject.containsPredicates()) subject.deleteFromGraph();
			} else if (this.containsInnerGraph()) {

				Graph innerGraph = this.getInnerGraph();

				innerGraph.deleteFromPredicate();
				predicate.deleteFromSubject();
				if (! subject.containsPredicates()) subject.deleteFromGraph();
			} else {

				predicate.deleteFromSubject();
				if (! subject.containsPredicates()) subject.deleteFromGraph();
			}
		} else {

			subject.deleteFromGraph();
		}
	}

	public boolean containsPredicate() {

		return(this.getPredicate() != null);
	}

	public boolean containsReference() {

		return(this.getReference() != null);
	}

	public boolean containsLiteral() {

		return(this.getLiteral() != null);
	}

	public boolean containsInnerGraph() {

		return(this.getInnerGraph() != null);
	}

	public boolean containsSameGraphComponents(Statement other) {

		if (this.containsInnerGraph() && ! other.containsInnerGraph()) return(false);
		if (! this.containsInnerGraph() && other.containsInnerGraph()) return(false);

		if (this.containsLiteral() && ! other.containsLiteral()) return(false);
		if (! this.containsLiteral() && other.containsLiteral()) return(false);

		if (this.containsReference() && ! other.containsReference()) return(false);
		if (! this.containsReference() && other.containsReference()) return(false);

		if (this.containsPredicate() && ! other.containsPredicate()) return(false);
		if (! this.containsPredicate() && other.containsPredicate()) return(false);

		return(true);
	}

	public boolean startsWith(Statement other) {

		if (! this.getSubject().equals(other.getSubject())) return(false);

		if (! this.containsPredicate()) return(true);
		if (! other.containsPredicate()) return(false);
		if (! this.getPredicate().equals(other.getPredicate())) return(false);

		if (this.containsReference()) {

			if (! other.containsReference()) return(false);
			return(other.getReference().equals(this.getReference()));
		}

		if (this.containsLiteral()) {

			if (! other.containsLiteral()) return(false);
			return(other.getLiteral().equals(this.getLiteral()));
		}

		if (this.containsInnerGraph()) {

			if (! other.containsInnerGraph()) return(false);
			return(other.getInnerGraph().equals(this.getInnerGraph()));
		}

		return(true);
	}

	public int getSize() {

		if (this.containsPredicate()) {

			if (this.containsReference()) return(3);
			if (this.containsLiteral()) return(3);
			if (this.containsInnerGraph()) return(3);

			return(2);
		}

		return(1);
	}

	public GraphComponent getLeafGraphComponent() {

		if (this.containsPredicate()) {

			if (this.containsReference()) return(this.getReference());
			if (this.containsLiteral()) return(this.getLiteral());
			if (this.containsInnerGraph()) return(this.getInnerGraph());

			return(this.getPredicate());
		}

		return(this.getSubject());
	}

	public String toString(String format, Properties parameters) {

		XDIWriter writer = XDIWriterRegistry.forFormat(format);
		StringWriter buffer = new StringWriter();

		try {

			writer.write(this, buffer, parameters);
		} catch (IOException ex) {
		
			return("[Exception: " + ex.getMessage() + "]");
		}

		return(buffer.toString());
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return(this.toString(XDIWriterRegistry.getDefault().getFormat(), null));
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Statement)) return(false);
		if (object == this) return(true);

		Statement other = (Statement) object;

		// two statements are equal if their components are equals

		if (this.getSubject() == null && other.getSubject() != null) return(false);
		if (this.getSubject() != null && other.getSubject() == null) return(false);
		if (this.getSubject() != null && other.getSubject() != null && ! this.getSubject().equals(other.getSubject())) return(false);

		if (this.getPredicate() == null && other.getPredicate() != null) return(false);
		if (this.getPredicate() != null && other.getPredicate() == null) return(false);
		if (this.getPredicate() != null && other.getPredicate() != null && ! this.getPredicate().equals(other.getPredicate())) return(false);

		if (this.getReference() == null && other.getReference() != null) return(false);
		if (this.getReference() != null && other.getReference() == null) return(false);
		if (this.getReference() != null && other.getReference() != null && ! this.getReference().equals(other.getReference())) return(false);

		if (this.getLiteral() == null && other.getLiteral() != null) return(false);
		if (this.getLiteral() != null && other.getLiteral() == null) return(false);
		if (this.getLiteral() != null && other.getLiteral() != null && ! this.getLiteral().equals(other.getLiteral())) return(false);

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.getSubject() == null ? 0 : this.getSubject().hashCode());
		hashCode = (hashCode * 31) + (this.getPredicate() == null ? 0 : this.getPredicate().hashCode());
		hashCode = (hashCode * 31) + (this.getReference() == null ? 0 : this.getReference().hashCode());
		hashCode = (hashCode * 31) + (this.getLiteral() == null ? 0 : this.getLiteral().hashCode());

		return(hashCode);
	}

	public int compareTo(Statement other) {

		if (other == null || other == this) return(0);

		int c;

		// compare subject

		c = this.getSubject().compareTo(other.getSubject());
		if (c != 0) return(c);

		// compare predicate

		if (this.containsPredicate() && other.containsPredicate()) {

			c = this.getPredicate().compareTo(other.getPredicate());
			if (c != 0) return(c);
		}

		// compare references

		if (this.containsReference() && other.containsReference()) {

			c = this.getReference().compareTo(other.getReference());
			if (c != 0) return(c);
		}

		// compare literals

		if (this.containsLiteral() && other.containsLiteral()) {

			c = this.getLiteral().compareTo(other.getLiteral());
			if (c != 0) return(c);
		}

		// other cases

		if (this.containsPredicate() && ! other.containsPredicate()) return(1);
		if (! this.containsPredicate() && other.containsPredicate()) return(1);
		if (this.containsReference() && ! other.containsReference()) return(1);
		if (! this.containsReference() && other.containsReference()) return(1);
		if (this.containsLiteral() && ! other.containsLiteral()) return(1);
		if (! this.containsLiteral() && other.containsLiteral()) return(1);

		return(0);
	}
}
