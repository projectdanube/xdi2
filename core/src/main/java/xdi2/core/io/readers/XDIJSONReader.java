package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.roots.InnerRoot;
import xdi2.core.features.roots.Root;
import xdi2.core.features.roots.Roots;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.parser.aparse.ParserException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class XDIJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONReader.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	public XDIJSONReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	public void read(Root root, JSONObject graphObject, State state) throws IOException, Xdi2ParseException, JSONException {

		for (Entry<String, Object> entry : graphObject.entrySet()) {

			if (! (entry.getValue() instanceof JSONArray)) throw new Xdi2ParseException("Value for key " + entry.getKey() + " must be a JSON array");

			String key = entry.getKey();
			JSONArray value = (JSONArray) entry.getValue();

			if (key.endsWith("/" + XDIConstants.XRI_S_CONTEXT.toString())) {

				XDI3Statement statementXri = makeStatement(key + "/()", state);

				// add context nodes

				ContextNode contextNode = root.getContextNode().findContextNode(statementXri.getSubject(), true);

				for (int i=0; i<value.size(); i++) {

					XDI3SubSegment arcXri = makeXDI3SubSegment(value.getString(i), state);

					// ignore implied context nodes

					if (contextNode.containsContextNode(arcXri)) {

						continue;
					} else {

						ContextNode innerContextNode = contextNode.createContextNode(arcXri);
						if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created context node " + innerContextNode.getArcXri() + " --> " + innerContextNode.getXri());
					}
				}
			} else if (key.endsWith("/" + XDIConstants.XRI_S_LITERAL.toString())) {

				XDI3Statement statementXri = makeStatement(key + "/()", state);

				// add literal

				ContextNode contextNode = root.getContextNode().findContextNode(statementXri.getSubject(), true);

				if (value.size() != 1) throw new Xdi2ParseException("JSON array for key " + key + " must have exactly one item");

				String literalData = value.getString(0);

				Literal literal = contextNode.createLiteral(literalData);
				if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created literal --> " + literal.getLiteralData());
			} else {

				XDI3Statement statementXri = makeStatement(key + "/()", state);
				XDI3Segment arcXri = statementXri.getPredicate();

				// add relations

				for (int i=0; i<value.size(); i++) {

					// inner root?

					JSONObject jsonObjectInnerRoot;

					try {

						jsonObjectInnerRoot = value.getJSONObject(i);
					} catch (ClassCastException ex) {

						jsonObjectInnerRoot = null;
					}

					if (jsonObjectInnerRoot != null) {

						// add inner root

						root = root.findRoot(statementXri.getSubject(), true);

						XDI3Segment subject = root.getRelativePart(statementXri.getSubject());
						XDI3Segment predicate = statementXri.getPredicate();
						
						InnerRoot innerRoot = root.findInnerRoot(subject, predicate, true);

						this.read(innerRoot, jsonObjectInnerRoot, state);
					} else {

						// add relation

						ContextNode contextNode = root.getContextNode().findContextNode(statementXri.getSubject(), true);

						String valueString = value.getString(i);

						XDI3Segment targetContextNodeXri = makeXDI3Segment(valueString, state);

						Relation relation = contextNode.createRelation(arcXri, root.getContextNode().findContextNode(targetContextNodeXri, true));
						if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created relation " + relation.getArcXri() + " --> " + relation.getTargetContextNodeXri());
					}
				}
			}
		}
	}

	private void read(Graph graph, BufferedReader bufferedReader, State state) throws IOException, Xdi2ParseException, JSONException {

		String line;
		StringBuilder graphString = new StringBuilder();

		while ((line = bufferedReader.readLine()) != null) {

			graphString.append(line + "\n");
		}

		this.read(Roots.findLocalRoot(graph), JSON.parseObject(graphString.toString()), state);
	}

	@Override
	public Reader read(Graph graph, Reader reader) throws IOException, Xdi2ParseException {

		State state = new State();

		try {

			this.read(graph, new BufferedReader(reader), state);
		} catch (JSONException ex) {

			throw new Xdi2ParseException("JSON parse error: " + ex.getMessage(), ex);
		} catch (Xdi2GraphException ex) {

			throw new Xdi2ParseException("Graph problem: " + ex.getMessage(), ex);
		} catch (ParserException ex) {

			throw new Xdi2ParseException("Cannot parse XRI " + state.lastXriString + ": " + ex.getMessage(), ex);
		}

		return reader;
	}

	private static class State {

		private String lastXriString;
	}

	private static XDI3Statement makeStatement(String xriString, State state) throws Xdi2ParseException {

		state.lastXriString = xriString;
		return XDI3Statement.create(xriString);
	}

	private static XDI3Segment makeXDI3Segment(String xriString, State state) {

		state.lastXriString = xriString;
		return XDI3Segment.create(xriString);
	}

	private static XDI3SubSegment makeXDI3SubSegment(String xriString, State state) {

		state.lastXriString = xriString;
		return XDI3SubSegment.create(xriString);
	}
}
