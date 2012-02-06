package old;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.GraphFactory;
import org.eclipse.higgins.xdi4j.exceptions.ParseException;
import org.eclipse.higgins.xdi4j.io.XDIReaderRegistry;

public abstract class AbstractGraphFactory implements GraphFactory {

	public Graph parseGraph(String string) throws IOException, ParseException {

		Graph graph = this.openGraph();
		XDIReaderRegistry.getAuto().read(graph, string, null);

		return graph;
	}

	public Graph parseGraph(String string, String format, Properties parameters) throws IOException, ParseException {

		Graph graph = this.openGraph();
		XDIReaderRegistry.forFormat(format).read(graph, string, null);

		return graph;
	}
}
