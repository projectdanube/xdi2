package xdi2.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Properties;

import xdi2.Graph;
import xdi2.exceptions.ParseException;

/**
 * If you extend this class, you only have to implement read(Graph, Reader, Properties)
 * 
 * @author markus
 */
public abstract class AbstractXDIReader implements XDIReader {

	private static final long serialVersionUID = -3924954880534200486L;

	public synchronized void read(Graph graph, String string, Properties parameters) throws IOException, ParseException {

		this.read(graph, new StringReader(string), parameters);
	}

	public synchronized void read(Graph graph, InputStream stream, Properties parameters) throws IOException, ParseException {

		this.read(graph, new InputStreamReader(stream), parameters);
	}
}
