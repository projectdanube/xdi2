package xdi2.core.xri3.parser.aparse;

import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

/**
 * An XRI parser based on the recursive-descent APG parser generator. 
 * Parts of this parser have been automatically generated from an ABNF.  
 * @see http://www.coasttocoastresearch.com/
 */
public class XDI3ParserAParse extends XDI3Parser {

	@Override
	public XDI3Statement parseXDI3Statement(String string) {

		return parseXDI3Statement(Parser.parse("xdi-statement", string));
	}

	private XDI3Statement parseXDI3Statement(Rule rule) {

		/*		String string = rule.spelling;
		XDI3Segment subject = null;
		XDI3Segment predicate = null;
		XDI3Segment object = null;

		// read xdi_subject from xdi_statement

		List<Rule> list_xdi_statement = ((Rule_xdi_statement) rule).rules;
		rule = list_xdi_statement.get(0);	// xdi_subject

		// read xdi_segment from xdi_subject

		List<Rule> list_xdi_subject = ((Rule_xdi_subject) rule).rules;
		rule = list_xdi_subject.get(0); // xdi_segment
		subject = parseXDI3Segment(rule);

		// read xdi_predicate from xdi_statement

		rule = list_xdi_statement.get(2);	// xdi_predicate

		// read xdi_segment from xdi_predicate

		List<Rule> list_xdi_predicate = ((Rule_xdi_predicate) rule).rules;
		rule = list_xdi_predicate.get(0); // xdi_segment
		predicate = parseXDI3Segment(rule);

		// read xdi_object from xdi_statement

		rule = list_xdi_statement.get(4);	// xdi_object

		// read xdi_segment from xdi_object

		List<Rule> list_xdi_object = ((Rule_xdi_object) rule).rules;
		rule = list_xdi_object.get(0); // xdi_segment
		object = parseXDI3Segment(rule);

		// done

		return this.makeXDI3Statement(string, subject, predicate, object);*/

		return null;
	}

	@Override
	public XDI3Segment parseXDI3Segment(String string) {

		return parseXDI3Segment(Parser.parse("xdi-segment", string));
	}

	private XDI3Segment parseXDI3Segment(Rule rule) {

		/*		String string = rule.spelling;
		List<XDI3SubSegment> subSegments = new ArrayList<XDI3SubSegment> ();

		// read subsegs from xdi_segment

		List<Rule> list_xdi_segment = ((Rule_xdi_segment) rule).rules;

		for (int i=0; i<list_xdi_segment.size(); i++) {

			rule = list_xdi_segment.get(i);	// subseg
			subSegments.add(parseXDI3SubSegment(rule));
		}

		// done

		return this.makeXDI3Segment(string, subSegments);*/

		return null;
	}

	@Override
	public XDI3SubSegment parseXDI3SubSegment(String string) {

		return parseXDI3SubSegment(Parser.parse("subseg", string));
	}

	private XDI3SubSegment parseXDI3SubSegment(Rule rule) {

		/*String string = rule.spelling;
		Character gcs = null;
		Character lcs = null;
		String literal = null;
		XDI3XRef xref = null;

		// subseg?

		if (rule instanceof Rule_subseg) {

			// read global_subseg or local_subseg or xref from subseg

			List<Rule> list_subseg = ((Rule_subseg) rule).rules;
			rule = list_subseg.get(0);	// global_subseg or local_subseg or xref
		}

		// global_subseg?

		if (rule instanceof Rule_global_subseg) {

			// read gcs_char from global_subseg;

			List<Rule> list_global_subseg = ((Rule_global_subseg) rule).rules;
			rule = list_global_subseg.get(0);	// gcs_char
			gcs = new Character(((Rule_gcs_char) rule).spelling.charAt(0));

			// read local_subseg or xref or literal from global_subseg

			if (list_global_subseg.size() < 2) return this.makeXDI3SubSegment(rule.spelling, gcs, literal, xref);
			rule = list_global_subseg.get(1);	// local_subseg or xref or literal
		}

		// local_subseg?

		if (rule instanceof Rule_local_subseg) {

			// read lcs_char from local_subseg;

			List<Rule> list_local_subseg = ((Rule_local_subseg) rule).rules;
			rule = list_local_subseg.get(0);	// lcs_char
			lcs = new Character(((Rule_lcs_char) rule).spelling.charAt(0));

			// read xref or literal from local_subseg

			if (list_local_subseg.size() < 2) return this.makeXDI3SubSegment(rule.spelling, gcs, literal, xref);
			rule = list_local_subseg.get(1);	// xref or literal
		}

		// literal or xref?

		if (rule instanceof Rule_literal) {

			literal = ((Rule_literal) rule).spelling;
		} else if (rule instanceof Rule_xref) {

			xref = parseXDI3XRef(rule);
		}

		// done

		return this.makeXDI3SubSegment(string, gcs, literal, xref);*/

		return null;
	}

	@Override
	public XDI3XRef parseXDI3XRef(String string) {

		return parseXDI3XRef(Parser.parse("xref", string));
	}

	private XDI3XRef parseXDI3XRef(Rule rule) {

		/*String string = rule.spelling;
		XDI3Segment segment = null;
		XDI3Statement statement = null;
		XDI3Segment partialSubject = null;
		XDI3Segment partialPredicate = null;
		String IRI = null;
		String literal = null;

		// xref or xref_empty or or xref_IRI or xref_segment or xref_statement or xref_literal ?

		if (rule instanceof Rule_xref) {

			List<Rule> list_xref = ((Rule_xref) rule).rules;
			rule = list_xref.get(0);	// xref_empty or or xref_IRI or xref_segment or xref_subject_predicate or xref_statement
		} else if (rule instanceof Rule_xref_empty) {

		} else if (rule instanceof Rule_xref_IRI) {

		} else if (rule instanceof Rule_xref_segment) {

		} else if (rule instanceof Rule_xref_subject_predicate) {

		} else if (rule instanceof Rule_xref_statement) {

		} else if (rule instanceof Rule_xref_literal) {

		} else {

			throw new ClassCastException(rule.getClass().getName());
		}

		// xref_empty or or xref_IRI or xref_segment or xref_subject_predicate or xref_statement or xref_literal ?

		if (rule instanceof Rule_xref_empty) {

		} else if (rule instanceof Rule_xref_IRI) {

			// read IRI from xref_IRI

			List<Rule> list_xref_IRI = ((Rule_xref_IRI) rule).rules;
			rule = list_xref_IRI.get(1);	// IRI
			IRI = ((Rule_IRI) rule).spelling;
		} else if (rule instanceof Rule_xref_segment) {

			// read xdi_segment from xref_segment

			List<Rule> list_xref_segment = ((Rule_xref_segment) rule).rules;
			rule = list_xref_segment.get(1);	// xdi_segment

			segment = parseXDI3Segment(rule);
		} else if (rule instanceof Rule_xref_subject_predicate) {

			// read xdi_subject from xref_subject_predicate

			List<Rule> list_xref_subject_predicate = ((Rule_xref_subject_predicate) rule).rules;
			rule = list_xref_subject_predicate.get(1);	// xdi_subject

			// read xdi_segment from xdi_subject

			List<Rule> list_xdi_subject = ((Rule_xdi_subject) rule).rules;
			rule = list_xdi_subject.get(0); // xdi_segment
			partialSubject = parseXDI3Segment(rule);

			// read xdi_predicate from xref_subject_predicate

			rule = list_xref_subject_predicate.get(3);	// xdi_predicate

			// read xdi_segment from xdi_predicate

			List<Rule> list_xdi_predicate = ((Rule_xdi_predicate) rule).rules;
			rule = list_xdi_predicate.get(0); // xdi_segment
			partialPredicate = parseXDI3Segment(rule);
		} else if (rule instanceof Rule_xref_statement) {

			// read xdi_statement from xref_statement

			List<Rule> list_xref_statement = ((Rule_xref_statement) rule).rules;
			rule = list_xref_statement.get(1);	// xdi_statement

			statement = parseXDI3Statement(rule);
		} else if (rule instanceof Rule_xref_literal) {

			// read literal from xref_literal

			List<Rule> list_xref_literal = ((Rule_xref_literal) rule).rules;
			rule = list_xref_literal.get(1);	// literal
			literal = ((Rule_literal) rule).spelling;
		} else {

			throw new ClassCastException(rule.getClass().getName());
		}

		// done

		return this.makeXDI3XRef(string, "", segment, statement, partialSubject, partialPredicate, IRI, literal);*/

		return null;
	}
}
