package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.xri3.impl.XDI3SubSegment;

public class XDIRawJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 5574875512968150162L;

	public static final String FORMAT_NAME = "RAW JSON";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	public XDIRawJSONReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	private static void readJSONObject(ContextNode contextNode, JSONObject jsonObject) throws JSONException {

		for (Iterator<?> keys = jsonObject.keys(); keys.hasNext(); ) {

			String key = (String) keys.next();
			Object value = jsonObject.get(key);

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
				innerContextNode.createLiteral(value.toString());
			}
		}
	}

	private static void readJSONArray(ContextNode contextNode, JSONArray jsonArray) throws JSONException {

		for (int i=0; i<jsonArray.length(); i++) {

			Object value = jsonArray.get(i);

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
				innerContextNode.createLiteral(value.toString());
			}
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

		this.read(graph, new JSONObject(graphString.toString()));
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
