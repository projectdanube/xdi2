package xdi2.core.xri3.impl;

import xdi2.core.xri3.XRISyntaxComponent;
import xdi2.core.xri3.impl.parser.Rule;

public abstract class XRI3SyntaxComponent implements XRISyntaxComponent {

	public abstract Rule getParserObject();

	public String toIRINormalForm() {

		String this_spelling = this.getParserObject().spelling;
		if (this_spelling == null) throw new NullPointerException();

		return(IRIUtils.XRItoIRI(this_spelling, false));
	}

	public String toURINormalForm() {

		return(IRIUtils.IRItoURI(this.toIRINormalForm()));
	}

	public int compareTo(Object obj) {

		if (obj == this) return(0);
		if (obj == null) throw new NullPointerException();
		if (obj instanceof String) return(this.toString().compareTo((String) obj));
		if (! (obj instanceof XRI3SyntaxComponent)) return(0);

		String this_spelling = this.getParserObject().spelling;
		String obj_spelling = ((XRI3SyntaxComponent) obj).getParserObject().spelling;
		if (this_spelling == null || obj_spelling == null) throw new NullPointerException();

		return(this_spelling.compareTo(obj_spelling));
	}

	public boolean equals(Object obj) {

		if (obj == this) return(true);
		if (obj == null) return(false);
		if (obj instanceof String) return(this.toString().equals(obj));
		if (! (obj instanceof XRI3SyntaxComponent)) return(false);

		String this_spelling = this.getParserObject().spelling;
		String obj_spelling = ((XRI3SyntaxComponent) obj).getParserObject().spelling;
		if (this_spelling == null || obj_spelling == null) return(false);

		return(this_spelling.equals(obj_spelling));
	}

	public int hashCode() {

		String this_spelling = this.getParserObject().spelling;
		if (this_spelling == null) return(0);

		return(this_spelling.hashCode());
	}

	public String toString() {

		return(this.getParserObject().spelling);
	}
}
