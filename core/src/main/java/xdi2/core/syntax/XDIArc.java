package xdi2.core.syntax;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.syntax.parser.ParserException;
import xdi2.core.syntax.parser.ParserRegistry;

public class XDIArc extends XDIIdentifier {

	private static final long serialVersionUID = -645927779266394209L;

	public static final String DEFAULT_DIGEST_ALGORITHM = "SHA-512";

	private Character cs;
	private boolean variable;
	private boolean definition;
	private boolean collection;
	private boolean attribute;
	private boolean immutable;
	private boolean relative;
	private String literal;
	private XDIXRef xref;

	private XDIArc(String string, Character cs, boolean variable, boolean definition, boolean collection, boolean attribute, boolean immutable, boolean relative, String literal, XDIXRef xref) {

		super(string);

		this.cs = cs;
		this.variable = variable;
		this.definition = definition;
		this.collection = collection;
		this.attribute = attribute;
		this.immutable = immutable;
		this.relative = relative;
		this.literal = literal;
		this.xref = xref;
	}

	public static XDIArc create(String string) {

		return ParserRegistry.getInstance().getParser().parseXDIArc(string);
	}

	static XDIArc fromComponents(String string, Character cs, boolean variable, boolean definition, boolean collection, boolean attribute, boolean immutable, boolean relative, String literal, XDIXRef xref) {

		if (string == null) {

			StringBuffer buffer = new StringBuffer();
			if (variable) buffer.append(XDIConstants.XS_VARIABLE.charAt(0));
			if (definition) buffer.append(XDIConstants.XS_DEFINITION.charAt(0));
			if (collection) buffer.append(XDIConstants.XS_COLLECTION.charAt(0));
			if (attribute) buffer.append(XDIConstants.XS_ATTRIBUTE.charAt(0));
			if (cs != null) buffer.append(cs);
			if (immutable) buffer.append(XDIConstants.S_IMMUTABLE);
			if (relative) buffer.append(XDIConstants.S_RELATIVE);
			if (literal != null) buffer.append(literal);
			if (xref != null) buffer.append(xref.toString());
			if (attribute) buffer.append(XDIConstants.XS_ATTRIBUTE.charAt(1));
			if (collection) buffer.append(XDIConstants.XS_COLLECTION.charAt(1));
			if (definition) buffer.append(XDIConstants.XS_DEFINITION.charAt(1));
			if (variable) buffer.append(XDIConstants.XS_VARIABLE.charAt(1));

			string = buffer.toString();
		}

		if (cs == null && literal != null) throw new ParserException("If there is a literal, must have a context symbol: " + string);
		if (literal != null && xref != null) throw new ParserException("Cannot have both literal and xref: " + string);

		return new XDIArc(string, cs, variable, definition, collection, attribute, immutable, relative, literal, xref);
	}

	/*
	 * Factory methods
	 */

	public static XDIArc fromComponents(Character cs, boolean variable, boolean definition, boolean collection, boolean attribute, boolean immutable, boolean relative, String literal, XDIXRef xref) {

		return fromComponents(null, cs, variable, definition, collection, attribute, immutable, relative, literal, xref);
	}

	public static XDIArc fromComponent(XDIAddress XDIaddress) {

		if (XDIaddress.getNumXDIArcs() > 1) return null;

		return XDIaddress.getFirstXDIArc();
	}

	public static String literalFromUuid(String uuid) {

		String literal = ":uuid:" + uuid.toLowerCase();

		return literal;
	}

	public static String literalFromUuid(UUID uuid) {

		return literalFromUuid(uuid.toString());
	}

	public static String literalFromRandomUuid() {

		UUID uuid = UUID.randomUUID();

		return literalFromUuid(uuid);
	}

	public static String literalFromDigest(String input, String algorithm) {

		byte[] output;

		try {

			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(input.getBytes(Charset.forName("UTF-8")));
			output = digest.digest();
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		String hex = new String(Hex.encodeHex(output));
		String literal = ":" + algorithm.toLowerCase().replace("-", "") + ":" + hex;

		return literal;
	}

	public static String literalFromDigest(String input) {

		return literalFromDigest(input, DEFAULT_DIGEST_ALGORITHM);
	}

	/*
	 * Getters
	 */

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

	public boolean isImmutable() {

		return this.immutable;
	}

	public boolean isRelative() {

		return this.relative;
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
