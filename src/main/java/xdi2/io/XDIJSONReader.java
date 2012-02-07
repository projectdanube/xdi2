package xdi2.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.exceptions.ParseException;
import xdi2.exceptions.Xdi2GraphException;
import xdi2.xri3.impl.XRI3SubSegment;
import xdi2.xri3.impl.parser.ParserException;

class XDIJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	protected static final String FORMAT_TYPE = "XDI/JSON";
	protected static final String[] MIME_TYPES = new String[] { "application/xdi+json" };
	protected static final String DEFAULT_FILE_EXTENSION = "json";

	private String lastXriString;

	XDIJSONReader() { }

	private synchronized void read(ContextNode contextNode, JSONObject graphObject) throws IOException, ParseException, JSONException {

		JSONArray jsonArray = graphObject.getJSONArray(contextNode.getXri() + "/()");
		if (jsonArray == null) return;

		for (int i=0; i<jsonArray.length(); i++) {

			String arcXri = jsonArray.getString(i);
			ContextNode innerContextNode = contextNode.createContextNode(new XRI3SubSegment(arcXri));
			this.read(innerContextNode, graphObject);
		}
	}

	public synchronized void read(Graph graph, JSONObject graphObject) throws IOException, ParseException, JSONException {

		this.read(graph.getRootContextNode(), graphObject);
	}

	private void read(Graph graph, BufferedReader bufferedReader) throws IOException, ParseException, JSONException {

		String line;
		StringBuffer graphString = new StringBuffer();

		while ((line = bufferedReader.readLine()) != null) {

			graphString.append(line);
		}

		this.read(graph, new JSONObject(graphString.toString()));
	}

	public synchronized void read(Graph graph, Reader reader, Properties parameters) throws IOException, ParseException {

		this.lastXriString = null;

		try {

			this.read(graph, new BufferedReader(reader));
		} catch (JSONException ex) {

			throw new ParseException("JSON parse error: " + ex.getMessage(), ex);
		} catch (Xdi2GraphException ex) {

			throw new ParseException("Graph problem: " + ex.getMessage(), ex);
		} catch (ParserException ex) {

			throw new ParseException("Cannot parse XRI " + this.lastXriString + ": " + ex.getMessage(), ex);
		}
	}

	public String getFormat() {

		return(FORMAT_TYPE);
	}

	public String[] getMimeTypes() {

		return(MIME_TYPES);
	}

	public String getDefaultFileExtension() {

		return(DEFAULT_FILE_EXTENSION);
	}
}
