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
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.io.util.JXDConstants;
import xdi2.core.io.util.JXDMapping;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.parser.ParserException;

public class XDIJXDReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	private static final Logger log = LoggerFactory.getLogger(XDIJXDReader.class);

	public static final String FORMAT_NAME = "JXD";
	public static final String FILE_EXTENSION = "jxd";
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJXDReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	public void read(ContextNode contextNode, JsonObject jsonGraphObject, JXDMapping baseMapping, State state) throws IOException, Xdi2ParseException {

		// read mapping

		JXDMapping mapping;
		JsonObject jsonObjectMapping = jsonGraphObject.getAsJsonObject(JXDConstants.JXD_MAPPING);

		if (jsonObjectMapping == null) {

			if (baseMapping != null) {

				mapping = baseMapping;
			} else {
				
				mapping = JXDMapping.empty(null);
			}
		} else {

			if (baseMapping != null) {

				mapping = JXDMapping.create(null, baseMapping);
				mapping.merge(jsonObjectMapping);
			} else {

				mapping = JXDMapping.create(null, jsonObjectMapping);
			}
		}

		// read id

		XDIAddress id = jsonGraphObject.has(JXDConstants.JXD_ID) ? makeXDIAddress(jsonGraphObject.get(JXDConstants.JXD_ID).getAsString(), state) : null;

		if (id != null) {

			contextNode = contextNode.setDeepContextNode(id);
		}

		// read type

		XDIAddress type = null;

		String typeString = jsonGraphObject.has(JXDConstants.JXD_TYPE) ? jsonGraphObject.get(JXDConstants.JXD_TYPE).getAsString() : null;
		JXDMapping.JXDTerm typeTerm = typeString == null ? null : mapping.getTerm(typeString);
		if (typeTerm != null && typeTerm.getId() != null) type = typeTerm.getId();
		else if (typeString != null) type = makeXDIAddress(typeString, state);

		if (type != null && ! JXDConstants.JXD_ID.equals(type.toString()) && ! JXDConstants.JXD_GRAPH.equals(type.toString())) {

			Dictionary.setContextNodeType(contextNode, type);
		}

		// parse graph

		for (Entry<String, JsonElement> entry : jsonGraphObject.entrySet()) {

			String entryString = entry.getKey();
			JsonElement entryJsonElement = entry.getValue();

			if (JXDConstants.JXD_MAPPING.equals(entryString)) continue;
			if (JXDConstants.JXD_ID.equals(entryString)) continue;
			if (JXDConstants.JXD_TYPE.equals(entryString)) continue;

			// look up entry term

			JXDMapping.JXDTerm entryTerm = mapping.getTerm(entryString);

			// read entry id

			XDIAddress entryId = null;

			if (entryTerm != null && entryTerm.getId() != null) entryId = entryTerm.getId();
			else entryId = makeXDIAddress(entryString, state);

			// read entry type

			XDIAddress entryType = null;

			if (entryTerm != null && entryTerm.getType() != null) entryType = entryTerm.getType();

			if (entryJsonElement instanceof JsonObject) {

				JsonObject entryJsonObject = (JsonObject) entryJsonElement;

				String entryTypeString = entryJsonObject.has(JXDConstants.JXD_TYPE) ? entryJsonObject.get(JXDConstants.JXD_TYPE).getAsString() : null;
				JXDMapping.JXDTerm entryTypeTerm = entryTypeString == null ? null : mapping.getTerm(entryTypeString);
				if (entryTypeTerm != null && entryTypeTerm.getId() != null) type = entryTypeTerm.getId();
				else if (entryTypeString != null) entryType = makeXDIAddress(entryTypeString, state);
			}

			// context or relation or inner root or literal?

			if (entryType != null && JXDConstants.JXD_GRAPH.equals(entryType.toString()) && entryJsonElement instanceof JsonObject) {

				// inner root

				XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(contextNode);
				ContextNode nestedContextNode = xdiContext.getXdiInnerRoot(entryId, true).getContextNode();

				this.read(nestedContextNode, (JsonObject) entryJsonElement, mapping, state);
			} else if ((entryType != null && JXDConstants.JXD_ID.equals(entryType.toString())) || entryType != null) {

				if (entryJsonElement instanceof JsonPrimitive && ((JsonPrimitive) entryJsonElement).isString()) {

					// one relation

					XDIAddress targetXDIAddress = makeXDIAddress(entryJsonElement.getAsString(), state);

					contextNode.setRelation(entryId, targetXDIAddress);
				} else if (entryJsonElement instanceof JsonArray) {

					// relations and nested context nodes

					for (JsonElement jsonEntryArrayElement : ((JsonArray) entryJsonElement)) {

						if (jsonEntryArrayElement instanceof JsonPrimitive && ((JsonPrimitive) jsonEntryArrayElement).isString()) {

							// relation

							XDIAddress targetXDIAddress = makeXDIAddress(jsonEntryArrayElement.getAsString(), state);

							contextNode.setRelation(entryId, targetXDIAddress);
						} else if (jsonEntryArrayElement instanceof JsonObject) {

							// nested context node

							ContextNode nestedContextNode = contextNode.setDeepContextNode(entryId);

							this.read(nestedContextNode, (JsonObject) jsonEntryArrayElement, mapping, state);
						}
					}
				} else if (entryJsonElement instanceof JsonObject) {

					// nested context node

					ContextNode nestedContextNode = contextNode.setDeepContextNode(entryId);

					this.read(nestedContextNode, (JsonObject) entryJsonElement, mapping, state);
				}
			} else {

				// literal

				Object literalData = AbstractLiteralNode.jsonElementToLiteralData(entryJsonElement);

				LiteralNode literalNode = contextNode.setDeepContextNode(entryId).setLiteralNode(literalData);

				if (entryType != null && ! JXDConstants.JXD_ID.equals(entryType.toString()) && ! JXDConstants.JXD_GRAPH.equals(entryType.toString())) {

					Dictionary.setContextNodeType(literalNode.getContextNode(), entryType);
				}
			}
		}
	}

	private void read(Graph graph, BufferedReader bufferedReader, State state) throws IOException, Xdi2ParseException {

		JsonElement graphJsonElement = gson.getAdapter(JsonObject.class).fromJson(bufferedReader);

		if (graphJsonElement instanceof JsonObject) {

			this.read(graph.getRootContextNode(), (JsonObject) graphJsonElement, null, state);
		} else if (graphJsonElement instanceof JsonArray) {

			for (JsonElement graphEntryJsonElement : ((JsonArray) graphJsonElement)) {

				if (! (graphEntryJsonElement instanceof JsonObject)) throw new Xdi2ParseException("JSON array must only contain objects: " + graphJsonElement);

				this.read(graph.getRootContextNode(), (JsonObject) graphEntryJsonElement, null, state);
			}
		} else {

			throw new Xdi2ParseException("JSON must be an object or array: " + graphJsonElement);
		}
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

	private static XDIAddress makeXDIAddress(String addressString, State state) {

		state.lastString = addressString;
		return XDIAddress.create(addressString);
	}
}
