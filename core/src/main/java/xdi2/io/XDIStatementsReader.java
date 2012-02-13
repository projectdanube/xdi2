package xdi2.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.exceptions.Xdi2GraphException;
import xdi2.exceptions.Xdi2ParseException;
import xdi2.util.XDIConstants;
import xdi2.util.XDIUtil;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;
import xdi2.xri3.impl.parser.ParserException;

class XDIStatementsReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	protected static final String FORMAT_TYPE = "STATEMENTS";
	protected static final String[] MIME_TYPES = new String[] { "text/plain" };
	protected static final String DEFAULT_FILE_EXTENSION = "xdi";

	private static final Logger log = LoggerFactory.getLogger(XDIStatementsReader.class);

	private String lastXriString;

	XDIStatementsReader() { }

	private synchronized XRI3Segment makeXRI3Segment(String xriString) {

		this.lastXriString = xriString;
		return new XRI3Segment(xriString);
	}

	public synchronized void readStatement(Graph graph, String line, int lineNr) throws IOException, Xdi2ParseException, JSONException {

		String[] segments = line.split("/");
		if (segments.length != 3) throw new Xdi2ParseException("Invalid line #" + lineNr + ":" + line);

		XRI3Segment subject = makeXRI3Segment(segments[0]);
		XRI3Segment predicate = makeXRI3Segment(segments[1]);
		XRI3Segment object = makeXRI3Segment(segments[2]);

		ContextNode contextNode = graph.findContextNode(subject, true);

		if (XDIConstants.XRI_S_CONTEXT.equals(predicate)) {

			ContextNode innerContextNode = contextNode.createContextNode(new XRI3SubSegment(object.toString()));
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created context node " + innerContextNode.getArcXri() + " --> " + innerContextNode.getXri());
		} else if (XDIConstants.XRI_S_LITERAL.equals(predicate)) {

			Literal literal = contextNode.createLiteral(XDIUtil.dataXriSegmentToString(object));
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created literal --> " + literal.getLiteralData());
		} else {

			Relation relation = contextNode.createRelation(predicate, object);
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created relation " + relation.getArcXri() + " --> " + relation.getRelationXri());
		}
	}

	private synchronized void read(Graph graph, BufferedReader bufferedReader) throws IOException, Xdi2ParseException, JSONException {

		String line;
		int lineNr = 0;
		
		while ((line = bufferedReader.readLine()) != null) {
			
			lineNr++;
			this.readStatement(graph, line, lineNr);
		}
	}

	public synchronized Reader read(Graph graph, Reader reader, Properties parameters) throws IOException, Xdi2ParseException {

		this.lastXriString = null;

		try {

			this.read(graph, new BufferedReader(reader));
		} catch (JSONException ex) {

			throw new Xdi2ParseException("JSON parse error: " + ex.getMessage(), ex);
		} catch (Xdi2GraphException ex) {

			throw new Xdi2ParseException("Graph problem: " + ex.getMessage(), ex);
		} catch (ParserException ex) {

			throw new Xdi2ParseException("Cannot parse XRI " + this.lastXriString + ": " + ex.getMessage(), ex);
		}

		return reader;
	}

	public String getFormat() {

		return FORMAT_TYPE;
	}

	public String[] getMimeTypes() {

		return MIME_TYPES;
	}

	public String getDefaultFileExtension() {

		return DEFAULT_FILE_EXTENSION;
	}
}
