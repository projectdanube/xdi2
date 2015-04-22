package xdi2.core.syntax;

import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.parser.ParserRegistry;

public class XDIArc extends XDIIdentifier {

	private static final long serialVersionUID = -645927779266394209L;

	private Character cs;
	private boolean variable;
	private boolean definition;
	private boolean collection;
	private boolean attribute;
	private String literal;
	private XDIXRef xref;

	private XDIArc(String string, Character cs, boolean variable, boolean definition, boolean collection, boolean attribute, String literal, XDIXRef xref) {

		super(string);

		this.cs = cs;
		this.variable = variable;
		this.definition = definition;
		this.collection = collection;
		this.attribute = attribute;
		this.literal = literal;
		this.xref = xref;
	}

	public static XDIArc create(String string) {

		return ParserRegistry.getInstance().getParser().parseXDIArc(string);
	}

	static XDIArc fromComponents(String string, Character cs, boolean variable, boolean definition, boolean collection, boolean attribute, String literal, XDIXRef xref) {

		if (string == null) {

			StringBuffer buffer = new StringBuffer();
			if (variable) buffer.append(XDIConstants.XS_VARIABLE.charAt(0));
			if (definition) buffer.append(XDIConstants.XS_DEFINITION.charAt(0));
			if (collection) buffer.append(XDIConstants.XS_COLLECTION.charAt(0));
			if (attribute) buffer.append(XDIConstants.XS_ATTRIBUTE.charAt(0));
			if (cs != null) buffer.append(cs);
			if (literal != null) buffer.append(literal);
			if (xref != null) buffer.append(xref.toString());
			if (attribute) buffer.append(XDIConstants.XS_ATTRIBUTE.charAt(1));
			if (collection) buffer.append(XDIConstants.XS_COLLECTION.charAt(1));
			if (definition) buffer.append(XDIConstants.XS_DEFINITION.charAt(1));
			if (variable) buffer.append(XDIConstants.XS_VARIABLE.charAt(0));

			string = buffer.toString();
		}

		return new XDIArc(string, cs, variable, definition, collection, attribute, literal, xref);
	}

	public static XDIArc fromComponents(Character cs, boolean variable, boolean definition, boolean collection, boolean attribute, String literal, XDIXRef xref) {

		return fromComponents(null, cs, variable, definition, collection, attribute, literal, xref);
	}

	public boolean hasCs() {

		return this.cs != null;
	}

	public boolean isVariable() {

		return this.variable;
	}

	public boolean isDefinition() {

		return this.definition;
	}

	public boolean isCollection() {

		return this.collection;
	}

	public boolean isAttribute() {

		return this.attribute;
	}

	public boolean hasLiteral() {

		return this.literal != null;
	}

	public boolean hasXRef() {

		return this.xref != null;
	}

	public Character getCs() {

		return this.cs;
	}

	public String getLiteral() {

		return this.literal;
	}

	public XDIXRef getXRef() {

		return this.xref;
	}

	public boolean isLiteralNodeXDIArc() {

		return this.equals(XDIConstants.XDI_ARC_LITERAL);
	}
}
