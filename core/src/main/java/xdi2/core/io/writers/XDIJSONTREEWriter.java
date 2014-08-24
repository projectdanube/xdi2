package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;

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

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writePretty;

	public XDIJSONTREEWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));

		if (log.isTraceEnabled()) log.trace("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writePretty=" + this.writePretty);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write the statements

		JsonObject json = makeJson(graph.getRootContextNode(true), this.writeImplied);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.writePretty) jsonWriter.setIndent("  ");
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

				if (innerContextNode.getXDIArc().equals(XDIConstants.CS_VALUE.toString()) && innerContextNode.containsLiteral()) {

					json.add(XDIConstants.CS_VALUE.toString(), AbstractLiteral.literalDataToJsonElement(innerContextNode.getLiteral().getLiteralData()));
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

			relationJson.add(new JsonPrimitive(relation.getTargetContextNodeXDIAddress().toString()));
		}

		// done

		return json;
	}
}
