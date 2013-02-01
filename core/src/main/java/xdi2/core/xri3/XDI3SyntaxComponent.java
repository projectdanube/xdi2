package xdi2.core.xri3;

import java.io.Serializable;

public abstract class XDI3SyntaxComponent implements Serializable, Cloneable, Comparable<XDI3SyntaxComponent> {

	private static final long serialVersionUID = 7667278290948679857L;

	protected String string;

	public XDI3SyntaxComponent(String string) {

		this.string = string;
	}

	public String toIRINormalForm() {

		return IRIUtils.XRItoIRI(this.string, false);
	}

	public String toURINormalForm() {

		return IRIUtils.IRItoURI(this.toIRINormalForm());
	}

	@Override
	public int compareTo(XDI3SyntaxComponent other) {

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
		if (! (object instanceof XDI3SyntaxComponent)) return false;

		XDI3SyntaxComponent other = (XDI3SyntaxComponent) object;

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
