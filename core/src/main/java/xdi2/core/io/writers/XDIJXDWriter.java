package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.util.JXDConstants;
import xdi2.core.util.CopyUtil;

public class XDIJXDWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = 1077789049204778292L;

	public static final String FORMAT_NAME = "JXD";
	public static final String FILE_EXTENSION = "jxd";
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJXDWriter(Properties parameters) {

		super(parameters);
	}

	private void writeInternal(Graph graph, JsonArray jsonArray) throws IOException {

		// start with the common root node

		for (ContextNode contextNode : graph.getRootContextNode().getContextNodes()) {

			JsonObject jsonObject = new JsonObject();
			jsonArray.add(jsonObject);

			this.putContextNodeIntoJsonObject(contextNode, jsonObject, true);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write ordered?

		Graph orderedGraph = null;

		if (this.isWriteOrdered()) {

			MemoryGraphFactory memoryGraphFactory = new MemoryGraphFactory();
			memoryGraphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ALPHA);
			orderedGraph = memoryGraphFactory.openGraph();
			CopyUtil.copyGraph(graph, orderedGraph, null);

			graph = orderedGraph;
		}

		// write

		JsonArray jsonArray  = new JsonArray();

		this.writeInternal(graph, jsonArray);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.isWritePretty()) jsonWriter.setIndent("  ");
		gson.toJson(jsonArray , jsonWriter);
		jsonWriter.flush();
		writer.flush();

		return writer;
	}

	private void putContextNodeIntoJsonObject(ContextNode contextNode, JsonObject jsonObject, boolean first) throws IOException {

		// set up context node

		if (first) {

			jsonObject.addProperty(JXDConstants.JXD_ID, contextNode.getXDIArc().toString());
		}

		// context nodes

		for (ContextNode childContextNode : contextNode.getContextNodes()) {

			JsonObject childJsonObject = new JsonObject();
			jsonObject.add(contextNode.getXDIArc().toString(), childJsonObject);

			this.putContextNodeIntoJsonObject(childContextNode, childJsonObject, false);
		}

		// relations

		for (Relation relation : contextNode.getRelations()) {

			this.putRelationIntoJsonObject(relation, jsonObject);
		}

		// literal

		if (contextNode.containsLiteralNode()) {

			this.putLiteralNodeIntoJsonObject(contextNode.getLiteralNode(), jsonObject);
		}
	}

	/*	private void putLiteralNodeIntoJsonObject(LiteralNode literalNode, JsonObject attributeJsonObject, XDIAddress parentXDIAddress) {

		XDIAddress XDIaddress = literalNode.getXDIAddress();
		XDIAddress localXDIAddress = XDIAddressUtil.localXDIAddress(XDIaddress, - parentXDIAddress.getNumXDIArcs());

		JsonElement literalJsonElement = AbstractLiteralNode.literalDataToJsonElement(literalNode.getLiteralData());
		attributeJsonObject.add(localXDIAddress.toString(), literalJsonElement);
	}*/

	private void putRelationIntoJsonObject(Relation relation, JsonObject jsonObject) {

		if (! this.isWriteImplied() && relation.getStatement().isImplied()) return;

		JsonArray relationJsonArray = jsonObject.getAsJsonArray(relation.getXDIAddress().toString());

		if (relationJsonArray == null) {

			relationJsonArray = new JsonArray();
			jsonObject.add(relation.getXDIAddress().toString(), relationJsonArray);
		}

		relationJsonArray.add(new JsonPrimitive(relation.getTargetXDIAddress().toString()));
	}

	private void putLiteralNodeIntoJsonObject(LiteralNode literalNode, JsonObject jsonObject) {

		jsonObject.add(literalNode.getContextNode().getXDIArc().toString(), AbstractLiteralNode.literalDataToJsonElement(literalNode.getLiteralData()));
	}
}
