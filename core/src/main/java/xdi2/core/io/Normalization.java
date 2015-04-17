package xdi2.core.io;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.readers.XDIJSONReader;
import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class Normalization {

	private static Logger log = LoggerFactory.getLogger(Normalization.class.getName());

	private static final XDIWriter XDIWRITER;
	private static final XDIReader XDIREADER;

	static {

		Properties parameters = new Properties();
		parameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "0");

		XDIWRITER = new XDIJSONWriter(parameters);

		XDIREADER = new XDIJSONReader(null);
	}

	/**
	 * Returns the normalized serialization string of a context node, to be used
	 * e.g. for signatures and encryptions.
	 * @throws IOException 
	 */
	public static String serialize(ContextNode contextNode, CopyStrategy copyStrategy) throws IOException {

		Graph tempGraph;

		tempGraph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNode(contextNode, tempGraph, copyStrategy);

		StringWriter buffer = new StringWriter();
		String string;

		try {

			XDIWRITER.write(tempGraph, buffer);
			string = buffer.toString();
		} catch (IOException ex) {

			throw ex;
		} finally {

			try { buffer.close(); } catch (Exception ex) { }

			tempGraph.close();
		}

		if (log.isDebugEnabled()) log.debug("Normalized context node " + contextNode.getXDIAddress() + ": " + string);

		return string;
	}

	public static Graph deserialize(String string) throws Xdi2ParseException, IOException {

		Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();

		try {

			XDIREADER.read(tempGraph, new StringReader(string));
		} catch (Xdi2ParseException ex) {

			tempGraph.close();
			throw ex;
		} catch (IOException ex) {

			tempGraph.close();
			throw ex;
		}

		return tempGraph;
	}
}
