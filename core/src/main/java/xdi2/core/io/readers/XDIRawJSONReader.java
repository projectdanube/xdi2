package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.security.MessageDigest;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeClass;
import xdi2.core.features.nodetypes.XdiAttributeInstanceUnordered;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiEntityClass;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class XDIRawJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 5574875512968150162L;

	private static final Logger log = LoggerFactory.getLogger(XDIRawJSONReader.class);

	public static final String FORMAT_NAME = "RAW JSON";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	public static final XDI3Segment XRI_DATATYPE_JSON_NUMBER = XDI3Segment.create("+$json$number");
	public static final XDI3Segment XRI_DATATYPE_JSON_TRUE = XDI3Segment.create("+$json$true");
	public static final XDI3Segment XRI_DATATYPE_JSON_FALSE = XDI3Segment.create("+$json$false");
	public static final XDI3Segment XRI_DATATYPE_JSON_NULL = XDI3Segment.create("+$json$null");

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public XDIRawJSONReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	private static void readJsonObject(XdiContext xdiContext, JsonObject jsonObject) {

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {

			String key = entry.getKey();
			JsonElement jsonElement = entry.getValue();

			if (jsonElement instanceof JsonObject) {

				XDI3SubSegment arcXri = Dictionary.nativeIdentifierToInstanceXri(key);

				XdiEntitySingleton xdiEntitySingleton = xdiContext.getXdiEntitySingleton(arcXri, true);
				readJsonObject(xdiEntitySingleton, (JsonObject) jsonElement);
			} else if (jsonElement instanceof JsonArray) {

				XDI3SubSegment arcXri = Dictionary.nativeIdentifierToInstanceXri(key);

				readJsonArray(xdiContext, arcXri, (JsonArray) jsonElement);
			} else if (jsonElement instanceof JsonPrimitive && ((JsonPrimitive) jsonElement).isString()){

				XDI3SubSegment arcXri = Dictionary.nativeIdentifierToInstanceXri(key);

				XdiAttributeSingleton xdiAttributeSingleton = xdiContext.getXdiAttributeSingleton(arcXri, true);

				XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(true);

				xdiValue.getContextNode().createLiteral(((JsonPrimitive) jsonElement).getAsString());
			}
		}
	}

	private static void readJsonArray(XdiContext xdiContext, XDI3SubSegment arcXri, JsonArray jsonArray) {

		for (JsonElement jsonElement : jsonArray) {

			XDI3SubSegment jsonContentId = jsonContentId(jsonElement);

			if (jsonElement instanceof JsonObject) {

				XdiEntityClass xdiEntityClass = xdiContext.getXdiEntityClass(arcXri, true);

				XdiEntityInstanceUnordered xdiEntityInstance = xdiEntityClass.setXdiInstanceUnordered(jsonContentId);
				readJsonObject(xdiEntityInstance, (JsonObject) jsonElement);
			} else if (jsonElement instanceof JsonArray) {

				throw new RuntimeException("Nested JSON arrays not supported in XDI mapping.");
			} else {

				XdiAttributeClass xdiAttributeClass = xdiContext.getXdiAttributeClass(arcXri, true);

				XdiAttributeInstanceUnordered xdiAttributeInstance = xdiAttributeClass.setXdiInstanceUnordered(jsonContentId);

				XdiValue xdiValue = xdiAttributeInstance.getXdiValue(true);

				xdiValue.getContextNode().createLiteral(jsonElement.toString());
			}
		}
	}

	private static XDI3SubSegment jsonContentId(JsonElement jsonElement) {

		try {

			String canonicalJson = gson.toJson(jsonElement);
			if (log.isDebugEnabled()) log.debug("canonical JSON: " + canonicalJson);

			MessageDigest digest;

			digest = MessageDigest.getInstance("SHA-256");
			digest.update(canonicalJson.getBytes("UTF-8"));

			String jsonContentId = new String(Base64.encodeBase64URLSafe(digest.digest()), "UTF-8");
			if (log.isDebugEnabled()) log.debug("json_content_id: " + jsonContentId);

			return XDI3SubSegment.create(XDIConstants.CS_BANG + jsonContentId);
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}
	}

	public void read(Graph graph, JsonObject graphObject) throws IOException, Xdi2ParseException {

		readJsonObject(XdiAbstractContext.fromContextNode(graph.getRootContextNode()), graphObject);
	}

	private void read(Graph graph, BufferedReader bufferedReader) throws IOException, Xdi2ParseException {

		JsonElement jsonRootElement = gson.getAdapter(JsonObject.class).fromJson(bufferedReader);

		if (! (jsonRootElement instanceof JsonObject)) throw new Xdi2ParseException("JSON must be an object: " + jsonRootElement);

		this.read(graph, (JsonObject) jsonRootElement);
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
