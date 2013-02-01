package xdi2.core.xri3;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.UTF16;


/**
 * Utility class that provides XRI-IRI-URI transformations
 * @author wtan
 *
 */
public class IRIUtils
{
	private static final int[] HEXCHARS = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0F
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1F
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20-2F
		 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1, // 30-3F
		-1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1  // 40-4F
	};
	
	private static final String RFC3986_GEN_DELIMS = ":/?#[]@";
	private static final String RFC3986_SUB_DELIMS = "!$&'()*+,;=";
	private static final String RFC3986_RESERVED = RFC3986_GEN_DELIMS + RFC3986_SUB_DELIMS;
	private static final String ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";
	private static final String ALPHA = ALPHA_LOWER + ALPHA_LOWER.toUpperCase();
	
	// utf8 byte types
	private static final int end = 0;
	private static final int ill = 1;
	private static final int asc = 2;
	private static final int trl = 3;
	private static final int by2 = 4;
	private static final int e0  = 5;
	private static final int by3 = 6;
	private static final int ed  = 7;
	private static final int p13 = 8;
	private static final int by4 = 9;
	private static final int p16 = 10;

	
	private static final int notal = 0;
	private static final int unres = 1;
	private static final int gendl = 2;
	private static final int subdl = 3;
	private static final int slash = 4;

	// private static final String URICHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._~/%:[]@?#!$&'()*,;=+";
	private static final int[] URICHARS = {
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 00-0F
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 10-1F
		0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // 20-2F
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, // 30-3F
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // 40-4F
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, // 50-5F
		0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // 60-6F
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0  // 70-7F
	};

	private static final int[] UTF8lead = {
		/* 0x00 */ end, ill, ill, ill, ill, ill, ill, ill,
		/* 0x08 */ ill, asc, asc, ill, ill, asc, ill, ill,
		/* 0x10 */ ill, ill, ill, ill, ill, ill, ill, ill,
		/* 0x18 */ ill, ill, ill, ill, ill, ill, ill, ill,
		/* 0x20 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x28 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x30 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x38 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x40 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x48 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x50 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x58 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x60 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x68 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x70 */ asc, asc, asc, asc, asc, asc, asc, asc,
		/* 0x78 */ asc, asc, asc, asc, asc, asc, asc, ill,
		/* 0x80 */ trl, trl, trl, trl, trl, trl, trl, trl,
		/* 0x88 */ trl, trl, trl, trl, trl, trl, trl, trl,
		/* 0x90 */ trl, trl, trl, trl, trl, trl, trl, trl,
		/* 0x98 */ trl, trl, trl, trl, trl, trl, trl, trl,
		/* 0xA0 */ trl, trl, trl, trl, trl, trl, trl, trl,
		/* 0xA8 */ trl, trl, trl, trl, trl, trl, trl, trl,
		/* 0xB0 */ trl, trl, trl, trl, trl, trl, trl, trl,
		/* 0xB8 */ trl, trl, trl, trl, trl, trl, trl, trl,
		/* 0xC0 */ ill, ill, by2, by2, by2, by2, by2, by2,
		/* 0xC8 */ by2, by2, by2, by2, by2, by2, by2, by2,
		/* 0xD0 */ by2, by2, by2, by2, by2, by2, by2, by2,
		/* 0xD8 */ by2, by2, by2, by2, by2, by2, by2, by2,
		/* 0xE0 */  e0, by3, by3, by3, by3, by3, by3, by3,
		/* 0xE8 */ by3, by3, by3, by3, by3,  ed, by3, by3,
		/* 0xF0 */ p13, by4, by4, by4, p16, ill, ill, ill,
		/* 0xF8 */ ill, ill, ill, ill, ill, ill, ill, ill
	};
	
	public static String IRItoXRI(String iri)
	{
		StringBuffer sb = new StringBuffer();
		
		int cp;
		for (int i = 0; i < iri.length(); i += UTF16.getCharCount(cp)) {
			cp = UTF16.charAt(iri, i);
			
			if (cp == '%') {
				// check percent encoded characters
				int percentEnc = decodeHex(iri, i);
				if (percentEnc == -1)
					throw new RuntimeException("Invalid percent encoding encountered in IRI");
				
				// the percent-encoded sequence is valid and we know that i+1 and i+2 are ASCII
				switch (percentEnc) {
					case '/':
						sb.append('/');
						break;
					case '?':
						sb.append('?');
						break;
					case '#':
						sb.append('#');
						break;
					case '%':
						sb.append('%');
						break;
					default:
						sb.append(iri.substring(i, i+3));
				}
				i += 2;
			}
			else {
				// just append the current codepoint to the buffer
				sb.append(iri.substring(i, i + UTF16.getCharCount(cp)));
			}
		}
		return sb.toString();
	}
	
	
	/**
	 * Transform the given URI to IRI according to the rules in RFC3987 section 3.2 
	 * @param uri - this MUST be a valid URI string. Use the <code>java.net.URI</code> class 
	 * 				to check before using this method.
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String URItoIRI(String uri) throws UnsupportedEncodingException
	{
		int uriLength = uri.length();
		byte[] bb = new byte[uriLength]; // Holds the transformed IRI encoded in UTF-8 (should never be greater than uriLength if URI is in ASCII)
		int bbIndex = 0;

		// Step 1 - Represent the URI as a sequence of octets in US-ASCII.
		//          Not checked because we assume that the URI is valid.
		//          It follows from that that the String is all ASCII. 

		byte[] u8buf = new byte[4]; // max number of bytes for UTF-8 encoded code point
		int i = 0; // uri index
		while (i < uriLength) {
			char cp = uri.charAt(i);

			if (cp > 0x7F)
				throw new RuntimeException("Non ASCII character encountered in URI");
			
			if (cp != '%') {
				// non-percent encoded character, append to byte buffer
				bb[bbIndex++] = (byte)cp;
				i++;
				continue;
			}

			// check that the percent-encoded sequence is valid
			int cpVal;
			try {
				cpVal = decodeHex(uri, i);
			}
			catch (IllegalArgumentException e) {
				throw new RuntimeException("Invalid percent encoding encountered in URI: " + e.getMessage());				
			}

			// TODO should we allow cpVal == 0? Not checking for now because we are not URI component aware
			if (cpVal <= 0x7F) { // isascii(cpVal)
				// Step 2 - decode everything but '%' or reserved or illegal characters in US-ASCII
				int flg = URICHARS[cpVal];
				if (cpVal == '%' || flg == gendl || flg == subdl || flg == notal) {
					bb[bbIndex++] = (byte)uri.charAt(i++);
					bb[bbIndex++] = (byte)uri.charAt(i++);
					bb[bbIndex++] = (byte)uri.charAt(i++);
				}
				else {
					// append decoded
					bb[bbIndex++] = (byte)cpVal;
					i += 3;
				}
				continue;
			}
			
			// test to see if we have a valid UTF-8 sequence
			int n = countUTF8Sequence(uri, i, u8buf);
			if (n > 0) {
				// valid UTF-8 sequence of n-bytes long
				if (hasBiDiChar(new String(u8buf, 0, n, "UTF-8"))) {
					// do not unescape bi-di character (must not be in IRI)
					bb[bbIndex++] = (byte)uri.charAt(i++);
					bb[bbIndex++] = (byte)uri.charAt(i++);
					bb[bbIndex++] = (byte)uri.charAt(i++);					
				}
				else {
					for (int j = 0; j < n; j++) {
						bb[bbIndex++] = u8buf[j];
					}
					i += n*3; // skip n * (%XX sequences)
				}
			}
			else {
				// not a valid UTF-8 sequence, do not unescape it
				bb[bbIndex++] = (byte)uri.charAt(i++);
				bb[bbIndex++] = (byte)uri.charAt(i++);
				bb[bbIndex++] = (byte)uri.charAt(i++);
			}
		}		
		return new String(bb, 0, bbIndex, "UTF-8");
	}
	

	/**
	 * Transforms the given IRI to URI.
	 * @param iri
	 * @return
	 */
	public static String IRItoURI(String iri)
	{
		int iriLen = iri.length();
		StringBuffer sb = new StringBuffer(iriLen * 2);
		
		int cp;
		for (int i = 0; i < iriLen; i += UTF16.getCharCount(cp)) {
			cp = UTF16.charAt(iri, i);
			if (isUCSCharOrIPrivate(cp))
				sb.append(toUTF8PercentEncoded(cp));
			else
				UTF16.append(sb, cp);
		}
		return sb.toString();
	}

	
	protected static String toUTF8PercentEncoded(int cp)
	{
		String s = null;
		try {
			s = URLEncoder.encode(UCharacter.toString(cp), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
		}
		return s;
	}
	
	
	protected static boolean isUCSCharOrIPrivate(int cp)
	{
		return (isUCSChar(cp) || isIPrivate(cp));
	}
	
	protected static boolean isUCSChar(int cp)
	{
		if ((cp >= 0xA0 && cp <= 0xD7FF) ||
				(cp >= 0xF900 && cp <= 0xFDCF) ||
				(cp >= 0xFDF0 && cp <= 0xFFEF) ||
				(cp >= 0x10000 && cp <= 0x1FFFD) ||
				(cp >= 0x20000 && cp <= 0x2FFFD) ||
				(cp >= 0x30000 && cp <= 0x3FFFD) ||
				(cp >= 0x40000 && cp <= 0x4FFFD) ||
				(cp >= 0x50000 && cp <= 0x5FFFD) ||
				(cp >= 0x60000 && cp <= 0x6FFFD) ||
				(cp >= 0x70000 && cp <= 0x7FFFD) ||
				(cp >= 0x80000 && cp <= 0x8FFFD) ||
				(cp >= 0x90000 && cp <= 0x9FFFD) ||
				(cp >= 0xA0000 && cp <= 0xAFFFD) ||
				(cp >= 0xB0000 && cp <= 0xBFFFD) ||
				(cp >= 0xC0000 && cp <= 0xCFFFD) ||
				(cp >= 0xD0000 && cp <= 0xDFFFD) ||
				(cp >= 0xE0000 && cp <= 0xEFFFD))
			return true;
		return false;
	}
	
	protected static boolean isIPrivate(int cp)
	{
		if ((cp >= 0xE000 && cp <= 0xF8FF) ||
				(cp >= 0xF0000 && cp <= 0xFFFFD) ||
				(cp >= 0x100000 && cp <= 0x10FFFD))
			return true;
		return false;
	}

	
	/**
	 * Transforms the given XRI part to IRI-normal form. This method does not parse the given String;
	 * it simply converts all '%' to '%25', and if <code>inXref</code> is true, also percent encodes
	 * '#', '?' and '/'. 
	 * @param s
	 * @param inXref
	 * @return
	 */
	public static String XRItoIRI(String s, boolean inXref)
	{
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '%')
				sb.append("%25");
			else if (inXref) {
				if (c == '#')
					sb.append("%23");
				else if (c == '?')
					sb.append("%3F");
				else if (c == '/')
					sb.append("%2F");
				else
					sb.append(c);
			}
			else
				sb.append(c);
		}
		return sb.toString();
	}
	
	
	private static int countUTF8Sequence(String in, int inIndex, byte[] u8buf)
	{
		int c[] = new int[4];
		
		if (!isPercentEncoded(in, inIndex))
			return 0;
		
		c[0] = decodeHex(in, inIndex);
		switch (UTF8lead[c[0]]) {
			case end: // truncated sequence
			case ill: // illegal in UTF-8
			case trl: // trailer - illegal
			case asc: // this function is not meant to get ASCII
				return 0;
				
			case by2:
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[1] = decodeHex(in, inIndex);
				if (UTF8lead[c[1]] != trl)
					return 0;
				u8buf[0] = (byte)c[0]; u8buf[1] = (byte)c[1];
				return 2;
				
			case e0:
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex) || !isPercentEncoded(in, inIndex+3))
					return 0;
				c[1] = decodeHex(in, inIndex);
				inIndex += 3;
				c[2] = decodeHex(in, inIndex);

				if (((c[1] & 0xE0) != 0xA0) || (UTF8lead[c[2]] != trl))
					return 0;
				u8buf[0] = (byte)c[0]; u8buf[1] = (byte)c[1]; u8buf[2] = (byte)c[2];
				return 3;
				
			case by3:
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex) || !isPercentEncoded(in, inIndex+3))
					return 0;
				c[1] = decodeHex(in, inIndex);
				inIndex += 3;
				c[2] = decodeHex(in, inIndex);

				if ((UTF8lead[c[1]] != trl) || (UTF8lead[c[2]] != trl))
					return 0;
				u8buf[0] = (byte)c[0]; u8buf[1] = (byte)c[1]; u8buf[2] = (byte)c[2];
				return 3;
				
			case ed:
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex) || !isPercentEncoded(in, inIndex+3))
					return 0;
				c[1] = decodeHex(in, inIndex);
				inIndex += 3;
				c[2] = decodeHex(in, inIndex);

				if (((c[1] & 0xE0) != 0x80) || (UTF8lead[c[2]] != trl))
					return 0;
				u8buf[0] = (byte)c[0]; u8buf[1] = (byte)c[1]; u8buf[2] = (byte)c[2];
				return 3;

			case p13:
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[1] = decodeHex(in, inIndex);
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[2] = decodeHex(in, inIndex);
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[3] = decodeHex(in, inIndex);
				
				if ((c[1] < 0x90 || 0xBF < c[1])
						|| (UTF8lead[c[2]] != trl)
						|| (UTF8lead[c[3]] != trl))
					return 0;
				u8buf[0] = (byte)c[0]; u8buf[1] = (byte)c[1]; u8buf[2] = (byte)c[2]; u8buf[3] = (byte)c[3];
				return 4;

			case by4:
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[1] = decodeHex(in, inIndex);
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[2] = decodeHex(in, inIndex);
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[3] = decodeHex(in, inIndex);
				
				if ((UTF8lead[c[1]] != trl)
						|| (UTF8lead[c[2]] != trl)
						|| (UTF8lead[c[3]] != trl))
					return 0;
				u8buf[0] = (byte)c[0]; u8buf[1] = (byte)c[1]; u8buf[2] = (byte)c[2]; u8buf[3] = (byte)c[3];
				return 4;
				
			case p16:
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[1] = decodeHex(in, inIndex);
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[2] = decodeHex(in, inIndex);
				inIndex += 3;
				if (!isPercentEncoded(in, inIndex))
					return 0;
				c[3] = decodeHex(in, inIndex);
				
				if (((c[1] & 0xF0) != 0x80)
						|| (UTF8lead[c[2]] != trl)
						|| (UTF8lead[c[3]] != trl))
					return 0;
				u8buf[0] = (byte)c[0]; u8buf[1] = (byte)c[1]; u8buf[2] = (byte)c[2]; u8buf[3] = (byte)c[3];
				return 4;
				
			default:
				// should never reach here
				return 0;
		}
	}
	
	
	/**
	 * Attempt to read a percent encoded sequence from the given string <code>s</code> at <code>index</code> position.
	 * @param s
	 * @param index
	 * @return The percent sequence String of length 3, or <code>null</code> if a valid sequence cannot be read.
	 */
	private static String getHex(String s, int index)
	{
		// make sure the string is long enough
		if (s.length() < index + 3)
			return null;
		
		if (s.charAt(index) != '%')
			return null;
		
		int c1 = (int)Character.toUpperCase(s.charAt(index + 1));
		int c2 = (int)Character.toUpperCase(s.charAt(index + 2));

		if (c1 >= HEXCHARS.length || HEXCHARS[c1] == -1 ||
				c2 >= HEXCHARS.length || HEXCHARS[c2] == -1)
			return null; // invalid hex chars

		return s.substring(index, index + 3);
	}
	
	
	/**
	 * Attempts to decode a 3-character percent-encoded sequence to the numeric value.
	 * @param s
	 * @param index
	 * @return the numeric value of the %XX sequence, or -1 if the sequence is invalid.
	 */
	public static int decodeHex(String s, int index)
	{
		// make sure the string is long enough
		if (s.length() < index + 3)
			throw new IllegalArgumentException("Incomplete percent escape");
		
		if (s.charAt(index) != '%')
			return -1;
		
		int c1 = (int)Character.toUpperCase(s.charAt(index + 1));
		int c2 = (int)Character.toUpperCase(s.charAt(index + 2));
		int c1val, c2val;

		if (c1 >= HEXCHARS.length || c2 >= HEXCHARS.length)
			throw new IllegalArgumentException("Illegal hex characters");

		c1val = HEXCHARS[c1];
		c2val = HEXCHARS[c2];

		if (c1val == -1 || c2val == -1)
			throw new IllegalArgumentException("Illegal hex characters");
		
		return c1val * 16 + c2val;
	}
	
	private static boolean isPercentEncoded(String s, int index)
	{
		// make sure the string is long enough
		if (s.length() < index + 3)
			return false;

		if (s.charAt(index) != '%')
			return false;

		int c1 = (int)Character.toUpperCase(s.charAt(index + 1));
		int c2 = (int)Character.toUpperCase(s.charAt(index + 2));

		if (c1 >= HEXCHARS.length || c2 >= HEXCHARS.length)
			return false; // invalid hex chars

		return true;
	}
	
	/**
	 * 
	 * @param s
	 * @return true if any of (LRM, RLM, LRE, RLE, LRO, RLO, or PDF) exists in <code>s</code>
	 */
	private static boolean hasBiDiChar(String s)
	{
		int cp;
		for (int i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
			cp = UTF16.charAt(s, i);
			if (cp == 0x200E // LRM
					|| cp == 0x200F // RLM
					|| cp == 0x202A // LRE
					|| cp == 0x202B // RLE
					|| cp == 0x202D // LRO
					|| cp == 0x202E // RLO
					|| cp == 0x202C // PDF
					)
				return true;
		}
		return false;
	}
}
