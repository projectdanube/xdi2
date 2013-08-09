package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.XDI3SubSegment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class XDIJSONTREEWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -7518609157052712790L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONTREEWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON/TREE";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writeInner;
	private boolean writePretty;

	public XDIJSONTREEWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writeInner = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_INNER, XDIWriterRegistry.DEFAULT_INNER));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));

		if (log.isTraceEnabled()) log.trace("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writeInner=" + this.writeInner + ", writePretty=" + this.writePretty);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write the statements

		JsonObject json = makeJson(graph.getRootContextNode(), this.writeImplied, this.writeInner);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.writePretty) jsonWriter.setIndent("  ");
		gson.toJson(json, jsonWriter);
		writer.flush();

		return writer;
	}

	/*
	 * Helper methods
	 */

	private static JsonObject makeJson(ContextNode contextNode, boolean writeImplied, boolean writeInner) {

		Map<XdiInnerRoot, JsonObject> xdiInnerRootJsons = new HashMap<XdiInnerRoot, JsonObject> ();

		JsonObject json = makeJson(contextNode, writeImplied, writeInner, xdiInnerRootJsons);

		if (writeInner) {

			for (Entry<XdiInnerRoot, JsonObject> entry : xdiInnerRootJsons.entrySet()) {

				XdiInnerRoot xdiInnerRoot = entry.getKey();
				JsonObject xdiInnerRootJson = entry.getValue();

				JsonObject tempJsonObject = json;
				JsonArray tempJsonArray;

				for (XDI3SubSegment subSegment : xdiInnerRoot.getSubjectOfInnerRoot().getSubSegments()) tempJsonObject = setJsonObject(tempJsonObject, subSegment.toString());

				tempJsonObject = setJsonObject(tempJsonObject, "/");
				tempJsonArray = setJsonArray(tempJsonObject, xdiInnerRoot.getPredicateOfInnerRoot().toString());

				tempJsonArray.add(xdiInnerRootJson);
			}
		}

		return json;
	}

	private static JsonObject setJsonObject(JsonObject json, String key) {

		JsonObject innerJson = json.getAsJsonObject(key);
		if (innerJson != null) return innerJson;

		innerJson = new JsonObject();
		json.add(key, innerJson);

		return innerJson;
	}

	private static JsonArray setJsonArray(JsonObject json, String key) {

		JsonArray innerJson = json.getAsJsonArray(key);
		if (innerJson != null) return innerJson;

		innerJson = new JsonArray();
		json.add(key, innerJson);

		return innerJson;
	}

	private static JsonObject makeJson(ContextNode contextNode, boolean writeImplied, boolean writeInner, Map<XdiInnerRoot, JsonObject> xdiInnerRootJsons) {

		JsonObject json = new JsonObject();

		// context nodes

		for (ContextNode innerContextNode : contextNode.getContextNodes()) {

			if (! writeImplied && innerContextNode.getStatement().isImplied() && innerContextNode.isEmpty()) continue;

			if (writeInner && XdiInnerRoot.isValid(innerContextNode)) {

				xdiInnerRootJsons.put(XdiInnerRoot.fromContextNode(innerContextNode), makeJson(innerContextNode, writeImplied, writeInner));

				continue;
			}

			if (json.get(innerContextNode.getArcXri().toString()) == null) {

				if (innerContextNode.getArcXri().equals(XDIConstants.CS_VALUE.toString()) && innerContextNode.containsLiteral()) {

					json.add(XDIConstants.CS_VALUE.toString(), literalDataToJsonPrimitive(innerContextNode.getLiteral().getLiteralData()));
				} else {

					json.add(innerContextNode.getArcXri().toString(), makeJson(innerContextNode, writeImplied, writeInner));
				}
			}
		}

		// relations

		for (Relation relation : contextNode.getRelations()) {

			if (! writeImplied && relation.getStatement().isImplied()) continue;

			JsonObject relationsJson = json.getAsJsonObject("/");
			if (relationsJson == null) { relationsJson = new JsonObject(); json.add("/", relationsJson); }

			JsonArray relationJson = relationsJson.getAsJsonArray(relation.getArcXri().toString());
			if (relationJson == null) { relationJson = new JsonArray(); relationsJson.add(relation.getArcXri().toString(), relationJson); }

			relationJson.add(new JsonPrimitive(relation.getTargetContextNodeXri().toString()));
		}

		// done

		return json;
	}

	private static JsonPrimitive literalDataToJsonPrimitive(Object literalData) {

		if (literalData instanceof String) {

			return new JsonPrimitive((String) literalData);
		} else if (literalData instanceof Number) {

			return new JsonPrimitive((Number) literalData);
		} else if (literalData instanceof Boolean) {

			return new JsonPrimitive((Boolean) literalData);
		} else {

			throw new IllegalArgumentException("Invalid literal data: " + literalData.getClass().getSimpleName());
		}
	}
}
