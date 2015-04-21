package xdi2.core.syntax.parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.syntax.Parser;
import xdi2.core.syntax.ParserAbstract;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.syntax.XDIXRef;

/**
 * An XDI parser implemented manually in pure Java.
 * This parser does not use an ABNF.
 */
public class ParserImpl extends ParserAbstract implements Parser {

	private static final Logger log = LoggerFactory.getLogger(ParserImpl.class);

	@Override
	public XDIStatement parseXDIStatement(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing statement: " + string);

		String temp = stripXs(string);

		String[] parts = temp.split("/", -1);
		if (parts.length != 3) throw new ParserException("Invalid statement: " + string + " (wrong number of parts: " + parts.length + ")");
		int split0 = parts[0].length();
		int split1 = parts[1].length();

		String subjectString = string.substring(0, split0);
		String predicateString = string.substring(split0 + 1, split0 + split1 + 1);
		String objectString = string.substring(split0 + split1 + 2);

		XDIAddress subject = this.parseXDIAddress(subjectString);

		if (XDIConstants.XDI_ARC_LITERAL.toString().equals(predicateString)) {

			XDIArc predicate = XDIConstants.XDI_ARC_LITERAL;
			Object object = this.parseLiteralData(objectString);

			return this.newXDIStatement(string, subject, predicate, object);
		} else if (XDIConstants.STRING_CONTEXT.equals(predicateString)) {

			String predicate = XDIConstants.STRING_CONTEXT;
			XDIArc object = this.parseXDIArc(objectString);

			return this.newXDIStatement(string, subject, predicate, object);
		} else {

			XDIAddress predicate = this.parseXDIAddress(predicateString);
			XDIAddress object = this.parseXDIAddress(objectString);

			return this.newXDIStatement(string, subject, predicate, object);
		}
	}

	@Override
	public XDIAddress parseXDIAddress(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing address: " + string);

		int start = 0, pos = 0;
		String pair;
		Stack<String> pairs = new Stack<String> ();
		List<XDIArc> arcs = new ArrayList<XDIArc> ();

		while (pos < string.length()) {

			// parse beginning of arc

			if (pos < string.length() && (pair = xsvariable(string.charAt(pos))) != null) { pairs.push(pair); pos++; }
			if (pos < string.length() && (pair = xsdefinition(string.charAt(pos))) != null) { pairs.push(pair); pos++; }
			if (pos < string.length() && (pair = xscollection(string.charAt(pos))) != null) { pairs.push(pair); pos++; }
			if (pos < string.length() && (pair = xsattribute(string.charAt(pos))) != null) { pairs.push(pair); pos++; }
			if (pos < string.length() && cs(string.charAt(pos)) != null) pos++;
			if (pos < string.length() && (pair = xsxref(string.charAt(pos))) != null) { pairs.push(pair); pos++; }

			// parse to the end of the arc

			while (pos < string.length()) {

				// no open pairs?

				if (pairs.isEmpty()) {

					// reached beginning of the next arc

					if (xsvariable(string.charAt(pos)) != null) break;
					if (xsdefinition(string.charAt(pos)) != null) break;
					if (xscollection(string.charAt(pos)) != null) break;
					if (xsattribute(string.charAt(pos)) != null) break;
					if (cs(string.charAt(pos)) != null) break;
					if (xsxref(string.charAt(pos)) != null) break;
				}

				// at least one pair still open?

				if (! pairs.isEmpty()) {

					// pair being closed?

					if (string.charAt(pos) == pairs.peek().charAt(1)) {

						pairs.pop();
						pos++;
						continue;
					}

					// new pair being opened?

					pair = xsvariable(string.charAt(pos));
					if (pair == null) pair = xsdefinition(string.charAt(pos));
					if (pair == null) pair = xscollection(string.charAt(pos));
					if (pair == null) pair = xsattribute(string.charAt(pos));
					if (pair == null) pair = xsxref(string.charAt(pos));

					if (pair != null) { 

						pairs.push(pair); 
						pos++; 
						continue;
					}
				}

				pos++;
			}

			if (! pairs.isEmpty()) throw new ParserException("Missing closing character '" + pairs.peek().charAt(1) + "' at position " + pos + ".");

			arcs.add(this.parseXDIArc(string.substring(start, pos)));

			start = pos;
		}

		// done

		return this.newXDIAddress(string, arcs);
	}

	@Override
	public XDIArc parseXDIArc(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing arc: " + string);

		Character cs = null;
		String variable = null;
		String definition = null;
		String collection = null;
		String attribute = null;
		String literal = null;
		XDIXRef xref = null;

		int pos = 0, len = string.length();

		// extract variable pair

		if (pos < len && (variable = xsvariable(string.charAt(pos))) != null) {

			if (string.charAt(len - 1) != variable.charAt(1)) throw new ParserException("Invalid arc: " + string + " (invalid closing '" + variable.charAt(1) + "' character for variable at position " + pos + ")");

			pos++; len--;
		}

		// extract definition pair

		if (pos < len && (definition = xsdefinition(string.charAt(pos))) != null) {

			if (string.charAt(len - 1) != definition.charAt(1)) throw new ParserException("Invalid arc: " + string + " (invalid closing '" + definition.charAt(1) + "' character for definition at position " + pos + ")");

			pos++; len--;
		}

		// extract collection pair

		if (pos < len && (collection = xscollection(string.charAt(pos))) != null) {

			if (string.charAt(len - 1) != collection.charAt(1)) throw new ParserException("Invalid arc: " + string + " (invalid closing '" + collection.charAt(1) + "' character for collection at position " + pos + ")");

			pos++; len--;
		}

		// extract attribute pair

		if (pos < len && (attribute = xsattribute(string.charAt(pos))) != null) {

			if (string.charAt(len - 1) != attribute.charAt(1)) throw new ParserException("Invalid arc: " + string + " (invalid closing '" + attribute.charAt(1) + "' character for attribute at position " + pos + ")");

			pos++; len--;
		}

		// extract cs

		if (pos < len && (cs = cs(string.charAt(pos))) != null) {

			pos++;
		}

		// parse the rest, either xref or literal

		if (pos < len) {

			if (xsxref(string.charAt(pos)) != null) {

				xref = this.parseXDIXRef(string.substring(pos, len));
			} else {

				if (pos == 0) throw new ParserException("Invalid arc: " + string + " (no context symbol or cross reference)");
				literal = parseLiteral(string.substring(pos, len));
			}
		}

		// done

		return this.newXDIArc(string, cs, variable != null, definition != null, collection != null, attribute != null, literal, xref);
	}

	@Override
	public XDIXRef parseXDIXRef(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing xref: " + string);

		String xs = xsxref(string.charAt(0));
		if (xs == null) throw new ParserException("Invalid cross reference: " + string + " (no opening delimiter)");
		if (string.charAt(string.length() - 1) != xs.charAt(1)) throw new ParserException("Invalid cross reference: " + string + " (invalid closing '" + xs.charAt(1) + "' delimiter)");

		String value = string.substring(1, string.length() - 1);

		String temp = stripXs(value);

		XDIAddress XDIaddress = null;
		XDIAddress partialSubject = null;
		XDIAddress partialPredicate = null;
		String iri = null;
		String literal = null;

		if (isIri(temp)) {

			iri = value;
		} else {

			int addresses = StringUtils.countMatches(temp, "/") + 1;

			if (addresses == 2) {

				temp = " " + temp + " ";
				String[] parts = temp.split("/");
				int split0 = parts[0].length() - 1;

				partialSubject = this.parseXDIAddress(value.substring(0, split0));
				partialPredicate = this.parseXDIAddress(value.substring(split0 + 1));
			} else if (value.isEmpty() || cs(value.charAt(0)) != null || xsvariable(value.charAt(0)) != null || xsdefinition(value.charAt(0)) != null || xscollection(value.charAt(0)) != null || xsattribute(value.charAt(0)) != null || xsxref(value.charAt(0)) != null) {

				XDIaddress = this.parseXDIAddress(value);
			} else {

				literal = value;
			}
		}

		// done

		return this.newXDIXRef(string, xs, XDIaddress, partialSubject, partialPredicate, iri, literal);
	}

	public Object parseLiteralData(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing literal data: " + string);

		try {

			return AbstractLiteralNode.stringToLiteralData(string);
		} catch (Exception ex) {

			throw new ParserException("Invalid literal data: " + string);
		}
	}	

	private static String stripXs(String string) {

		string = stripPattern(string, Pattern.compile(".*(\\|[^\\|]*\\|).*"));
		string = stripPattern(string, Pattern.compile(".*(\\([^\\(\\)]*\\)).*"));
		string = stripPattern(string, Pattern.compile(".*(\"[^\"]*\").*"));

		return string;
	}

	private static String stripPattern(String string, Pattern pattern) {

		String temp = string;

		while (true) {

			Matcher matcher = pattern.matcher(temp);
			if (! matcher.matches()) break;

			StringBuffer newtemp = new StringBuffer();
			newtemp.append(temp.substring(0, matcher.start(1)));
			for (int i=matcher.start(1); i<matcher.end(1); i++) newtemp.append(" ");
			newtemp.append(temp.substring(matcher.end(1)));

			temp = newtemp.toString();
		}

		return temp;
	}

	private static boolean isIri(String string) {

		int indexColon = string.indexOf(':');
		int indexAuthorityPersonal = string.indexOf(XDIConstants.CS_AUTHORITY_PERSONAL.charValue());
		int indexAuthorityLegal = string.indexOf(XDIConstants.CS_AUTHORITY_LEGAL.charValue());
		int indexAuthorityGeneral = string.indexOf(XDIConstants.CS_AUTHORITY_GENERAL.charValue());
		int indexClassUnreserved = string.indexOf(XDIConstants.CS_CLASS_UNRESERVED.charValue());
		int indexClassReserved = string.indexOf(XDIConstants.CS_CLASS_RESERVED.charValue());
		int indexValue = string.indexOf(XDIConstants.CS_LITERAL.charValue());
		int indexMemberUnordered = string.indexOf(XDIConstants.CS_MEMBER_UNORDERED.charValue());
		int indexMemberOrdered = string.indexOf(XDIConstants.CS_MEMBER_ORDERED.charValue());

		if (indexColon == -1) return false;

		if (indexAuthorityPersonal != -1 && indexAuthorityPersonal < indexColon) return false;
		if (indexAuthorityLegal != -1 && indexAuthorityLegal < indexColon) return false;
		if (indexAuthorityGeneral != -1 && indexAuthorityGeneral < indexColon) return false;
		if (indexClassUnreserved != -1 && indexClassUnreserved < indexColon) return false;
		if (indexClassReserved != -1 && indexClassReserved < indexColon) return false;
		if (indexValue != -1 && indexValue < indexColon) return false;
		if (indexMemberUnordered != -1 && indexMemberUnordered < indexColon) return false;
		if (indexMemberOrdered != -1 && indexMemberOrdered < indexColon) return false;

		return true;
	}

	/*
	 * Helper methods
	 */

	private static Character cs(char c) {

		for (Character cs : XDIConstants.CS_ARRAY) if (cs.charValue() == c) return cs;

		return null;
	}

	private static String xsvariable(char c) {

		if (XDIConstants.XS_VARIABLE.charAt(0) == c) return XDIConstants.XS_VARIABLE;

		return null;
	}

	private static String xsdefinition(char c) {

		if (XDIConstants.XS_DEFINITION.charAt(0) == c) return XDIConstants.XS_DEFINITION;

		return null;
	}

	private static String xscollection(char c) {

		if (XDIConstants.XS_COLLECTION.charAt(0) == c) return XDIConstants.XS_COLLECTION;

		return null;
	}

	private static String xsattribute(char c) {

		if (XDIConstants.XS_ATTRIBUTE.charAt(0) == c) return XDIConstants.XS_ATTRIBUTE;

		return null;
	}

	private static String xsxref(char c) {

		if (XDIConstants.XS_ROOT.charAt(0) == c) return XDIConstants.XS_ROOT;

		return null;
	}

	private static String parseLiteral(String string) {

		try {

			string = URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException ex) {

			throw new ParserException(ex.getMessage(), ex);
		}

		for (int pos=0; pos<string.length(); pos++) {

			char c = string.charAt(pos);

			if (c >= 0x41 && c <= 0x5A) continue;
			if (c >= 0x61 && c <= 0x7A) continue;
			if (c >= 0x30 && c <= 0x39) continue;
			if (c == '-') continue;
			if (c == '.') continue;
			if (c == ':') continue;
			if (c == '_') continue;
			if (c == '~') continue;
			if (c >= 0xA0 && c <= 0xD7FF) continue;
			if (c >= 0xF900 && c <= 0xFDCF) continue;
			if (c >= 0xFDF0 && c <= 0xFFEF) continue;

			throw new ParserException("Invalid character '" + c + "' at position " + pos + " of literal " + string);
		}

		return string;
	}
}
