package xdi2.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;

/**
 * Provides methods for reading an XDI graph.
 *
 * @author markus
 */
public interface XDIReader extends Serializable {

	/**
	 * Reads an XDI graph from a character stream.
	 * @param graph A graph that will hold the read data.
	 * @param reader A character stream from which to read.
	 * @return The character stream.
	 */
	public Reader read(Graph graph, Reader reader) throws IOException, Xdi2ParseException;

	/**
	 * Reads an XDI graph from a byte stream.
	 * @param graph A graph that will hold the read data.
	 * @param stream A byte stream from which to read.
	 * @return The byte stream.
	 */
	public InputStream read(Graph graph, InputStream stream) throws IOException, Xdi2ParseException;

	/**
	 * Returns the format this XDIReader can read, e.g.
	 * <ul>
	 * <li>XDI/JSON</li>
	 * <li>XDI DISPLAY</li>
	 * </ul>
	 * @return The format of this XDIReader.
	 */
	public String getFormat();

	/**
	 * Returns the file extension of this XDIReader, e.g.
	 * <ul>
	 * <li>.xdi</li>
	 * <li>.json</li>
	 * </ul>
	 * @return The file extension of this XDIReader.
	 */
	public String getFileExtension();

	/**
	 * Returns the mime type this XDIReader can read, e.g.
	 * <ul>
	 * <li>text/plain</li>
	 * <li>application/xdi+json</li>
	 * </ul>
	 * @return The mime type of this XDIReader.
	 */
	public MimeType getMimeType();

	/**
	 * Checks if a given format is supported.
	 * @return True, if supported.
	 */
	public boolean supportsFormat(String format);

	/**
	 * Checks if a given file extension is supported.
	 * @return True, if supported.
	 */
	public boolean supportsFileExtension(String fileExtension);

	/**
	 * Checks if a given mime type is supported.
	 * @return True, if supported.
	 */
	public boolean supportsMimeType(MimeType mimeType);
}
