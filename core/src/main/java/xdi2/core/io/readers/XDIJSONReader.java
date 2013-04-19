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
import xdi2.core.features.roots.XdiInnerRoot;
import xdi2.core.features.roots.XdiLocalRoot;
import xdi2.core.features.roots.XdiRoot;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.parser.aparse.ParserException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class XDIJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONReader.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	private static final JsonParser jsonParser = new JsonParser();

	public XDIJSONReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	public void read(XdiRoot root, JsonObject jsonGraphObject, State state) throws IOException, Xdi2ParseException {

		for (Entry<String, JsonElement> entry : jsonGraphObject.entrySet()) {

			if (! (entry.getValue() instanceof JsonArray)) throw new Xdi2ParseException("Object member '" + entry.getKey() + "' must be a JSON array");

			String key = entry.getKey();
			JsonArray jsonEntryArray = (JsonArray) entry.getValue();

			if (key.endsWith("/" + XDIConstants.XRI_S_CONTEXT.toString())) {

				XDI3Statement statementXri = makeStatement(key + "/()", state);

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.findRoot(statementXri.getSubject(), true);
				XDI3Segment absoluteSubject = XDI3Util.expandXri(statementXri.getSubject(), root.getContextNode().getXri());
				XDI3Segment relativePart = statementRoot.getRelativePart(absoluteSubject);
				ContextNode baseContextNode = relativePart == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativePart);

				// add context nodes

				for (JsonElement jsonEntryElement : jsonEntryArray) {

					if (! (jsonEntryElement instanceof JsonPrimitive) || ! ((JsonPrimitive) jsonEntryElement).isString()) throw new Xdi2ParseException("Array element must be a string: " + jsonEntryElement);

					XDI3SubSegment arcXri = makeXDI3SubSegment(((JsonPrimitive) jsonEntryElement).getAsString(), state);

					ContextNode contextNode = baseContextNode.setContextNode(arcXri);
					if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getXri() + ": Set context node " + contextNode.getArcXri() + " --> " + contextNode.getXri());
				}
			} else if (key.endsWith("/" + XDIConstants.XRI_S_LITERAL.toString())) {

				XDI3Statement statementXri = makeStatement(key + "/\"\"", state);

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.findRoot(statementXri.getSubject(), true);
				XDI3Segment absoluteSubject = XDI3Util.expandXri(statementXri.getSubject(), root.getContextNode().getXri());
				XDI3Segment relativePart = statementRoot.getRelativePart(absoluteSubject);
				ContextNode baseContextNode = relativePart == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativePart);

				// add literal

				if (jsonEntryArray.size() != 1) throw new Xdi2ParseException("JSON array for key " + key + " must have exactly one item");

				JsonElement jsonEntryElement = jsonEntryArray.get(0);
				if (! (jsonEntryElement instanceof JsonPrimitive) || ! ((JsonPrimitive) jsonEntryElement).isString()) throw new Xdi2ParseException("Array element must be a string: " + jsonEntryElement);

				String literalData = ((JsonPrimitive) jsonEntryElement).getAsString();

				Literal literal = baseContextNode.setLiteral(literalData);
				if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getXri() + ": Set literal --> " + literal.getLiteralData());
			} else {

				XDI3Statement statementXri = makeStatement(key + "/()", state);

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.findRoot(statementXri.getSubject(), true);
				XDI3Segment absoluteSubject = XDI3Util.expandXri(statementXri.getSubject(), root.getContextNode().getXri());
				XDI3Segment relativePart = statementRoot.getRelativePart(absoluteSubject);
				ContextNode baseContextNode = relativePart == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativePart);

				// add inner root and/or relations

				XDI3Segment arcXri = statementXri.getPredicate();

				for (JsonElement jsonEntryElement : jsonEntryArray) {

					// inner root or relation?

					if (jsonEntryElement instanceof JsonObject) {

						root = root.findRoot(statementXri.getSubject(), true);

						XDI3Segment subject = root.getRelativePart(XDI3Util.expandXri(statementXri.getSubject(), root.getContextNode().getXri()));
						XDI3Segment predicate = statementXri.getPredicate();

						XdiInnerRoot innerRoot = root.findInnerRoot(subject, predicate, true);

						this.read(innerRoot, (JsonObject) jsonEntryElement, state);
					} else if (jsonEntryElement instanceof JsonPrimitive && ((JsonPrimitive) jsonEntryElement).isString()) {

						XDI3Segment targetContextNodeXri = makeXDI3Segment(((JsonPrimitive) jsonEntryElement).getAsString(), state);
						targetContextNodeXri = XDI3Util.expandXri(targetContextNodeXri, root.getContextNode().getXri());

						Relation relation = baseContextNode.setRelation(arcXri, targetContextNodeXri);
						if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getXri() + ": Set relation " + relation.getArcXri() + " --> " + relation.getTargetContextNodeXri());
					} else {

						throw new Xdi2ParseException("Array element must be either an object or a string: " + jsonEntryElement);
					}
				}
			}
		}
	}

	private void read(Graph graph, BufferedReader bufferedReader, State state) throws IOException, Xdi2ParseException {

		JsonElement jsonGraphElement = jsonParser.parse(bufferedReader);

		if (! (jsonGraphElement instanceof JsonObject)) throw new Xdi2ParseException("JSON must be an object: " + jsonGraphElement);

		this.read(XdiLocalRoot.findLocalRoot(graph), (JsonObject) jsonGraphElement, state);
	}

	@Override
	public Reader read(Graph graph, Reader reader) throws IOException, Xdi2ParseException {

		State state = new State();

		try {

			this.read(graph, new BufferedReader(reader), state);
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
