package xdi2.impl;

import java.io.IOException;
import java.util.Properties;

import xdi2.Graph;
import xdi2.GraphFactory;
import xdi2.exceptions.Xdi2ParseException;
import xdi2.io.XDIReaderRegistry;

public abstract class AbstractGraphFactory implements GraphFactory {

	public Graph parseGraph(String string) throws IOException, Xdi2ParseException {

		Graph graph = this.openGraph();
		XDIReaderRegistry.getAuto().read(graph, string, null);

		return graph;
	}

	public Graph parseGraph(String string, String format, Properties parameters) throws IOException, Xdi2ParseException {

		Graph graph = this.openGraph();
		XDIReaderRegistry.forFormat(format).read(graph, string, null);

		return graph;
	}
}
