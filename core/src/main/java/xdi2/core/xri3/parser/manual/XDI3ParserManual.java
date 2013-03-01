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
import xdi2.core.xri3.XRI3Constants;

/**
 * An XRI parser implemented manually in pure Java.
 * This parse has not been automatically generated from an ABNF. 
 */
public class XDI3ParserManual extends XDI3Parser {

	private static final Logger log = LoggerFactory.getLogger(XDI3ParserManual.class);

	@Override
	public XDI3Statement parseXDI3Statement(String string) {

		log.trace("Parsing statement: " + string);

		String temp = stripParens(string);

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

		int start = 0, pos = 0, parens = 0;
		List<XDI3SubSegment> subSegments = new ArrayList<XDI3SubSegment> ();

		while (pos < string.length()) {

			if (pos + 1 < string.length() && isGcs(string.charAt(pos)) && isLcs(string.charAt(pos + 1))) pos += 2;
			else if (isGcs(string.charAt(pos))) pos++;
			else if (isLcs(string.charAt(pos))) pos++;

			if (pos < string.length() && string.charAt(pos) == '(') { parens = 1; pos++; }

			while (pos < string.length()) {

				if (isGcs(string.charAt(pos)) && parens == 0) break;
				if (isLcs(string.charAt(pos)) && parens == 0) break;
				if (string.charAt(pos) == '(' && parens == 0) break;
				if (string.charAt(pos) == '(') parens++;
				if (string.charAt(pos) == ')' && parens == 0) throw new ParserException("Invalid segment: " + string + " (wrong closing parentheses at position " + pos + ")"); 
				if (string.charAt(pos) == ')') parens--;
				if (string.charAt(pos) == ')' && parens == 0) { pos++; break; }

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

		Character gcs = null;
		Character lcs = null;
		String literal = null;
		XDI3XRef xref = null;

		int pos = 0;

		if (string.length() >= 2 && isGcs(string.charAt(0)) && isLcs(string.charAt(1))) {

			gcs = Character.valueOf(string.charAt(pos++));
			lcs = Character.valueOf(string.charAt(pos++));
		} else if (isGcs(string.charAt(0))) {

			gcs = Character.valueOf(string.charAt(pos++));
		} else if (isLcs(string.charAt(0))) {

			lcs = Character.valueOf(string.charAt(pos++));
		}

		if (pos < string.length()) {

			if (string.charAt(pos) == '(') {

				xref = this.parseXDI3XRef(string.substring(pos));
			} else {

				if (pos == 0) throw new ParserException("Invalid subsegment: " + string + " (no gcs, lcs, xref)");
				literal = parseLiteral(string.substring(pos));
			}
		}

		return this.makeXDI3SubSegment(string, gcs, lcs, literal, xref);
	}

	@Override
	public XDI3XRef parseXDI3XRef(String string) {

		log.trace("Parsing xref: " + string);

		if (string.charAt(0) != '(') throw new ParserException("Invalid xref: " + string + " (no opening parentheses)");
		if (string.charAt(string.length() - 1) != ')') throw new ParserException("Invalid xref: " + string + " (no closing parentheses)");
		if (string.length() == 2) return this.makeXDI3XRef(string, null, null, null, null, null, null);

		string = string.substring(1, string.length() - 1);

		String temp = stripParens(string);

		XDI3Segment segment = null;
		XDI3Statement statement = null;
		XDI3Segment partialSubject = null;
		XDI3Segment partialPredicate = null;
		String IRI = null;
		String literal = null;

		if (temp.indexOf(':') != -1) {

			IRI = string;
		} else {

			int segments = StringUtils.countMatches(temp, "/") + 1;

			if (segments == 3) {

				statement = this.parseXDI3Statement(string);
			} else if (segments == 2) {

				String[] parts = temp.split("/");
				int split0 = parts[0].length();

				partialSubject = this.parseXDI3Segment(string.substring(0, split0));
				partialPredicate = this.parseXDI3Segment(string.substring(split0 + 1));
			} else if (isGcs(string.charAt(0)) || isLcs(string.charAt(0)) || string.charAt(0) == '(') {

				segment = this.parseXDI3Segment(string);
			} else {

				literal = string;
			}
		}

		return this.makeXDI3XRef(string, segment, statement, partialSubject, partialPredicate, IRI, literal);
	}

	private static String stripParens(String string) {

		Pattern pattern = Pattern.compile(".*(\\([^()]*\\)).*");

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

	/*
	 * Helper methods
	 */

	private static boolean isGcs(char c) {

		for (Character gcs : XRI3Constants.GCS_ARRAY) if (gcs.charValue() == c) return true;

		return false;
	}

	private static boolean isLcs(char c) {

		for (Character lcs : XRI3Constants.LCS_ARRAY) if (lcs.charValue() == c) return true;

		return false;
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
