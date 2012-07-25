package xdi2.core.io;



import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

import xdi2.core.Graph;

/**
 * Provides methods for writing an XDI graph.
 *
 * @author markus
 */
public interface XDIWriter extends Serializable {

	/**
	 * Writes an XDI graph to a character stream.
	 * @param graph A graph that will be written to the stream.
	 * @param writer The character stream to write to.
	 * @return The character stream.
	 */
	public Writer write(Graph graph, Writer writer) throws IOException;

	/**
	 * Writes an XDI graph to a byte stream.
	 * @param graph A graph that will be written to the stream.
	 * @param stream The byte stream to write to.
	 * @return The byte stream.
	 */
	public OutputStream write(Graph graph, OutputStream stream) throws IOException;

	/**
	 * Returns the format this XDIWriter can write, e.g.
	 * <ul>
	 * <li>XDI/JSON</li>
	 * <li>XDI DISPLAY</li>
	 * </ul>
	 * @return The format of this XDIWriter.
	 */
	public String getFormat();

	/**
	 * Returns the file extension of this XDIWriter, e.g.
	 * <ul>
	 * <li>.json</li>
	 * <li>.xdi</li>
	 * </ul>
	 * @return The file extension of this XDIWriter.
	 */
	public String getFileExtension();

	/**
	 * Returns the mime type this XDIWriter can write, e.g.
	 * <ul>
	 * <li>text/plain</li>
	 * <li>application/xdi+json</li>
	 * </ul>
	 * @return The mime type of this XDIWriter.
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
