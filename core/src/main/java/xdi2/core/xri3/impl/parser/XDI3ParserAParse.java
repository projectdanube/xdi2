package xdi2.core.xri3.impl.parser;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3Statement;
import xdi2.core.xri3.impl.XDI3SubSegment;
import xdi2.core.xri3.impl.XDI3XRef;
import xdi2.core.xri3.impl.parser.aparse.Parser;
import xdi2.core.xri3.impl.parser.aparse.Rule;
import xdi2.core.xri3.impl.parser.aparse.Rule$IRI;
import xdi2.core.xri3.impl.parser.aparse.Rule$gcs_char;
import xdi2.core.xri3.impl.parser.aparse.Rule$lcs_char;
import xdi2.core.xri3.impl.parser.aparse.Rule$literal;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_address;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_context;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_global_subseg;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_local_subseg;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_object;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_predicate;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_segment;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_statement;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_subject;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_subseg;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_xref;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_xref_IRI;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_xref_address;
import xdi2.core.xri3.impl.parser.aparse.Rule$xdi_xref_empty;
import xdi2.core.xri3.impl.parser.aparse.Rule$xref_IRI;
import xdi2.core.xri3.impl.parser.aparse.Rule$xref_empty;
import xdi2.core.xri3.impl.parser.aparse.Rule$xref_xri_reference;

public class XDI3ParserAParse extends XDI3Parser {

	@Override
	public XDI3Statement parseXDI3Statement(String string) {

		return parseXDI3Statement(Parser.parse("xdi-statement", string));
	}

	private XDI3Statement parseXDI3Statement(Rule rule) {

		String string = rule.spelling;
		XDI3Segment subject = null;
		XDI3Segment predicate = null;
		XDI3Segment object = null;

		// read xdi_subject from xdi_statement

		List<Rule> list_xdi_statement = ((Rule$xdi_statement) rule).rules;
		rule = list_xdi_statement.get(0);	// xdi_subject

		// read xdi_segment from xdi_subject

		List<Rule> list_xdi_subject = ((Rule$xdi_subject) rule).rules;
		rule = list_xdi_subject.get(0); // xdi_segment
		subject = parseXDI3Segment(rule);

		// read xdi_predicate from xdi_statement

		rule = list_xdi_statement.get(2);	// xdi_predicate

		// read xdi_segment from xdi_predicate

		List<Rule> list_xdi_predicate = ((Rule$xdi_predicate) rule).rules;
		rule = list_xdi_predicate.get(0); // xdi_segment
		predicate = parseXDI3Segment(rule);

		// read xdi_object from xdi_statement

		rule = list_xdi_statement.get(4);	// xdi_object

		// read xdi_segment from xdi_object

		List<Rule> list_xdi_object = ((Rule$xdi_object) rule).rules;
		rule = list_xdi_object.get(0); // xdi_segment
		object = parseXDI3Segment(rule);

		// done

		return new XDI3Statement(string, subject, predicate, object);
	}

	@Override
	public XDI3Segment parseXDI3Segment(String string) {

		return parseXDI3Segment(Parser.parse("xdi-segment", string));
	}

	private XDI3Segment parseXDI3Segment(Rule rule) {

		String string = rule.spelling;
		String literal = null;
		List<XDI3SubSegment> subSegments = new ArrayList<XDI3SubSegment> ();

		// read literal or xdi_subseg from xdi_segment

		List<Rule> list_xdi_segment = ((Rule$xdi_segment) rule).rules;
		rule = list_xdi_segment.get(0);	// literal or xdi_subseg

		// literal or xdi_subseg?

		if (rule instanceof Rule$literal) {

			literal = ((Rule$literal) rule).spelling;
		} else if (rule instanceof Rule$xdi_subseg) {

			subSegments.add(parseXDI3SubSegment(rule));
		} else {

			throw new ClassCastException(rule.getClass().getName());
		}

		// read xdi_subsegs from xdi_segment

		if (list_xdi_segment.size() < 2) return new XDI3Segment(rule.spelling, literal, subSegments);
		for (int i=1; i<list_xdi_segment.size(); i++) {

			rule = list_xdi_segment.get(i);	// xdi_subseg
			subSegments.add(parseXDI3SubSegment(rule));
		}

		// done

		return new XDI3Segment(string, literal, subSegments);
	}

	@Override
	public XDI3SubSegment parseXDI3SubSegment(String string) {

		return parseXDI3SubSegment(Parser.parse("xdi-subseg", string));
	}

	private XDI3SubSegment parseXDI3SubSegment(Rule rule) {

		String string = rule.spelling;
		Character gcs = null;
		Character lcs = null;
		String literal = null;
		XDI3XRef xref = null;

		// xdi_subseg?

		if (rule instanceof Rule$xdi_subseg) {

			// read xdi_global_subseg or xdi_local_subseg or xdi_xref from xdi_subseg

			List<Rule> list_xdi_subseg = ((Rule$xdi_subseg) rule).rules;
			rule = list_xdi_subseg.get(0);	// xdi_global_subseg or xdi_local_subseg or xdi_xref
		}

		// xdi_global_subseg?

		if (rule instanceof Rule$xdi_global_subseg) {

			// read gcs_char from xdi_global_subseg;

			List<Rule> list_xdi_global_subseg = ((Rule$xdi_global_subseg) rule).rules;
			rule = list_xdi_global_subseg.get(0);	// gcs_char
			gcs = new Character(((Rule$gcs_char) rule).spelling.charAt(0));

			// read xdi_local_subseg or xdi_xref or literal from xdi_global_subseg

			if (list_xdi_global_subseg.size() < 2) return new XDI3SubSegment(rule.spelling, gcs, lcs, literal, xref);
			rule = list_xdi_global_subseg.get(1);	// xdi_local_subseg or xdi_xref or literal
		}

		// xdi_local_subseg?

		if (rule instanceof Rule$xdi_local_subseg) {

			// read lcs_char from xdi_local_subseg;

			List<Rule> list_xdi_local_subseg = ((Rule$xdi_local_subseg) rule).rules;
			rule = list_xdi_local_subseg.get(0);	// lcs_char
			lcs = new Character(((Rule$lcs_char) rule).spelling.charAt(0));

			// read xdi_xref or literal from local_subseg

			if (list_xdi_local_subseg.size() < 2) return new XDI3SubSegment(rule.spelling, gcs, lcs, literal, xref);
			rule = list_xdi_local_subseg.get(1);	// xdi_xref or literal
		}

		// literal or xdi_xref?

		if (rule instanceof Rule$literal) {

			literal = ((Rule$literal) rule).spelling;
		} else if (rule instanceof Rule$xdi_xref) {

			xref = parseXDI3XRef(rule);
		}

		// done

		return new XDI3SubSegment(string, gcs, lcs, literal, xref);
	}

	@Override
	public XDI3XRef parseXDI3XRef(String string) {

		return parseXDI3XRef(Parser.parse("xdi-xref", string));
	}

	private XDI3XRef parseXDI3XRef(Rule rule) {

		String string = rule.spelling;
		XDI3Segment segment = null;
		XDI3Statement statement = null;
		String IRI = null;

		// xdi_xref or xdi_xref_empty or xdi_xref_address or xdi_xref_IRI ?

		if (rule instanceof Rule$xdi_xref) {

			List<Rule> list_xdi_xref = ((Rule$xdi_xref) rule).rules;
			rule = list_xdi_xref.get(0);	// xdi_xref_empty or xdi_xref_address or xdi_xref_IRI
		} else if (rule instanceof Rule$xref_empty) {

		} else if (rule instanceof Rule$xref_xri_reference) {

		} else if (rule instanceof Rule$xref_IRI) {

		} else {

			throw new ClassCastException(rule.getClass().getName());
		}

		// xdi_xref_empty or xdi_xref_address or xdi_xref_IRI ?

		if (rule instanceof Rule$xdi_xref_empty) {

		} else if (rule instanceof Rule$xdi_xref_address) {

			// read xdi_address from xref_xdi_address

			List<Rule> list_xdi_xref_address = ((Rule$xdi_xref_address) rule).rules;
			rule = list_xdi_xref_address.get(1);	// xdi_address

			// read xdi_context or xdi_statement from xdi_address

			List<Rule> list_xdi_address = ((Rule$xdi_address) rule).rules;
			rule = list_xdi_address.get(0);	// xdi_context or xdi_statement

			// xdi_context or xdi_statement ?

			if (rule instanceof Rule$xdi_context) {

				// read xdi_segment from xdi_context

				List<Rule> list_xdi_context = ((Rule$xdi_context) rule).rules;
				rule = list_xdi_context.get(0);	// xdi_segment
				segment = parseXDI3Segment(rule);
			} else if (rule instanceof Rule$xdi_statement) {

				statement = parseXDI3Statement(rule);
			}
		} else if (rule instanceof Rule$xdi_xref_IRI) {

			// read IRI from xdi_xref_IRI

			List<Rule> list_xdi_xref_IRI = ((Rule$xdi_xref_IRI) rule).rules;
			rule = list_xdi_xref_IRI.get(1);	// IRI
			IRI = ((Rule$IRI) rule).spelling;
		} else {

			throw new ClassCastException(rule.getClass().getName());
		}

		// done

		return new XDI3XRef(string, segment, statement, IRI);
	}
}
