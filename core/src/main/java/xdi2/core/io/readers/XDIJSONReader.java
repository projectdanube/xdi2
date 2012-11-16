package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3SubSegment;
import xdi2.core.xri3.impl.parser.ParserException;

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

	public void read(Graph graph, JSONObject graphObject, State state) throws IOException, Xdi2ParseException, JSONException {

		for (Iterator<?> keys = graphObject.keys(); keys.hasNext(); ) {

			String key = (String) keys.next();
			JSONArray value = graphObject.getJSONArray(key);

			if (key.endsWith("/" + XDIConstants.XRI_S_CONTEXT.toString())) {

				Statement statement = makeStatement(key + "/($)", state);
				ContextNode contextNode = graph.findContextNode(statement.getSubject(), true);

				// add context nodes

				for (int i=0; i<value.length(); i++) {

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

				Statement statement = makeStatement(key + "/($)", state);
				ContextNode contextNode = graph.findContextNode(statement.getSubject(), true);

				// add literal

				if (value.length() != 1) throw new Xdi2ParseException("JSON array for key " + key + " must have exactly one item");

				String literalData = value.getString(0);

				Literal literal = contextNode.createLiteral(literalData);
				if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created literal --> " + literal.getLiteralData());
			} else {

				Statement statement = makeStatement(key + "/()", state);
				ContextNode contextNode = graph.findContextNode(statement.getSubject(), true);
				XDI3Segment arcXri = statement.getPredicate();

				// add relations

				for (int i=0; i<value.length(); i++) {

					String valueString = value.getString(i);

					// try parsing the valueString as a possible cross-reference

					JSONObject jsonObject = null;

					try {

						jsonObject = new JSONObject(valueString);
					} catch (JSONException ex) {

					}

					if (jsonObject != null) {

						// if a cross-reference exists, recursively parse each nested JSON and add it as a relation

						/*						Graph innerGraph = MemoryGraphFactory.getInstance().openGraph();
						read(innerGraph, jsonObject, state);

						for (Iterator<Statement> innerStatements = innerGraph.getRootContextNode().getAllStatements(); innerStatements.hasNext(); ) {

							Statement innerStatement = innerStatements.next();
							if (StatementUtil.isImplied(innerStatement)) continue;

							XDI3Segment innerTargetContextNodeXri = makeXDI3Segment("(" + innerStatement.toString() + ")", state);

							Relation innerRelation = contextNode.createRelation(arcXri, innerTargetContextNodeXri);
							if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created relation " + innerRelation.getArcXri() + " --> " + innerRelation.getTargetContextNodeXri());
						}

						innerGraph.close();*/

						for (Iterator<?> innerKeys = jsonObject.keys(); innerKeys.hasNext(); ) {

							String innerKey = (String) innerKeys.next();
							JSONArray innerValue = jsonObject.getJSONArray(innerKey);

							JSONObject innerJSONObject = new JSONObject();
							innerJSONObject.put(innerKey, innerValue);

							Graph innerGraph = MemoryGraphFactory.getInstance().openGraph();
							read(innerGraph, innerJSONObject, state);

							for (Iterator<Statement> innerStatements = innerGraph.getRootContextNode().getAllStatements(); innerStatements.hasNext(); ) {

								Statement innerStatement = innerStatements.next();

								if (StatementUtil.isImplied(innerStatement)) continue;

								//String innerValueString = ("(" + tempGraph.toString(new MimeType("text/xdi")) + ")").replaceAll("[ \n]", "");
								String innerValueString = ("(" + innerStatement.toString() + ")");
								XDI3Segment innerTargetContextNodeXri = makeXDI3Segment(innerValueString, state);

								Relation relation = contextNode.createRelation(arcXri, innerTargetContextNodeXri);
								if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXri() + ": Created relation " + relation.getArcXri() + " --> " + relation.getTargetContextNodeXri());
							}
						}
					} else {

						XDI3Segment targetContextNodeXri = makeXDI3Segment(valueString, state);

						Relation relation = contextNode.createRelation(arcXri, targetContextNodeXri);
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

		this.read(graph, new JSONObject(graphString.toString()), state);
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

	private static Statement makeStatement(String xriString, State state) throws Xdi2ParseException {

		state.lastXriString = xriString;
		return StatementUtil.fromString(xriString);
	}

	private static XDI3Segment makeXDI3Segment(String xriString, State state) {

		state.lastXriString = xriString;
		return new XDI3Segment(xriString);
	}

	private static XDI3SubSegment makeXDI3SubSegment(String xriString, State state) {

		state.lastXriString = xriString;
		return new XDI3SubSegment(xriString);
	}
}
