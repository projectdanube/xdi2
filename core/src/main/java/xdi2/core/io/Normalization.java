package xdi2.core.io;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class Normalization {

	private static Logger log = LoggerFactory.getLogger(Normalization.class.getName());

	private static final XDIWriter XDIWRITER;

	static {

		Properties parameters = new Properties();
		parameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "0");

		XDIWRITER = new XDIJSONWriter(parameters);
	}

	/**
	 * Returns the normalized serialization string of a context node, to be used
	 * e.g. for signatures and encryptions.
	 */
	public static String normalize(ContextNode contextNode, CopyStrategy copyStrategy) {

		Graph tempGraph;

		tempGraph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNode(contextNode, tempGraph, copyStrategy);

		StringWriter buffer = new StringWriter();
		String string;

		try {

			XDIWRITER.write(tempGraph, buffer);
			string = buffer.toString();
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot serialize " + contextNode + ": " + ex.getMessage(), ex);
		} finally {

			try { buffer.close(); } catch (Exception ex) { }

			tempGraph.close();
		}

		if (log.isDebugEnabled()) log.debug("Normalized context node " + contextNode.getXDIAddress() + ": " + string);

		return string;
	}

	/**
	 * Returns the normalized serialization string of a context node, to be used
	 * e.g. for signatures and encryptions.
	 */
	public static String normalize(ContextNode contextNode) {

		return normalize(contextNode, null);
	}

	/**
	 * Returns the normalized serialization string of a graph, to be used
	 * e.g. for signatures and encryptions.
	 */
	public static String normalize(Graph graph, CopyStrategy copyStrategy) {

		return normalize(graph.getRootContextNode(), copyStrategy);
	}

	/**
	 * Returns the normalized serialization string of a graph, to be used
	 * e.g. for signatures and encryptions.
	 */
	public static String normalize(Graph graph) {

		return normalize(graph, null);
	}
}
