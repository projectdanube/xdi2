package xdi2.core.xri3.parser.manual;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;
import xdi2.core.xri3.XDI3Constants;

/**
 * An XRI parser implemented manually in pure Java.
 * This parse has not been automatically generated from an ABNF. 
 */
public class XDI3ParserManual extends XDI3Parser {

	private static final Logger log = LoggerFactory.getLogger(XDI3ParserManual.class);

	@Override
	public XDI3Statement parseXDI3Statement(String string) {

		log.trace("Parsing statement: " + string);

		String temp = stripCf(string);

		String[] parts = temp.split("/");
		if (parts.length != 3) throw new ParserException("Invalid statement: " + string + " (wrong number of segments: " + parts.length + ")");
		int split0 = parts[0].length();
		int split1 = parts[1].length();

		XDI3Segment subject = this.parseXDI3Segment(string.substring(0, split0));
		XDI3Segment predicate = this.parseXDI3Segment(string.substring(split0 + 1, split0 + split1 + 1));
		XDI3Segment object = this.parseXDI3Segment(string.substring(split0 + split1 + 2));

		return this.makeXDI3Statement(string, subject, predicate, object);
	}

	@Override
	public XDI3Segment parseXDI3Segment(String string) {

		log.trace("Parsing segment: " + string);

		int start = 0, pos = 0;
		String cf = null;
		int cfcount = 0;
		List<XDI3SubSegment> subSegments = new ArrayList<XDI3SubSegment> ();

		while (pos < string.length()) {

			if (cs(string.charAt(pos)) != null) pos++;

			if (pos < string.length() && cf(string.charAt(pos)) != null) { cf = cf(string.charAt(pos)); cfcount = 1; pos++; }

			while (pos < string.length()) {

				if (cs(string.charAt(pos)) != null && cfcount == 0) break;
				if (cf(string.charAt(pos)) != null && cfcount == 0) break;

				if (cf != null && string.charAt(pos) == cf.charAt(0)) {

					cfcount++;
				}

				if (cf != null && string.charAt(pos) == cf.charAt(1)) {

					cfcount--;
					if (cfcount == -1) throw new ParserException("Invalid segment: " + string + " (wrong closing parentheses at position " + pos + ")");
					if (cfcount == 0) { pos++; break; }
				}

				pos++;
			}

			subSegments.add(this.parseXDI3SubSegment(string.substring(start, pos)));

			start = pos;
		}

		return this.makeXDI3Segment(string, subSegments);
	}

	@Override
	public XDI3SubSegment parseXDI3SubSegment(String string) {

		log.trace("Parsing subsegment: " + string);

		Character cs = null;
		String literal = null;
		XDI3XRef xref = null;

		int pos = 0;

		cs = cs(string.charAt(pos));
		if (cs != null) pos++;

		if (pos < string.length()) {

			if (cf(string.charAt(pos)) != null) {

				xref = this.parseXDI3XRef(string.substring(pos));
			} else {

				if (pos == 0) throw new ParserException("Invalid subsegment: " + string + " (no cs, xref)");
				literal = parseLiteral(string.substring(pos));
			}
		}

		return this.makeXDI3SubSegment(string, cs, literal, xref);
	}

	@Override
	public XDI3XRef parseXDI3XRef(String string) {

		log.trace("Parsing xref: " + string);

		String cf = cf(string.charAt(0));
		if (cf == null) throw new ParserException("Invalid xref: " + string + " (no opening delimiter)");
		if (string.charAt(string.length() - 1) != cf.charAt(1)) throw new ParserException("Invalid xref: " + string + " (no closing delimiter)");
		if (string.length() == 2) return this.makeXDI3XRef(string, cf, null, null, null, null, null, null);

		String value = string.substring(1, string.length() - 1);

		String temp = stripCf(value);

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
			} else if (cs(value.charAt(0)) != null || cf(value.charAt(0)) != null) {

				segment = this.parseXDI3Segment(value);
			} else {

				literal = value;
			}
		}

		return this.makeXDI3XRef(string, cf, segment, statement, partialSubject, partialPredicate, iri, literal);
	}

	private static String stripCf(String string) {

		string = stripCf(string, Pattern.compile(".*(\\([^\\(\\)]*\\)).*"));
		string = stripCf(string, Pattern.compile(".*(\\<[^\\<\\>]*\\>).*"));
		string = stripCf(string, Pattern.compile(".*(\\{[^\\{\\}]*\\}).*"));
		string = stripCf(string, Pattern.compile(".*(\\[[^\\[\\]]*\\]).*"));

		return string;
	}

	private static String stripCf(String string, Pattern pattern) {

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

	private static String cf(char c) {

		for (String cf : XDI3Constants.CF_ARRAY) if (cf.charAt(0) == c) return cf;

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
