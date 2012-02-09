package xdi2.impl;

import xdi2.Statement;

public abstract class AbstractStatement implements Statement {

	private static final long serialVersionUID = -8541962994048481222L;

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append(this.getSubject().getXri());
		builder.append("/");
		builder.append(this.getPredicate());
		builder.append("/");
		builder.append(this.getObject());

		return builder.toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Statement)) return false;
		if (object == this) return true;

		Statement other = (Statement) object;

		// two statements are equal if their components are equals

		if (! this.getSubject().equals(other.getSubject())) return false;
		if (! this.getPredicate().equals(other.getPredicate())) return false;
		if (! this.getObject().equals(other.getObject())) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.getSubject() == null ? 0 : this.getSubject().hashCode());
		hashCode = (hashCode * 31) + (this.getPredicate() == null ? 0 : this.getPredicate().hashCode());
		hashCode = (hashCode * 31) + (this.getObject() == null ? 0 : this.getObject().hashCode());

		return hashCode;
	}

	public int compareTo(Statement other) {

		if (other == null || other == this) return(0);

		int c;

		// compare subject

		c = this.getSubject().compareTo(other.getSubject());
		if (c != 0) return c;

		// compare predicate

		c = this.getPredicate().compareTo(other.getPredicate());
		if (c != 0) return c;

		// compare objects

		c = this.getObject().compareTo(other.getObject());
		if (c != 0) return c;

		return 0;
	}
}
