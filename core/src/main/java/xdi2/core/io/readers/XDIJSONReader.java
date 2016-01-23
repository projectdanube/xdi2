package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.syntax.parser.ParserException;

public class XDIJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONReader.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = null;

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

			if (key.endsWith("/" + XDIConstants.STRING_CONTEXT)) {

				XDIStatement XDIstatement = makeStatement(key + "/<$x>", state);

				if (! (jsonEntryElement instanceof JsonArray)) throw new Xdi2ParseException("JSON object member must be an array: " + jsonEntryElement);

				JsonArray jsonEntryArray = (JsonArray) jsonEntryElement;

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.getRoot(XDIstatement.getContextNodeXDIAddress(), true);
				XDIAddress absoluteSubject = root.relativeToAbsoluteXDIAddress(XDIstatement.getContextNodeXDIAddress());
				XDIAddress relativeSubject = statementRoot.absoluteToRelativeXDIAddress(absoluteSubject);
				ContextNode baseContextNode = relativeSubject == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativeSubject);

				// add context nodes

				for (JsonElement jsonEntryArrayElement : jsonEntryArray) {

					if (! (jsonEntryArrayElement instanceof JsonPrimitive) || ! ((JsonPrimitive) jsonEntryArrayElement).isString()) throw new Xdi2ParseException("JSON array element must be a string: " + jsonEntryArrayElement);

					XDIArc XDIarc = makeXDIArc(((JsonPrimitive) jsonEntryArrayElement).getAsString(), state);

					ContextNode contextNode = baseContextNode.setContextNode(XDIarc);
					if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getXDIAddress() + ": Set context node " + contextNode.getXDIArc() + " --> " + contextNode.getXDIAddress());
				}
			} else if (key.endsWith("/" + XDIConstants.XDI_ARC_LITERAL.toString())) {

				XDIStatement XDIstatement = makeStatement(key + "/\"\"", state);

				Object literalData = AbstractLiteralNode.jsonElementToLiteralData(jsonEntryElement);

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.getRoot(XDIstatement.getContextNodeXDIAddress(), true);
				XDIAddress absoluteSubject = root.relativeToAbsoluteXDIAddress(XDIstatement.getContextNodeXDIAddress());
				XDIAddress relativeSubject = statementRoot.absoluteToRelativeXDIAddress(absoluteSubject);
				ContextNode baseContextNode = relativeSubject == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativeSubject);

				// add literal

				LiteralNode literalNode = baseContextNode.setLiteralNode(literalData);
				if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getXDIAddress() + ": Set literal node --> " + literalNode.getLiteralData());
			} else {

				XDIStatement XDIstatement = makeStatement(key + "/", state);

				if (! (jsonEntryElement instanceof JsonArray)) throw new Xdi2ParseException("JSON object member must be an array: " + jsonEntryElement);

				XDIAddress XDIaddress = XDIstatement.getRelationXDIAddress();
				JsonArray jsonEntryArray = (JsonArray) jsonEntryElement;

				// find the root and the base context node of this statement

				XdiRoot statementRoot = root.getRoot(XDIstatement.getContextNodeXDIAddress(), true);
				XDIAddress absoluteSubject = root.relativeToAbsoluteXDIAddress(XDIstatement.getContextNodeXDIAddress());
				XDIAddress relativeSubject = statementRoot.absoluteToRelativeXDIAddress(absoluteSubject);
				ContextNode baseContextNode = relativeSubject == null ? statementRoot.getContextNode() : statementRoot.getContextNode().setDeepContextNode(relativeSubject);

				// add inner root and/or relations

				for (JsonElement jsonEntryArrayElement : jsonEntryArray) {

					// inner root or relation?

					if (jsonEntryArrayElement instanceof JsonObject) {

						XdiInnerRoot innerRoot = statementRoot.getInnerRoot(relativeSubject, XDIaddress, true);

						this.read(innerRoot, (JsonObject) jsonEntryArrayElement, state);
					} else if (jsonEntryArrayElement instanceof JsonPrimitive && ((JsonPrimitive) jsonEntryArrayElement).isString()) {

						XDIAddress targetXDIAddress = makeXDIAddress(((JsonPrimitive) jsonEntryArrayElement).getAsString(), state);

						Relation relation = baseContextNode.setRelation(XDIaddress, targetXDIAddress);
						if (log.isTraceEnabled()) log.trace("Under " + baseContextNode.getXDIAddress() + ": Set relation " + relation.getXDIAddress() + " --> " + relation.getTargetXDIAddress());
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

		this.read(XdiCommonRoot.findCommonRoot(graph), (JsonObject) jsonGraphElement, state);
	}

	@Override
	public Reader read(Graph graph, Reader reader) throws IOException, Xdi2ParseException {

		State state = new State();

		try {

			this.read(graph, new BufferedReader(reader), state);
		} catch (Xdi2GraphException ex) {

			throw new Xdi2ParseException("Graph problem: " + ex.getMessage(), ex);
		} catch (ParserException ex) {

			throw new Xdi2ParseException("Cannot parse string " + state.lastString + ": " + ex.getMessage(), ex);
		}

		return reader;
	}

	private static class State {

		private String lastString;
	}

	private static XDIStatement makeStatement(String statementString, State state) {

		state.lastString = statementString;
		return XDIStatement.create(statementString);
	}

	private static XDIAddress makeXDIAddress(String addressString, State state) {

		state.lastString = addressString;
		return XDIAddress.create(addressString);
	}

	private static XDIArc makeXDIArc(String arcString, State state) {

		state.lastString = arcString;
		return XDIArc.create(arcString);
	}
}
