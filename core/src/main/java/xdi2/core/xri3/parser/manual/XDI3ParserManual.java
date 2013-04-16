package xdi2.core.xri3.parser.manual;

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
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

/**
 * An XRI parser implemented manually in pure Java.
 * This parse has not been automatically generated from an ABNF. 
 */
public class XDI3ParserManual extends XDI3Parser {

	private static final Logger log = LoggerFactory.getLogger(XDI3ParserManual.class);

	@Override
	public XDI3Statement parseXDI3Statement(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing statement: " + string);

		String temp = stripXs(string);

		String[] parts = temp.split("/");
		if (parts.length != 3) throw new ParserException("Invalid statement: " + string + " (wrong number of segments: " + parts.length + ")");
		int split0 = parts[0].length();
		int split1 = parts[1].length();

		String subjectString = string.substring(0, split0);
		String predicateString = string.substring(split0 + 1, split0 + split1 + 1);
		String objectString = string.substring(split0 + split1 + 2);

		XDI3Segment subject = this.parseXDI3Segment(subjectString);
		XDI3Segment predicate = this.parseXDI3Segment(predicateString);
		Object object;

		if (XDIConstants.XRI_S_LITERAL.equals(predicateString)) {

			object = this.parseString(objectString);
		} else {

			object = this.parseXDI3Segment(objectString);
		}

		return this.makeXDI3Statement(string, subject, predicate, object);
	}

	@Override
	public XDI3Segment parseXDI3Segment(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing segment: " + string);

		int start = 0, pos = 0;
		String pair;
		Stack<String> pairs = new Stack<String> ();
		List<XDI3SubSegment> subSegments = new ArrayList<XDI3SubSegment> ();

		while (pos < string.length()) {

			// parse beginning of subsegment

			if (pos < string.length() && (pair = sin(string.charAt(pos))) != null) { pairs.push(pair); pos++; }
			if (pos < string.length() && (pair = att(string.charAt(pos))) != null) { pairs.push(pair); pos++; }
			if (pos < string.length() && cs(string.charAt(pos)) != null) pos++;
			if (pos < string.length() && (pair = xs(string.charAt(pos))) != null) { pairs.push(pair); pos++; }

			// parse to the end of the subsegment

			while (pos < string.length()) {

				// no open pairs?

				if (pairs.isEmpty()) {

					// reached beginning of the next subsegment

					if (sin(string.charAt(pos)) != null) break;
					if (att(string.charAt(pos)) != null) break;
					if (cs(string.charAt(pos)) != null) break;
					if (xs(string.charAt(pos)) != null) break;
				}

				// at least one pair still open?

				if (! pairs.isEmpty()) {

					// new pair being opened?

					pair = sin(string.charAt(pos));
					if (pair == null) pair = att(string.charAt(pos));
					if (pair == null) pair = xs(string.charAt(pos));

					if (pair != null) { 

						pairs.push(pair); 
						pos++; 
						continue;
					}

					// pair being closed?

					if (string.charAt(pos) == pairs.peek().charAt(1)) {

						pairs.pop();
						pos++;
						continue;
					}
				}

				pos++;
			}

			if (! pairs.isEmpty()) throw new ParserException("Missing closing character '" + pairs.peek().charAt(1) + "'.");

			subSegments.add(this.parseXDI3SubSegment(string.substring(start, pos)));

			start = pos;
		}

		// done
		
		return this.makeXDI3Segment(string, subSegments);
	}

	@Override
	public XDI3SubSegment parseXDI3SubSegment(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing subsegment: " + string);

		Character cs = null;
		String sin = null;
		String att = null;
		String literal = null;
		XDI3XRef xref = null;

		int pos = 0, len = string.length();

		// extract singleton pair
		
		if (pos < len && (sin = sin(string.charAt(pos))) != null) {

			if (string.charAt(len - 1) != sin.charAt(1)) throw new ParserException("Invalid subsegment: " + string + " (invalid closing '" + sin.charAt(1) + "' character for singleton)");

			pos++; len--;
		}

		// extract attribute pair
		
		if (pos < len && (att = att(string.charAt(pos))) != null) {

			if (string.charAt(len - 1) != att.charAt(1)) throw new ParserException("Invalid subsegment: " + string + " (invalid closing '" + att.charAt(1) + "' character for attribute)");

			pos++; len--;
		}

		// extract cs
		
		if (pos < len && (cs = cs(string.charAt(pos))) != null) {

			pos++;
		}

		// parse the rest, either xref or literal
		
		if (pos < len) {

			if (xs(string.charAt(pos)) != null) {

				xref = this.parseXDI3XRef(string.substring(pos, len));
			} else {

				if (pos == 0) throw new ParserException("Invalid subsegment: " + string + " (no cs, xref)");
				literal = parseLiteral(string.substring(pos, len));
			}
		}

		// done
		
		return this.makeXDI3SubSegment(string, cs, sin != null, att != null, literal, xref);
	}

	@Override
	public XDI3XRef parseXDI3XRef(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing xref: " + string);

		String xs = xs(string.charAt(0));
		if (xs == null) throw new ParserException("Invalid xref: " + string + " (no opening delimiter)");
		if (string.charAt(string.length() - 1) != xs.charAt(1)) throw new ParserException("Invalid xref: " + string + " (invalid closing '" + xs.charAt(1) + "' character)");
		if (string.length() == 2) return this.makeXDI3XRef(string, xs, null, null, null, null, null, null);

		String value = string.substring(1, string.length() - 1);

		String temp = stripXs(value);

		XDI3Segment segment = null;
		XDI3Statement statement = null;
		XDI3Segment partialSubject = null;
		XDI3Segment partialPredicate = null;
		String iri = null;
		String literal = null;

		if (isIri(temp)) {

			iri = value;
		} else {

			int segments = StringUtils.countMatches(temp, "/") + 1;

			if (segments == 3) {

				statement = this.parseXDI3Statement(value);
			} else if (segments == 2) {

				String[] parts = temp.split("/");
				int split0 = parts[0].length();

				partialSubject = this.parseXDI3Segment(value.substring(0, split0));
				partialPredicate = this.parseXDI3Segment(value.substring(split0 + 1));
			} else if (cs(value.charAt(0)) != null || sin(value.charAt(0)) != null || att(value.charAt(0)) != null || xs(value.charAt(0)) != null) {

				segment = this.parseXDI3Segment(value);
			} else {

				literal = value;
			}
		}

		return this.makeXDI3XRef(string, xs, segment, statement, partialSubject, partialPredicate, iri, literal);
	}

	public String parseString(String string) {

		if (log.isTraceEnabled()) log.trace("Parsing string: " + string);

		if (string.length() < 2) throw new ParserException("Invalid string: " + string);
		if (string.charAt(0) != '"') throw new ParserException("Invalid string: " + string + " (no opening double quotes)");
		if (string.charAt(string.length() - 1) != '"') throw new ParserException("Invalid string: " + string + " (no closing double quotes)");

		return string.substring(1, string.length() - 1);
	}

	private static String stripXs(String string) {

		string = stripXs(string, Pattern.compile(".*(\\([^\\(\\)]*\\)).*"));
		string = stripXs(string, Pattern.compile(".*(\\{[^\\{\\}]*\\}).*"));
		string = stripXs(string, Pattern.compile(".*(\\\"[^\\\"\\]]*\\\").*"));

		return string;
	}

	private static String stripXs(String string, Pattern pattern) {

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
		int indexEquals = string.indexOf(XDI3Constants.CS_EQUALS.charValue());
		int indexAt = string.indexOf(XDI3Constants.CS_AT.charValue());
		int indexPlus = string.indexOf(XDI3Constants.CS_PLUS.charValue());
		int indexDollar = string.indexOf(XDI3Constants.CS_DOLLAR.charValue());
		int indexStar = string.indexOf(XDI3Constants.CS_STAR.charValue());
		int indexBang = string.indexOf(XDI3Constants.CS_BANG.charValue());

		if (indexColon == -1) return false;

		if (indexEquals != -1 && indexEquals < indexColon) return false;
		if (indexAt != -1 && indexAt < indexColon) return false;
		if (indexPlus != -1 && indexPlus < indexColon) return false;
		if (indexDollar != -1 && indexDollar < indexColon) return false;
		if (indexStar != -1 && indexStar < indexColon) return false;
		if (indexBang != -1 && indexBang < indexColon) return false;

		return true;
	}

	/*
	 * Helper methods
	 */

	private static Character cs(char c) {

		for (Character cs : XDI3Constants.CS_ARRAY) if (cs.charValue() == c) return cs;

		return null;
	}

	private static String sin(char c) {

		if (XDI3Constants.XS_SINGLETON.charAt(0) == c) return XDI3Constants.XS_SINGLETON;

		return null;
	}

	private static String att(char c) {

		if (XDI3Constants.XS_ATTRIBUTE.charAt(0) == c) return XDI3Constants.XS_ATTRIBUTE;

		return null;
	}

	private static String xs(char c) {

		if (XDI3Constants.XS_ROOT.charAt(0) == c) return XDI3Constants.XS_ROOT;
		if (XDI3Constants.XS_VARIABLE.charAt(0) == c) return XDI3Constants.XS_VARIABLE;

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
