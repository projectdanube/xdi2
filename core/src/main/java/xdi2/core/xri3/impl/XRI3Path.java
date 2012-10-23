package xdi2.core.xri3.impl;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.xri3.XRIPath;
import xdi2.core.xri3.XRISegment;
import xdi2.core.xri3.impl.parser.Parser;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.core.xri3.impl.parser.Rule;
import xdi2.core.xri3.impl.parser.Rule$xri_path;
import xdi2.core.xri3.impl.parser.Rule$xri_path_abempty;
import xdi2.core.xri3.impl.parser.Rule$xri_path_abs;
import xdi2.core.xri3.impl.parser.Rule$xri_path_noscheme;
import xdi2.core.xri3.impl.parser.Rule$xri_segment;
import xdi2.core.xri3.impl.parser.Rule$xri_segment_nc;
import xdi2.core.xri3.impl.parser.Rule$xri_segment_nz;

public class XRI3Path extends XRI3SyntaxComponent implements XRIPath {

	private static final long serialVersionUID = 482492757184837341L;

	private Rule rule;

	private List segments;

	public XRI3Path(String string) throws ParserException {

		this.rule = Parser.parse("xri-path", string);
		this.read();
	}

	XRI3Path(Rule rule) {

		this.rule = rule;
		this.read();
	}

	private void reset() {
		
		this.segments = new ArrayList();
	}

	private void read() {

		this.reset();
		
		Object object = this.rule;	// xri_path or xri_path_abempty or xri_path_abs or xri_path_noscheme

		// xri_path ?

		if (object instanceof Rule$xri_path) {

			// read xri_path_abempty or xri_path_abs or xri_path_noscheme from xri_path

			List list_xri_path = ((Rule$xri_path) object).rules;
			if (list_xri_path.size() < 1) return;
			object = list_xri_path.get(0);	// xri_path_abempty or xri_path_abs or xri_path_noscheme
		}

		// xri_path_abempty or xri_path_abs or xri_path_noscheme ?

		if (object instanceof Rule$xri_path_abempty) {

			// read xri_segments from xri_path_abempty

			List list_xri_path_abempty = ((Rule$xri_path_abempty) object).rules;
			if (list_xri_path_abempty.size() < 2) return;
			for (int i=0; i+1<list_xri_path_abempty.size(); i+=2) {

				object = list_xri_path_abempty.get(i+1);	// xri_segment
				this.segments.add(new XRI3Segment((Rule$xri_segment) object));
			}
		} else if (object instanceof Rule$xri_path_abs) {

			// read xri_segment_nz from xri_path_abs

			List list_xri_path_abs = ((Rule$xri_path_abs) object).rules;
			if (list_xri_path_abs.size() < 2) return;
			object = list_xri_path_abs.get(1);	// xri_segment_nz
			this.segments.add(new XRI3Segment((Rule$xri_segment_nz) object));
			
			// read xri_segments from xri_path_abs
			
			if (list_xri_path_abs.size() < 4) return;
			for (int i=2; i+1<list_xri_path_abs.size(); i+=2) {
				
				object = list_xri_path_abs.get(i+1);	// xri_segment
				this.segments.add(new XRI3Segment((Rule$xri_segment) object));
			}
		} else if (object instanceof Rule$xri_path_noscheme) {
			
			// read xri_segment_nc from xri_path_noscheme
			
			List list_xri_path_noscheme = ((Rule$xri_path_noscheme) object).rules;
			if (list_xri_path_noscheme.size() < 1) return;
			object = list_xri_path_noscheme.get(0);	// xri_segment_nc
			this.segments.add(new XRI3Segment((Rule$xri_segment_nc) object));
			
			// read xri_segments from xri_path_noscheme
			
			if (list_xri_path_noscheme.size() < 3) return;
			for (int i=1; i+1<list_xri_path_noscheme.size(); i+=2) {
				
				object = list_xri_path_noscheme.get(i+1);	// xri_segment
				this.segments.add(new XRI3Segment((Rule$xri_segment) object));
			}
		} else {
			
			throw new ClassCastException(object.getClass().getName());
		}
	}

	public Rule getParserObject() {

		return(this.rule);
	}

	public List getSegments() {

		return(this.segments);
	}
	
	public int getNumSegments() {
		
		return(this.segments.size());
	}
	
	public XRISegment getSegment(int i) {
		
		return((XRISegment) this.segments.get(i));
	}

	public XRISegment getFirstSegment() {

		if (this.segments.size() < 1) return(null);

		return((XRISegment) this.segments.get(0));
	}

	public XRISegment getLastSegment() {

		if (this.segments.size() < 1) return(null);

		return((XRISegment) this.segments.get(this.segments.size() - 1));
	}

	public boolean startsWith(XRISegment[] segments) {

		if (this.segments.size() < segments.length) return(false);

		for (int i=0; i<segments.length; i++) {

			if (! (this.segments.get(i).equals(segments[i]))) return(false);
		}

		return(true);
	}
}
