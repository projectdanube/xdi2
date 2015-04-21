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
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.parser.ParserException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class XDIJSONQuadReader extends AbstractXDIReader {

	private static final long serialVersionUID = -141407281637273449L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONQuadReader.class);

	public static final String FORMAT_NAME = "XDI/JSON/QUAD";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJSONQuadReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	public void read(ContextNode contextNode, JsonObject jsonGraphObject, State state) throws IOException, Xdi2ParseException {

		for (Entry<String, JsonElement> entry : jsonGraphObject.entrySet()) {

			String key = entry.getKey();
			JsonElement jsonEntryElement = entry.getValue();

			if (key.equals("//")) {

				if (! (jsonEntryElement instanceof JsonArray)) throw new Xdi2ParseException("For key " + key + ", the value must be a JSON array (list of subcontexts): " + jsonEntryElement);

				JsonArray jsonEntryArray = (JsonArray) jsonEntryElement;

				// add context nodes

				for (JsonElement jsonEntryArrayElement : jsonEntryArray) {

					if (! (jsonEntryArrayElement instanceof JsonPrimitive) || ! ((JsonPrimitive) jsonEntryArrayElement).isString()) throw new Xdi2ParseException("Under key " + key + ", the element of the JSON array must be a string (subcontext): " + jsonEntryArrayElement);

					XDIArc XDIarc = makeXDIArc(((JsonPrimitive) jsonEntryArrayElement).getAsString(), state);

					ContextNode subContextNode = contextNode.setContextNode(XDIarc);
					if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXDIAddress() + ": Set context node " + subContextNode.getXDIArc() + " --> " + subContextNode.getXDIAddress());
				}
			} else if (key.equals("&")) {

				Object literalData = AbstractLiteralNode.jsonElementToLiteralData(jsonEntryElement);

				// add literal node

				LiteralNode literalNode = contextNode.setLiteralNode(literalData);
				if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXDIAddress() + ": Set literal --> " + literalNode.getLiteralData());
			} else if (key.startsWith("/")) {

				if (! (jsonEntryElement instanceof JsonArray)) throw new Xdi2ParseException("For key " + key + ", the value must be a JSON array (list of relations): " + jsonEntryElement);

				XDIAddress XDIaddress = makeXDIAddress(key.substring(1), state);
				JsonArray jsonEntryArray = (JsonArray) jsonEntryElement;

				// add relations

				for (JsonElement jsonEntryArrayElement : jsonEntryArray) {

					if (! (jsonEntryArrayElement instanceof JsonPrimitive) || ! ((JsonPrimitive) jsonEntryArrayElement).isString()) throw new Xdi2ParseException("Under key " + key + ", the element of the JSON array must be a string (relation): " + jsonEntryArrayElement);

					XDIAddress targetXDIAddress = makeXDIAddress(((JsonPrimitive) jsonEntryArrayElement).getAsString(), state);

					Relation relation = contextNode.setRelation(XDIaddress, targetXDIAddress);
					if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXDIAddress() + ": Set relation " + relation.getXDIAddress() + " --> " + relation.getTargetXDIAddress());
				}
			} else {

				if (! (jsonEntryElement instanceof JsonObject)) throw new Xdi2ParseException("For key " + key + ", the value must be a JSON object (context): " + jsonEntryElement);

				XDIAddress XDIaddress = makeXDIAddress(key, state);
				JsonObject jsonEntryObject = (JsonObject) jsonEntryElement;

				// add context node

				ContextNode subContextNode = contextNode.setDeepContextNode(XDIaddress);
				if (log.isTraceEnabled()) log.trace("Under " + contextNode.getXDIAddress() + ": Set context node(s) " + subContextNode.getXDIArc() + " --> " + subContextNode.getXDIAddress());

				this.read(subContextNode, jsonEntryObject, state);
			}
		}
	}

	private void read(Graph graph, BufferedReader bufferedReader, State state) throws IOException, Xdi2ParseException {

		JsonElement jsonGraphElement = gson.getAdapter(JsonObject.class).fromJson(bufferedReader);

		if (! (jsonGraphElement instanceof JsonObject)) throw new Xdi2ParseException("JSON must be an object: " + jsonGraphElement);

		this.read(graph.getRootContextNode(), (JsonObject) jsonGraphElement, state);
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

	private static XDIArc makeXDIArc(String arcString, State state) {

		state.lastString = arcString;
		return XDIArc.create(arcString);
	}
}
