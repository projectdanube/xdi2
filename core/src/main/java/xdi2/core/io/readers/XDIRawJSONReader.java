package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeMemberOrdered;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityMemberOrdered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.syntax.XDIArc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class XDIRawJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 5574875512968150162L;

	public static final String FORMAT_NAME = "RAW JSON";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIRawJSONReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	private static void readJsonObject(XdiContext<?> xdiContext, JsonObject jsonObject) {

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {

			String key = entry.getKey();
			JsonElement jsonElement = entry.getValue();

			if (jsonElement instanceof JsonObject) {

				XDIArc arc = Dictionary.nativeIdentifierToInstanceAddress(key);

				XdiEntitySingleton xdiEntitySingleton = xdiContext.getXdiEntitySingleton(XdiEntitySingleton.createarc(arc), true);
				readJsonObject(xdiEntitySingleton, (JsonObject) jsonElement);
			} else if (jsonElement instanceof JsonArray) {

				XDIArc arc = Dictionary.nativeIdentifierToInstanceAddress(key);

				readJsonArray(xdiContext, arc, (JsonArray) jsonElement);
			} else if (jsonElement instanceof JsonPrimitive) {

				XDIArc arc = Dictionary.nativeIdentifierToInstanceAddress(key);

				XdiAttributeSingleton xdiAttributeSingleton = xdiContext.getXdiAttributeSingleton(XdiAttributeSingleton.createarc(arc), true);
				XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(true);
				xdiValue.getContextNode().setLiteral(AbstractLiteral.jsonElementToLiteralData(jsonElement));
			}
		}
	}

	private static void readJsonArray(XdiContext<?> xdiContext, XDIArc arc, JsonArray jsonArray) {

		if (arc == null) {

			arc = XDIArc.create("$array");
		}

		long index = 0;

		for (JsonElement jsonElement : jsonArray) {

			if (jsonElement instanceof JsonObject) {

				XdiEntityCollection xdiEntityCollection = xdiContext.getXdiEntityCollection(XdiEntityCollection.createarc(arc), true);

				XdiEntityMemberOrdered xdiEntityMember = xdiEntityCollection.setXdiMemberOrdered(index);
				readJsonObject(xdiEntityMember, (JsonObject) jsonElement);
			} else if (jsonElement instanceof JsonArray) {

				XdiEntityCollection xdiEntityCollection = xdiContext.getXdiEntityCollection(XdiEntityCollection.createarc(arc), true);

				XdiEntityMemberOrdered xdiEntityMember = xdiEntityCollection.setXdiMemberOrdered(index);
				readJsonArray(xdiEntityMember, null, (JsonArray) jsonElement);
			} else {

				XdiAttributeCollection xdiAttributeCollection = xdiContext.getXdiAttributeCollection(XdiAttributeCollection.createarc(arc), true);

				XdiAttributeMemberOrdered xdiAttributeMember = xdiAttributeCollection.setXdiMemberOrdered(index);
				XdiValue xdiValue = xdiAttributeMember.getXdiValue(true);
				xdiValue.getContextNode().setLiteral(AbstractLiteral.jsonElementToLiteralData(jsonElement));
			}

			index++;
		}
	}

	/*	private static XDIArc jsonContentId(JsonElement jsonElement) {

		try {

			String canonicalJson = gson.toJson(jsonElement);
			if (log.isDebugEnabled()) log.debug("canonical JSON: " + canonicalJson);

			MessageDigest digest;

			digest = MessageDigest.getInstance("SHA-512");
			digest.update(canonicalJson.getBytes("UTF-8"));

			String jsonContentId = new String(Base64.encodeBase64URLSafe(digest.digest()), "UTF-8");
			if (log.isDebugEnabled()) log.debug("json_content_id: " + jsonContentId);

			return XDIArc.create(XDIConstants.CS_BANG + jsonContentId);
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}
	}*/

	public void read(Graph graph, JsonObject jsonObject) {

		readJsonObject(XdiAbstractContext.fromContextNode(graph.getRootContextNode(false)), jsonObject);
	}

	public void read(Graph graph, JsonArray jsonArray) {

		readJsonArray(XdiAbstractContext.fromContextNode(graph.getRootContextNode(false)), null, jsonArray);
	}

	private void read(Graph graph, BufferedReader bufferedReader) throws IOException, Xdi2ParseException {

		JsonElement jsonRootElement = gson.getAdapter(JsonObject.class).fromJson(bufferedReader);

		if (jsonRootElement instanceof JsonObject) {

			this.read(graph, (JsonObject) jsonRootElement);
		} else if (jsonRootElement instanceof JsonArray) {

			this.read(graph, (JsonArray) jsonRootElement);
		} else {

			throw new Xdi2ParseException("JSON must be an object or array: " + jsonRootElement);
		}
	}

	@Override
	public Reader read(Graph graph, Reader reader) throws IOException, Xdi2ParseException {

		try {

			this.read(graph, new BufferedReader(reader));
		} catch (Xdi2GraphException ex) {

			throw new Xdi2ParseException("Graph problem: " + ex.getMessage(), ex);
		}

		return reader;
	}
}
