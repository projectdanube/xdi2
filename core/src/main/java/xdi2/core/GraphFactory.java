package xdi2.core;

import java.io.IOException;
import java.util.Properties;

import xdi2.core.exceptions.Xdi2ParseException;

/**
 * A graph factory can construct new XDI graph implementations.
 * 
 * @author markus
 */
public interface GraphFactory {

	/**
	 * Creates/opens a graph.
	 * @return An already existing or new graph.
	 */
	public Graph openGraph() throws IOException;

	/**
	 * Creates/opens a graph and fills it with content in one of the serialization formats.
	 * @return An already existing or new graph.
	 */
	public Graph parseGraph(String graph) throws IOException, Xdi2ParseException;

	/**
	 * Creates/opens a graph and fills it with content in one of the serialization formats.
	 * @return An already existing or new graph.
	 */
	public Graph parseGraph(String graph, String format, Properties parameters) throws IOException, Xdi2ParseException;
}
