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
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.syntax.parser.ParserException;
import xdi2.core.util.AddressUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class XDIJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONReader.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJSONReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	public void read(XdiRoot root, JsonObject jsonGraphObject, State state) throws IOException, Xdi2ParseException {

		for (Entry<String, JsonElement> entry : jsonGraphObject.entrySet()) {

			String key = entry.getKey();
			JsonElement jsonEntryElement = entry.getValue();

			if (key.endsWith("/" + XDIConstants.XDI_ADD_CONTEXT.toString())) {

				XDIStatement statementXri = makeStatement(key + "/", state);

				if (! (jsonEntryElement instanceof JsonArray)) throw new Xdi2ParseException("JSON object member must be an array: " + jsonEntryElement);

				JsonArray jsonEntryArray = (JsonArray) jsonEntryElement;

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.getRoot(statementXri.getSubject(), true);
				XDIAddress absoluteSubject = root.relativeToAbsoluteXri(statementXri.getSubject());
				XDIAddress relativeSubject = statementRoot.absoluteToRelativeXri(absoluteSubject);
				ContextNode baseContextNode = relativeSubject == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativeSubject);

				// add context nodes

				for (JsonElement jsonEntryArrayElement : jsonEntryArray) {

					if (! (jsonEntryArrayElement instanceof JsonPrimitive) || ! ((JsonPrimitive) jsonEntryArrayElement).isString()) throw new Xdi2ParseException("JSON array element must be a string: " + jsonEntryArrayElement);

					XDIArc arc = makeXDIArc(((JsonPrimitive) jsonEntryArrayElement).getAsString(), state);

					ContextNode contextNode = baseContextNode.setContextNode(arc);
					if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getAddress() + ": Set context node " + contextNode.getArc() + " --> " + contextNode.getAddress());
				}
			} else if (key.endsWith("/" + XDIConstants.XDI_ADD_LITERAL.toString())) {

				XDIStatement statementXri = makeStatement(key + "/\"\"", state);

				Object literalData = AbstractLiteral.jsonElementToLiteralData(jsonEntryElement);

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.getRoot(statementXri.getSubject(), true);
				XDIAddress absoluteSubject = root.relativeToAbsoluteXri(statementXri.getSubject());
				XDIAddress relativeSubject = statementRoot.absoluteToRelativeXri(absoluteSubject);
				ContextNode baseContextNode = relativeSubject == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativeSubject);

				// add literal

				Literal literal = baseContextNode.setLiteral(literalData);
				if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getAddress() + ": Set literal --> " + literal.getLiteralData());
			} else {

				XDIStatement statementXri = makeStatement(key + "/", state);

				if (! (jsonEntryElement instanceof JsonArray)) throw new Xdi2ParseException("JSON object member must be an array: " + jsonEntryElement);

				XDIAddress arc = statementXri.getPredicate();
				JsonArray jsonEntryArray = (JsonArray) jsonEntryElement;

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.getRoot(statementXri.getSubject(), true);
				XDIAddress absoluteSubject = root.relativeToAbsoluteXri(statementXri.getSubject());
				XDIAddress relativeSubject = statementRoot.absoluteToRelativeXri(absoluteSubject);
				ContextNode baseContextNode = relativeSubject == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativeSubject);

				// add inner root and/or relations

				for (JsonElement jsonEntryArrayElement : jsonEntryArray) {

					// inner root or relation?

					if (jsonEntryArrayElement instanceof JsonObject) {

						root = root.getRoot(statementXri.getSubject(), true);

						XDIAddress subject = root.absoluteToRelativeXri(AddressUtil.concatAddresses(root.getContextNode().getAddress(), statementXri.getSubject()));
						XDIAddress predicate = statementXri.getPredicate();

						XdiInnerRoot innerRoot = root.getInnerRoot(subject, predicate, true);

						this.read(innerRoot, (JsonObject) jsonEntryArrayElement, state);
					} else if (jsonEntryArrayElement instanceof JsonPrimitive && ((JsonPrimitive) jsonEntryArrayElement).isString()) {

						XDIAddress targetContextNodeAddress = makeXDIAddress(((JsonPrimitive) jsonEntryArrayElement).getAsString(), state);

						Relation relation = baseContextNode.setRelation(arc, targetContextNodeAddress);
						if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getAddress() + ": Set relation " + relation.getArc() + " --> " + relation.getTargetContextNodeAddress());
					} else {

						throw new Xdi2ParseException("JSON array element must be either an object or a string: " + jsonEntryArrayElement);
					}
				}
			}
		}
	}

	private void read(Graph graph, BufferedReader bufferedReader, State state) throws IOException, Xdi2ParseException {

		JsonElement jsonGraphElement = gson.getAdapter(JsonObject.class).fromJson(bufferedReader);

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

	private static XDIStatement makeStatement(String xriString, State state) {

		state.lastXriString = xriString;
		return XDIStatement.create(xriString);
	}

	private static XDIAddress makeXDIAddress(String xriString, State state) {

		state.lastXriString = xriString;
		return XDIAddress.create(xriString);
	}

	private static XDIArc makeXDIArc(String xriString, State state) {

		state.lastXriString = xriString;
		return XDIArc.create(xriString);
	}
}
