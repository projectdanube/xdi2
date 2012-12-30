package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3SubSegment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class XDIRawJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 5574875512968150162L;

	public static final String FORMAT_NAME = "RAW JSON";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	public static final XDI3Segment XRI_DATATYPE_JSON_NUMBER = new XDI3Segment("+$json$number");
	public static final XDI3Segment XRI_DATATYPE_JSON_TRUE = new XDI3Segment("+$json$true");
	public static final XDI3Segment XRI_DATATYPE_JSON_FALSE = new XDI3Segment("+$json$false");
	public static final XDI3Segment XRI_DATATYPE_JSON_NULL = new XDI3Segment("+$json$null");

	public XDIRawJSONReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	private static void readJSONObject(ContextNode contextNode, JSONObject jsonObject) throws JSONException {

		for (Entry<String, Object> entry : jsonObject.entrySet()) {

			String key = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof JSONObject) {

				XDI3SubSegment arcXri = Multiplicity.entitySingletonArcXri(Dictionary.nativeIdentifierToInstanceXri(key));

				ContextNode innerContextNode = contextNode.createContextNode(arcXri);
				readJSONObject(innerContextNode, (JSONObject) value);
			} else if (value instanceof JSONArray) {

				XDI3SubSegment arcXri = Multiplicity.collectionArcXri(Dictionary.nativeIdentifierToInstanceXri(key));

				ContextNode innerContextNode = contextNode.createContextNode(arcXri);
				readJSONArray(innerContextNode, (JSONArray) value);
			} else {

				XDI3SubSegment arcXri = Multiplicity.attributeSingletonArcXri(Dictionary.nativeIdentifierToInstanceXri(key));

				ContextNode innerContextNode = contextNode.createContextNode(arcXri);
				createLiteral(innerContextNode, value);
			}
		}
	}

	private static void readJSONArray(ContextNode contextNode, JSONArray jsonArray) throws JSONException {

		for (Object value : jsonArray) {

			if (value instanceof JSONObject) {

				XDI3SubSegment arcXri = Multiplicity.entityMemberArcXriRandom();

				ContextNode innerContextNode = contextNode.createContextNode(arcXri);
				readJSONObject(innerContextNode, (JSONObject) value);
			} else if (value instanceof JSONArray) {

				XDI3SubSegment arcXri = Multiplicity.collectionArcXriRandom();

				ContextNode innerContextNode = contextNode.createContextNode(arcXri);
				readJSONArray(innerContextNode, (JSONArray) value);
			} else {

				XDI3SubSegment arcXri = Multiplicity.attributeMemberArcXriRandom();

				ContextNode innerContextNode = contextNode.createContextNode(arcXri);
				createLiteral(innerContextNode, value);
			}
		}
	}

	private static void createLiteral(ContextNode contextNode, Object value) {

		if (value instanceof String) {

			contextNode.createLiteral(value.toString());
		} else if (value instanceof Number) {

			Literal literal = contextNode.createLiteral(value.toString());
			DataTypes.setLiteralDataType(literal, XRI_DATATYPE_JSON_NUMBER);
		} else if (value instanceof Boolean) {

			Literal literal = contextNode.createLiteral(value.toString());

			if (value.equals(Boolean.TRUE)) DataTypes.setLiteralDataType(literal, XRI_DATATYPE_JSON_TRUE);
			if (value.equals(Boolean.FALSE)) DataTypes.setLiteralDataType(literal, XRI_DATATYPE_JSON_FALSE);
		} else if (value == null) {

			Literal literal = contextNode.createLiteral("null");
			DataTypes.setLiteralDataType(literal, XRI_DATATYPE_JSON_NULL);
		}
	}

	public void read(Graph graph, JSONObject graphObject) throws IOException, Xdi2ParseException, JSONException {

		readJSONObject(graph.getRootContextNode(), graphObject);
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
