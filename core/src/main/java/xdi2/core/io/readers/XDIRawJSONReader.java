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
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.nodetypes.XdiAbstractSubGraph;
import xdi2.core.features.nodetypes.XdiAttributeClass;
import xdi2.core.features.nodetypes.XdiAttributeInstanceUnordered;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiEntityClass;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiSubGraph;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

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

	public XDIRawJSONReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	private static void readJSONObject(XdiSubGraph xdiSubGraph, JSONObject jsonObject) throws JSONException {

		for (Entry<String, Object> entry : jsonObject.entrySet()) {

			String key = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof JSONObject) {

				XDI3SubSegment arcXri = Dictionary.nativeIdentifierToInstanceXri(key);

				XdiEntitySingleton xdiEntitySingleton = xdiSubGraph.getXdiEntitySingleton(arcXri, true);
				readJSONObject(xdiEntitySingleton, (JSONObject) value);
			} else if (value instanceof JSONArray) {

				XDI3SubSegment arcXri = Dictionary.nativeIdentifierToInstanceXri(key);

				readJSONArray(xdiSubGraph, arcXri, (JSONArray) value);
			} else {

				XDI3SubSegment arcXri = Dictionary.nativeIdentifierToInstanceXri(key);

				XdiAttributeSingleton xdiAttributeSingleton = xdiSubGraph.getXdiAttributeSingleton(arcXri, true);

				XdiValue xdiValue = xdiAttributeSingleton.getXdiValue(true);

				xdiValue.getContextNode().createLiteral(value.toString());
			}
		}
	}

	private static void readJSONArray(XdiSubGraph xdiSubGraph, XDI3SubSegment arcXri, JSONArray jsonArray) throws JSONException {

		for (Object value : jsonArray) {

			XDI3SubSegment jsonContentId = jsonContentId(value);

			if (value instanceof JSONObject) {

				XdiEntityClass xdiEntityClass = xdiSubGraph.getXdiEntityClass(arcXri, true);

				XdiEntityInstanceUnordered xdiEntityInstance = xdiEntityClass.setXdiInstanceUnordered(jsonContentId);
				readJSONObject(xdiEntityInstance, (JSONObject) value);
			} else if (value instanceof JSONArray) {

				throw new RuntimeException("Nested JSON arrays not supported in XDI mapping.");
			} else {

				XdiAttributeClass xdiAttributeClass = xdiSubGraph.getXdiAttributeClass(arcXri, true);

				XdiAttributeInstanceUnordered xdiAttributeInstance = xdiAttributeClass.setXdiInstanceUnordered(jsonContentId);

				XdiValue xdiValue = xdiAttributeInstance.getXdiValue(true);

				xdiValue.getContextNode().createLiteral(value.toString());
			}
		}
	}

	private static XDI3SubSegment jsonContentId(Object object) {

		try {

			String canonicalJson = JSON.toJSONString(object);
			if (log.isDebugEnabled()) log.debug("canonical JSON: " + canonicalJson);

			MessageDigest digest;

			digest = MessageDigest.getInstance("SHA-256");
			digest.update(canonicalJson.getBytes("UTF-8"));

			String jsonContentId = new String(Base64.encodeBase64URLSafe(digest.digest()), "UTF-8");
			if (log.isDebugEnabled()) log.debug("json_content_id: " + jsonContentId);

			return XDI3SubSegment.create(XDI3Constants.CS_BANG + jsonContentId);
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}
	}

	public void read(Graph graph, JSONObject graphObject) throws IOException, Xdi2ParseException, JSONException {

		readJSONObject(XdiAbstractSubGraph.fromContextNode(graph.getRootContextNode()), graphObject);
	}

	private void read(Graph graph, BufferedReader bufferedReader) throws IOException, Xdi2ParseException, JSONException {

		String line;
		StringBuilder graphString = new StringBuilder();

		while ((line = bufferedReader.readLine()) != null) {

			graphString.append(line + "\n");
		}

		this.read(graph, JSON.parseObject(graphString.toString()));
	}

	@Override
	public Reader read(Graph graph, Reader reader) throws IOException, Xdi2ParseException {

		try {

			this.read(graph, new BufferedReader(reader));
		} catch (JSONException ex) {

			throw new Xdi2ParseException("JSON parse error: " + ex.getMessage(), ex);
		} catch (Xdi2GraphException ex) {

			throw new Xdi2ParseException("Graph problem: " + ex.getMessage(), ex);
		}

		return reader;
	}
}
