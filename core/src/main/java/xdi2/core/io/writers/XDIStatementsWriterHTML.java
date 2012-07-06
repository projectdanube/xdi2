package xdi2.core.io.writers;


import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.io.MimeType;

public class XDIStatementsWriterHTML extends XDIStatementsWriter {

	private static final long serialVersionUID = 6165681888411006041L;

	public static final String FORMAT_NAME = "STATEMENTS_HTML";
	public static final String FILE_EXTENSION = "html";
	public static final MimeType[] MIME_TYPES = new MimeType[] { new MimeType("text/html") };

	@Override
	public Writer write(Graph graph, Writer writer, Properties parameters) throws IOException {

		if (parameters == null) parameters = new Properties();

		parameters.put(XDIStatementsWriter.PARAMETER_WRITE_CONTEXT_STATEMENTS, Boolean.TRUE.toString());
		parameters.put(XDIStatementsWriter.PARAMETER_WRITE_HTML, Boolean.TRUE.toString());
		parameters.put(XDIStatementsWriter.PARAMETER_WRITE_ORDERED, Boolean.TRUE.toString());

		return super.write(graph, writer, parameters);
	}
}
