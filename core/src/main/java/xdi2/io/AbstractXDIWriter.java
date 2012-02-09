package xdi2.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import xdi2.Graph;

/**
 * If you extend this class, you only have to implement write(Graph, Writer, Properties)
 * 
 * @author markus
 */
public abstract class AbstractXDIWriter implements XDIWriter {

	private static final long serialVersionUID = -4120729667091454408L;

	public synchronized OutputStream write(Graph graph, OutputStream stream, Properties parameters) throws IOException {

		this.write(graph, new OutputStreamWriter(stream), parameters);
		stream.flush();

		return stream;
	}
}
