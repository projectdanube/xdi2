package xdi2.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.constants.TypeConstants;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

@SuppressWarnings("unchecked")
public class XdiHinUtil {

	public static final XRI3Segment XRI_HIN = new XRI3Segment("$value$hash");
	
	public static final String ALGORITHM_SHA1 = "SHA-1";
	public static final String ALGORITHM_SHA256 = "SHA-256";
	public static final String ALGORITHM_MD5 = "MD5";

	public static final String ALGORITHM_DEFAULT = ALGORITHM_SHA256;
	
	private XdiHinUtil() { }

	public static XRI3Segment makeHin(Subject subject, String algorithm) {

		return(new XRI3Segment(XRI_HIN.toString() + algorithmToSubSegment(algorithm) + "!" + hashhex(makeString(subject), algorithm)));
	}

	public static String makeString(Subject subject) {

		if (subject.containsPredicate(TypeConstants.XRI_VALUE) && subject.getPredicate(TypeConstants.XRI_VALUE).containsLiteral()) {

			return(makeSimpleValueString(subject));
		} else {

			return(makeComplexValueString(subject));
		}
	}

	public static String makeSimpleValueString(Subject subject) {

		return(subject.getPredicate(TypeConstants.XRI_VALUE).getLiteral().getData());
	}

	public static String makeComplexValueString(Subject subject) {

		StringBuffer buffer = new StringBuffer();

		// get all predicates and sort them

		List<XRI3Segment> predicateXris = new ArrayList();

		for (Iterator<Predicate> predicates = subject.getPredicates(); predicates.hasNext(); ) {

			Predicate predicate = predicates.next();
			predicateXris.add(predicate.getPredicateXri());
		}

		Collections.sort(predicateXris);

		// iterate through the predicate XRIs

		for (Iterator<XRI3Segment> i = predicateXris.iterator(); i.hasNext(); ) {

			// append the predicate XRI to the buffer

			XRI3Segment predicateXri = i.next();

			buffer.append(predicateXri.toString());
			buffer.append(' ');

			// iterate through the predicates

			List simpleValuesStrings = new ArrayList();
			List complexValuesStrings = new ArrayList();

			Predicate predicate = subject.getPredicate(predicateXri);

			if (predicate.containsLiteral()) {

				simpleValuesStrings.add('"' + predicate.getLiteral().getData() + '"' + ' ');
			} else if (predicate.containsReferences()) {

				for (Iterator<Reference> references = predicate.getReferences(); references.hasNext(); ) {

					Reference reference = references.next();
					simpleValuesStrings.add('"' + reference.getReferenceXri().toString() + '"' + ' ');
				}
			} else if (predicate.containsInnerGraph()) {

				for (Iterator<Subject> innerSubjects = predicate.getInnerGraph().getSubjects(); innerSubjects.hasNext(); ) {

					Subject innerSubject = innerSubjects.next();

					if (innerSubject.containsPredicate(TypeConstants.XRI_VALUE) && innerSubject.getPredicate(TypeConstants.XRI_VALUE).containsLiteral()) {

						simpleValuesStrings.add('"' + innerSubject.getPredicate(TypeConstants.XRI_VALUE).getLiteral().getData() + '"' + ' ');
					} else {

						complexValuesStrings.add(makeComplexValueString(innerSubject) + ' ');
					}
				}
			}

			// sort the values and add them to the buffer

			Collections.sort(simpleValuesStrings);
			Collections.sort(complexValuesStrings);

			for (Iterator iii = simpleValuesStrings.iterator(); iii.hasNext(); ) buffer.append((String) iii.next());
			for (Iterator iii = complexValuesStrings.iterator(); iii.hasNext(); ) buffer.append((String) iii.next());
		}

		// done

		return(buffer.toString());
	}

	public static String hashhex(String string, String algorithm) {

		MessageDigest digest;

		try {

			digest = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {

			throw new RuntimeException(ex);
		}

		byte[] bytes;
		String hash;

		try {

			digest.update(string.getBytes("UTF-8"));
			bytes = digest.digest();
			hash = new String(Hex.encodeHex(bytes));
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex);
		}

		return(hash);
	}

	public static String algorithmToSubSegment(String algorithm) {

		if (algorithm.equals(ALGORITHM_SHA1)) return("$sha1");
		if (algorithm.equals(ALGORITHM_SHA256)) return("$sha256");
		if (algorithm.equals(ALGORITHM_MD5)) return("$md5");

		return(null);
	}

	public static String segmentToAlgorithm(XRI3Segment xri) {

		if (! isHin(xri)) return(null);

		if (xri.getNumSubSegments() < 3) return(ALGORITHM_DEFAULT);
		if (xri.getSubSegment(2).toString().equals("$sha1")) return(ALGORITHM_SHA1);
		if (xri.getSubSegment(2).toString().equals("$sha256")) return(ALGORITHM_SHA256);
		if (xri.getSubSegment(2).toString().equals("$md5")) return(ALGORITHM_MD5);

		return(null);
	}

	public static boolean isHin(XRI3Segment xri) {

		if (! xri.toString().startsWith(XRI_HIN.toString())) return(false);

		return(true);
	}
}
