package xdi2.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.exceptions.ParseException;
import xdi2.exceptions.Xdi2GraphException;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;
import xdi2.xri3.impl.parser.ParserException;

class XDIJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	protected static final String FORMAT_TYPE = "XDI/JSON";
	protected static final String[] MIME_TYPES = new String[] { "application/xdi+json" };
	protected static final String DEFAULT_FILE_EXTENSION = "json";

	private static final Log log = LogFactory.getLog(XDIJSONReader.class);
	
	private String lastXriString;

	XDIJSONReader() { }

	private synchronized void readContextNode(ContextNode contextNode, JSONObject graphObject) throws IOException, ParseException, JSONException {

		String contextNodeXri = contextNode.getXri().toString();

		String key = contextNodeXri + "/()";
		if (! graphObject.has(key)) return;
		JSONArray jsonArray = graphObject.getJSONArray(key);
		graphObject.remove(key);

		for (int i=0; i<jsonArray.length(); i++) {

			String arcXri = jsonArray.getString(i);
			ContextNode innerContextNode = contextNode.createContextNode(new XRI3SubSegment(arcXri));
			log.debug("Under " + contextNode.getXri() + ": Created context node " + innerContextNode.getArcXri() + " --> " + innerContextNode.getXri());

			this.readContextNode(innerContextNode, graphObject);
		}
	}

	public synchronized void read(Graph graph, JSONObject graphObject) throws IOException, ParseException, JSONException {

		this.readContextNode(graph.getRootContextNode(), graphObject);

		for (Iterator<?> keys = graphObject.keys(); keys.hasNext(); ) {

			String key = (String) keys.next();
			JSONArray value = graphObject.getJSONArray(key);
			if (value.length() != 1) throw new ParserException("JSON array for key " + key + " must have exactly one item");

			String[] strings = key.split("/");
			if (strings.length != 2) throw new ParseException("Invalid key: " + key);

			String subject = strings[0];
			String predicate = strings[1];
			ContextNode contextNode = graph.findContextNode(new XRI3Segment(subject));

			if (predicate.endsWith("!")) {

				XRI3SubSegment arcXri = new XRI3SubSegment(predicate.substring(0, predicate.length() - 1));
				String literalData = value.getString(0);

				Literal literal = contextNode.createLiteral(arcXri, literalData);
				log.debug("Under " + contextNode.getXri() + ": Created literal " + literal.getArcXri() + " --> " + literal.getLiteralData());
			} else {

				XRI3SubSegment arcXri = new XRI3SubSegment(predicate);
				XRI3Segment relationXri = new XRI3Segment(value.getString(0));

				Relation relation = contextNode.createRelation(arcXri, relationXri);
				log.debug("Under " + contextNode.getXri() + ": Created relation " + relation.getArcXri() + " --> " + relation.getRelationXri());
			}
		}
	}

	private void read(Graph graph, BufferedReader bufferedReader) throws IOException, ParseException, JSONException {

		String line;
		StringBuffer graphString = new StringBuffer();

		while ((line = bufferedReader.readLine()) != null) {

			graphString.append(line);
		}

		this.read(graph, new JSONObject(graphString.toString()));
	}

	public synchronized void read(Graph graph, Reader reader, Properties parameters) throws IOException, ParseException {

		this.lastXriString = null;

		try {

			this.read(graph, new BufferedReader(reader));
		} catch (JSONException ex) {

			throw new ParseException("JSON parse error: " + ex.getMessage(), ex);
		} catch (Xdi2GraphException ex) {

			throw new ParseException("Graph problem: " + ex.getMessage(), ex);
		} catch (ParserException ex) {

			throw new ParseException("Cannot parse XRI " + this.lastXriString + ": " + ex.getMessage(), ex);
		}
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
