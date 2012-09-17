package xdi2.core.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReaderRegistry;

public abstract class AbstractGraphFactory implements GraphFactory {

	@Override
	public Graph openGraph() throws IOException {

		return this.openGraph(null);
	}

	@Override
	public Graph parseGraph(String string) throws IOException, Xdi2ParseException {

		Graph graph = this.openGraph();
		XDIReaderRegistry.getAuto().read(graph, new StringReader(string));

		return graph;
	}

	@Override
	public Graph parseGraph(String string, String format, Properties parameters) throws IOException, Xdi2ParseException {

		Graph graph = this.openGraph();
		XDIReaderRegistry.forFormat(format, parameters).read(graph, new StringReader(string));

		return graph;
	}

	@Override
	public Graph parseGraph(String string, MimeType mimeType) throws IOException, Xdi2ParseException {

		Graph graph = this.openGraph();
		XDIReaderRegistry.forMimeType(mimeType).read(graph, new StringReader(string));

		return graph;
	}
}
