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
import xdi2.core.Literal;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.contextfunctions.XdiAttribute;
import xdi2.core.features.contextfunctions.XdiAttributeInstance;
import xdi2.core.features.contextfunctions.XdiAttributeSingleton;
import xdi2.core.features.contextfunctions.XdiCollection;
import xdi2.core.features.contextfunctions.XdiEntityInstance;
import xdi2.core.features.contextfunctions.XdiEntitySingleton;
import xdi2.core.features.contextfunctions.XdiSubGraph;
import xdi2.core.features.contextfunctions.XdiValue;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.features.dictionary.Dictionary;
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

				XdiEntitySingleton innerXdiEntitySingleton = xdiSubGraph.getXdiEntitySingleton(arcXri, true);
				readJSONObject(innerXdiEntitySingleton, (JSONObject) value);
			} else if (value instanceof JSONArray) {

				XDI3SubSegment arcXri = Dictionary.nativeIdentifierToInstanceXri(key);

				XdiCollection innerXdiCollection = xdiSubGraph.getXdiCollection(arcXri, true);
				readJSONArray(innerXdiCollection, (JSONArray) value);
			} else {

				XDI3SubSegment arcXri = Dictionary.nativeIdentifierToInstanceXri(key);

				XdiAttributeSingleton innerXdiAttributeSingleton = xdiSubGraph.getXdiAttributeSingleton(arcXri, true);
				createLiteral(innerXdiAttributeSingleton, value);
			}
		}
	}

	private static void readJSONArray(XdiCollection xdiCollection, JSONArray jsonArray) throws JSONException {

		for (Object value : jsonArray) {

			XDI3SubSegment jsonContentId = jsonContentId(value);

			if (value instanceof JSONObject) {

				XdiEntityInstance innerXdiEntityMember = xdiCollection.getXdiEntityMember(jsonContentId, true);
				readJSONObject(innerXdiEntityMember, (JSONObject) value);
			} else if (value instanceof JSONArray) {

				XdiCollection innerXdiCollection = xdiCollection.getXdiCollection(jsonContentId, true);
				readJSONArray(innerXdiCollection, (JSONArray) value);
			} else {

				XDI3SubSegment arcXri = XdiValue.createValueArcXri(jsonContentId);

				XdiAttributeInstance innerXdiAttributeMember = xdiCollection.getXdiAttributeMember(arcXri, true);
				createLiteral(innerXdiAttributeMember, value);
			}
		}
	}

	private static void createLiteral(XdiAttribute xdiAttribute, Object value) {

		if (value instanceof String) {

			xdiAttribute.getContextNode().createLiteral(value.toString());
		} else if (value instanceof Number) {

			Literal literal = xdiAttribute.getContextNode().createLiteral(value.toString());
			DataTypes.setLiteralDataType(literal, XRI_DATATYPE_JSON_NUMBER);
		} else if (value instanceof Boolean) {

			Literal literal = xdiAttribute.getContextNode().createLiteral(value.toString());

			if (value.equals(Boolean.TRUE)) DataTypes.setLiteralDataType(literal, XRI_DATATYPE_JSON_TRUE);
			if (value.equals(Boolean.FALSE)) DataTypes.setLiteralDataType(literal, XRI_DATATYPE_JSON_FALSE);
		} else if (value == null) {

			Literal literal = xdiAttribute.getContextNode().createLiteral("null");
			DataTypes.setLiteralDataType(literal, XRI_DATATYPE_JSON_NULL);
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

		readJSONObject(XdiSubGraph.fromContextNode(graph.getRootContextNode()), graphObject);
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
