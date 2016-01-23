package xdi2.core.syntax;

import java.io.Serializable;

public abstract class XDIIdentifier implements Serializable, Cloneable, Comparable<XDIIdentifier> {

	private static final long serialVersionUID = 7667278290948679857L;

	protected String string;

	XDIIdentifier(String string) {

		this.string = string;
	}

	@Override
	public int compareTo(XDIIdentifier other) {

		if (other == this) return 0;
		if (other == null) throw new NullPointerException();

		if (this.string == null || other.string == null) throw new NullPointerException();

		return this.string.compareTo(other.string);
	}

	@Override
	public boolean equals(Object object) {

		if (object == this) return true;
		if (object == null) return false;
		if (object instanceof String) return(this.toString().equals(object));
		if (! (object instanceof XDIIdentifier)) return false;

		XDIIdentifier other = (XDIIdentifier) object;

		if (this.string == null || other.string == null) return false;

		return this.string.equals(other.string);
	}

	@Override
	public int hashCode() {

		if (this.string == null) return 0;

		return this.string.hashCode();
	}

	@Override
	public String toString() {

		return this.string;
	}
}
