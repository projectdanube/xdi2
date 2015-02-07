package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAbstractRoot;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class XDIJSONQuadWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = 1077789049204778292L;

	public static final String FORMAT_NAME = "XDI/JSON/QUAD";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJSONQuadWriter(Properties parameters) {

		super(parameters);
	}

	private void writeInternal(Graph graph, JsonObject jsonObject) throws IOException {

		// start with the common root node

		putRootIntoJsonObject(graph.getRootContextNode(), jsonObject);
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

	private static void putRootIntoJsonObject(ContextNode rootContextNode, JsonObject outerJsonObject) throws IOException {

		JsonObject graphJsonObject;
		JsonObject entityJsonObject;

		if (XdiCommonRoot.isValid(rootContextNode)) {

			graphJsonObject = outerJsonObject;
		} else if (XdiAbstractRoot.isValid(rootContextNode)) {

			graphJsonObject = new JsonObject();
			outerJsonObject.add(rootContextNode.getXDIAddress().toString(), graphJsonObject);
		} else {

			throw new Xdi2RuntimeException("Unexpected root context node: " + rootContextNode);
		}

		entityJsonObject = new JsonObject();
		graphJsonObject.add("", entityJsonObject);

		// context nodes

		for (ContextNode contextNode : rootContextNode.getContextNodes()) {

			if (XdiAbstractRoot.isValid(contextNode)) {

				putRootIntoJsonObject(contextNode, outerJsonObject);
				continue;
			}

			if (XdiAbstractEntity.isValid(contextNode) || XdiEntityCollection.isValid(contextNode)) {

				putEntityIntoGraphJsonObject(contextNode, graphJsonObject);
				continue;
			}

			if (XdiAbstractAttribute.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode)) {

				putAttributeIntoEntityJsonObject(contextNode, entityJsonObject);
				continue;
			}

			throw new Xdi2RuntimeException("Unexpected context node: " + contextNode + " on root context node: " + rootContextNode);
		}

		// relations

		for (Relation relation : rootContextNode.getRelations()) {

			putRelationIntoEntityJsonObject(relation, entityJsonObject);
		}

		// literal

		if (rootContextNode.containsLiteral()) {

			throw new Xdi2RuntimeException("Unexpected literal on root context node: " + rootContextNode);
		}
	}

	private static void putEntityIntoGraphJsonObject(ContextNode entityContextNode, JsonObject graphJsonObject) throws IOException {

		JsonObject entityJsonObject = new JsonObject();
		graphJsonObject.add(entityContextNode.getXDIAddress().toString(), entityJsonObject);

		// context nodes

		for (ContextNode contextNode : entityContextNode.getContextNodes()) {

			if (XdiAbstractEntity.isValid(contextNode) || XdiEntityCollection.isValid(contextNode)) {

				putEntityIntoGraphJsonObject(contextNode, graphJsonObject);
				continue;
			}

			if (XdiAbstractAttribute.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode)) {

				putAttributeIntoEntityJsonObject(contextNode, entityJsonObject);
				continue;
			}

			throw new Xdi2RuntimeException("Unexpected context node: " + contextNode + " on entity context node: " + entityContextNode);
		}

		// relations

		for (Relation relation : entityContextNode.getRelations()) {

			putRelationIntoEntityJsonObject(relation, entityJsonObject);
		}

		// literal

		if (entityContextNode.containsLiteral()) {

			throw new Xdi2RuntimeException("Unexpected literal on entity context node: " + entityContextNode);
		}
	}

	private static void putAttributeIntoEntityJsonObject(ContextNode attributeContextNode, JsonObject entityJsonObject) throws IOException {

		JsonObject attributeJsonObject = new JsonObject();
		attributeJsonObject.add(attributeContextNode.getXDIAddress().toString(), attributeJsonObject);

		// context nodes

		for (ContextNode contextNode : attributeContextNode.getContextNodes()) {

			if (XdiAbstractAttribute.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode)) {

				putAttributeIntoEntityJsonObject(contextNode, attributeJsonObject);
				continue;
			}

			if (XdiValue.isValid(contextNode)) {

				putValueIntoAttributeJsonObject(contextNode, attributeJsonObject);
				continue;
			}

			throw new Xdi2RuntimeException("Unexpected context node: " + contextNode + " on attribute context node: " + attributeContextNode);
		}

		// relations

		for (Relation relation : attributeContextNode.getRelations()) {

			putRelationIntoAttributeJsonObject(relation, attributeJsonObject);
		}

		// literal

		if (attributeContextNode.containsLiteral()) {

			throw new Xdi2RuntimeException("Unexpected literal on attribute context node: " + attributeContextNode);
		}
	}

	private static void putValueIntoAttributeJsonObject(ContextNode valueContextNode, JsonObject attributeJsonObject) {

		JsonElement literalJsonElement;

		if (! valueContextNode.containsLiteral()) {

			throw new Xdi2RuntimeException("No literal on value context node: " + valueContextNode);
		}

		literalJsonElement = AbstractLiteral.literalDataToJsonElement(valueContextNode.getLiteral().getLiteralData());

		attributeJsonObject.add(XDIConstants.XDI_ARC_VALUE.toString(), literalJsonElement);
	}

	private static void putRelationIntoEntityJsonObject(Relation relation, JsonObject entityJsonObject) {

		JsonArray relationJsonArray = entityJsonObject.getAsJsonArray(relation.getXDIAddress().toString());

		if (relationJsonArray == null) {

			relationJsonArray = new JsonArray();
			entityJsonObject.add(relation.getXDIAddress().toString(), relationJsonArray);
		}

		relationJsonArray.add(new JsonPrimitive(relation.getTargetContextNodeXDIAddress().toString()));
	}

	private static void putRelationIntoAttributeJsonObject(Relation relation, JsonObject attributeJsonObject) {

		JsonArray relationJsonArray = attributeJsonObject.getAsJsonArray(relation.getXDIAddress().toString());

		if (relationJsonArray == null) {

			relationJsonArray = new JsonArray();
			attributeJsonObject.add(relation.getXDIAddress().toString(), relationJsonArray);
		}

		relationJsonArray.add(new JsonPrimitive(relation.getTargetContextNodeXDIAddress().toString()));
	}
}
