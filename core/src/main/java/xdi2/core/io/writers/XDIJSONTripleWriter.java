package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAbstractRoot;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.XDIAddressUtil;

public class XDIJSONTripleWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -1182712339753412616L;

	public static final String FORMAT_NAME = "XDI/JSON/TRIPLE";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJSONTripleWriter(Properties parameters) {

		super(parameters);
	}

	private void writeInternal(Graph graph, JsonObject jsonObject) throws IOException {

		// start with the common root node

		this.putRootIntoJsonObject(graph.getRootContextNode(), jsonObject);
	}

	@SuppressWarnings("resource")
	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		JsonObject jsonObject = new JsonObject();

		this.writeInternal(graph, jsonObject);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.isWritePretty()) jsonWriter.setIndent("  ");
		gson.toJson(jsonObject, jsonWriter);
		jsonWriter.flush();
		writer.flush();

		return writer;
	}

	private void putRootIntoJsonObject(ContextNode rootContextNode, JsonObject outerJsonObject) throws IOException {

		JsonObject rootJsonObject;

		// set up root

		if (XdiCommonRoot.isValid(rootContextNode)) {

			rootJsonObject = outerJsonObject;
		} else if (XdiAbstractRoot.isValid(rootContextNode)) {

			rootJsonObject = new JsonObject();
			outerJsonObject.add(rootContextNode.getXDIAddress().toString(), rootJsonObject);
		} else {

			throw new Xdi2RuntimeException("Unexpected root context node: " + rootContextNode);
		}

		// set up root entity

		JsonObject rootEntityJsonObject = new JsonObject();
		rootJsonObject.add("", rootEntityJsonObject);

		// context nodes

		for (ContextNode contextNode : rootContextNode.getContextNodes()) {

			if (XdiAbstractRoot.isValid(contextNode)) {

				this.putRootIntoJsonObject(contextNode, outerJsonObject);
				continue;
			}

			if (XdiAbstractEntity.isValid(contextNode) || XdiEntityCollection.isValid(contextNode)) {

				this.putEntityIntoRootJsonObject(contextNode, rootJsonObject, rootContextNode.getXDIAddress());
				continue;
			}

			if (XdiAbstractAttribute.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode)) {

				this.putAttributeIntoEntityJsonObject(contextNode, rootEntityJsonObject, rootContextNode.getXDIAddress());
				continue;
			}

			throw new Xdi2RuntimeException("Unexpected context node: " + contextNode + " on root context node: " + rootContextNode);
		}

		// relations

		for (Relation relation : rootContextNode.getRelations()) {

			this.putRelationIntoJsonObject(relation, rootEntityJsonObject);
		}

		// literal node

		if (rootContextNode.containsLiteralNode()) {

			throw new Xdi2RuntimeException("Unexpected literal node on root context node: " + rootContextNode);
		}

		// finish root

		if (rootJsonObject != outerJsonObject) {

			if (rootJsonObject.entrySet().isEmpty() && ! rootContextNode.isEmpty()) {

				if (! this.isWriteImplied()) outerJsonObject.remove(rootContextNode.getXDIAddress().toString());
			}
		}

		// finish root entity

		if (rootEntityJsonObject.entrySet().isEmpty()) {

			if (! this.isWriteImplied()) rootJsonObject.remove("");
		}
	}

	private void putEntityIntoRootJsonObject(ContextNode entityContextNode, JsonObject rootJsonObject, XDIAddress parentXDIAddress) throws IOException {

		XDIAddress XDIaddress = entityContextNode.getXDIAddress();
		XDIAddress localXDIAddress = XDIAddressUtil.localXDIAddress(XDIaddress, - parentXDIAddress.getNumXDIArcs());

		// set up entity

		JsonObject entityJsonObject = new JsonObject();
		rootJsonObject.add(localXDIAddress.toString(), entityJsonObject);

		// context nodes

		for (ContextNode contextNode : entityContextNode.getContextNodes()) {

			if (XdiAbstractEntity.isValid(contextNode) || XdiEntityCollection.isValid(contextNode)) {

				this.putEntityIntoRootJsonObject(contextNode, rootJsonObject, parentXDIAddress);
				continue;
			}

			if (XdiAbstractAttribute.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode)) {

				this.putAttributeIntoEntityJsonObject(contextNode, entityJsonObject, XDIaddress);
				continue;
			}

			throw new Xdi2RuntimeException("Unexpected context node: " + contextNode + " on entity context node: " + entityContextNode);
		}

		// relations

		for (Relation relation : entityContextNode.getRelations()) {

			this.putRelationIntoJsonObject(relation, entityJsonObject);
		}

		// literal node

		if (entityContextNode.containsLiteralNode()) {

			throw new Xdi2RuntimeException("Unexpected literal node on entity context node: " + entityContextNode);
		}

		// finish entity

		if (entityJsonObject.entrySet().isEmpty() && ! entityContextNode.isEmpty()) {

			if (! this.isWriteImplied()) rootJsonObject.remove(localXDIAddress.toString());
		}
	}

	private void putAttributeIntoEntityJsonObject(ContextNode attributeContextNode, JsonObject entityJsonObject, XDIAddress parentXDIAddress) throws IOException {

		XDIAddress XDIaddress = attributeContextNode.getXDIAddress();
		XDIAddress localXDIAddress = XDIAddressUtil.localXDIAddress(XDIaddress, - parentXDIAddress.getNumXDIArcs());

		// set up attribute

		JsonObject attributeJsonObject = new JsonObject();
		entityJsonObject.add(localXDIAddress.toString(), attributeJsonObject);

		// context nodes

		for (ContextNode contextNode : attributeContextNode.getContextNodes()) {

			if (XdiAbstractAttribute.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode)) {

				this.putAttributeIntoEntityJsonObject(contextNode, entityJsonObject, parentXDIAddress);
				continue;
			}

			throw new Xdi2RuntimeException("Unexpected context node: " + contextNode + " on attribute context node: " + attributeContextNode);
		}

		// relations

		for (Relation relation : attributeContextNode.getRelations()) {

			this.putRelationIntoJsonObject(relation, attributeJsonObject);
		}

		// literal node

		if (attributeContextNode.containsLiteralNode()) {

			LiteralNode literalNode = attributeContextNode.getLiteralNode();

			this.putLiteralNodeIntoAttributeJsonObject(literalNode, attributeJsonObject, XDIaddress);
		}

		// finish attribute

		if (attributeJsonObject.entrySet().isEmpty() && ! attributeContextNode.isEmpty()) {

			if (! this.isWriteImplied()) entityJsonObject.remove(localXDIAddress.toString());
		}
	}

	private void putLiteralNodeIntoAttributeJsonObject(LiteralNode literalNode, JsonObject attributeJsonObject, XDIAddress parentXDIAddress) {

		XDIAddress XDIaddress = literalNode.getXDIAddress();
		XDIAddress localXDIAddress = XDIAddressUtil.localXDIAddress(XDIaddress, - parentXDIAddress.getNumXDIArcs());

		JsonElement literalJsonElement = AbstractLiteralNode.literalDataToJsonElement(literalNode.getLiteralData());
		attributeJsonObject.add(localXDIAddress.toString(), literalJsonElement);
	}

	private void putRelationIntoJsonObject(Relation relation, JsonObject jsonObject) {

		JsonArray relationJsonArray = jsonObject.getAsJsonArray(relation.getXDIAddress().toString());

		if (relationJsonArray == null) {

			relationJsonArray = new JsonArray();
			jsonObject.add(relation.getXDIAddress().toString(), relationJsonArray);
		}

		relationJsonArray.add(new JsonPrimitive(relation.getTargetXDIAddress().toString()));
	}
}
