package xdi2.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.io.MimeType;

/**
 * A graph factory can construct new XDI graph implementations.
 * 
 * @author markus
 */
public interface GraphFactory {

	/**
	 * Opens a graph.
	 * @return An already existing or new graph.
	 */
	public Graph openGraph() throws IOException;

	/**
	 * Opens a graph.
	 * @param identifier An optional identifier to distinguish graphs from one another.
	 * @return An already existing or new graph.
	 */
	public Graph openGraph(String identifier) throws IOException;

	/**
	 * Opens a graph and fills it with content in one of the serialization formats.
	 * @return A new graph.
	 */
	public Graph loadGraph(URL url) throws IOException, Xdi2ParseException;

	/**
	 * Opens a graph and fills it with content in one of the serialization formats.
	 * @return A new graph.
	 */
	public Graph loadGraph(File file) throws IOException, Xdi2ParseException;

	/**
	 * Opens a graph and fills it with content in one of the serialization formats.
	 * @return A new graph.
	 */
	public Graph loadGraph(InputStream inputStream) throws IOException, Xdi2ParseException;

	/**
	 * Opens a graph and fills it with content in one of the serialization formats.
	 * @return A new graph.
	 */
	public Graph loadGraph(Reader reader) throws IOException, Xdi2ParseException;

	/**
	 * Opens a graph and fills it with content in one of the serialization formats.
	 * @return A new graph.
	 */
	public Graph parseGraph(String graph) throws IOException, Xdi2ParseException;

	/**
	 * Opens a graph and fills it with content in one of the serialization formats.
	 * @return A new graph.
	 */
	public Graph parseGraph(String graph, String format, Properties parameters) throws IOException, Xdi2ParseException;

	/**
	 * Opens a graph and fills it with content in one of the serialization formats.
	 * @return A new graph.
	 */
	public Graph parseGraph(String graph, MimeType mimeType) throws IOException, Xdi2ParseException;
}
