package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class XDIJSONTREEWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -7518609157052712790L;

	public static final String FORMAT_NAME = "XDI/JSON/TREE";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJSONTREEWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write the statements

		JsonObject json = makeJson(graph.getRootContextNode(true), this.isWriteImplied());

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.isWritePretty()) jsonWriter.setIndent("  ");
		gson.toJson(json, jsonWriter);
		jsonWriter.flush();
		jsonWriter.close();
		writer.flush();

		return writer;
	}

	/*
	 * Helper methods
	 */

	private static JsonObject makeJson(ContextNode contextNode, boolean writeImplied) {

		Map<XdiInnerRoot, JsonObject> xdiInnerRootJsons = new HashMap<XdiInnerRoot, JsonObject> ();

		JsonObject json = makeJson(contextNode, writeImplied, xdiInnerRootJsons);

		return json;
	}

	private static JsonObject makeJson(ContextNode contextNode, boolean writeImplied, Map<XdiInnerRoot, JsonObject> xdiInnerRootJsons) {

		JsonObject json = new JsonObject();

		// context nodes

		for (ContextNode innerContextNode : contextNode.getContextNodes()) {

			if (! writeImplied && innerContextNode.getStatement().isImplied() && innerContextNode.isEmpty()) continue;

			if (json.get(innerContextNode.getXDIArc().toString()) == null) {

				if (innerContextNode.getXDIArc().equals(XDIConstants.CS_LITERAL.toString()) && innerContextNode.containsLiteralNode()) {

					json.add(XDIConstants.CS_LITERAL.toString(), AbstractLiteralNode.literalDataToJsonElement(innerContextNode.getLiteralNode().getLiteralData()));
				} else {

					json.add(innerContextNode.getXDIArc().toString(), makeJson(innerContextNode, writeImplied));
				}
			}
		}

		// relations

		for (Relation relation : contextNode.getRelations()) {

			if (! writeImplied && relation.getStatement().isImplied()) continue;

			JsonObject relationsJson = json.getAsJsonObject("/");
			if (relationsJson == null) { relationsJson = new JsonObject(); json.add("/", relationsJson); }

			JsonArray relationJson = relationsJson.getAsJsonArray(relation.getXDIAddress().toString());
			if (relationJson == null) { relationJson = new JsonArray(); relationsJson.add(relation.getXDIAddress().toString(), relationJson); }

			relationJson.add(new JsonPrimitive(relation.getTargetXDIAddress().toString()));
		}

		// done

		return json;
	}
}
