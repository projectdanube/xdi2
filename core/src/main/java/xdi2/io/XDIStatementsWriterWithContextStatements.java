package xdi2.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import xdi2.Graph;

class XDIStatementsWriterWithContextStatements extends XDIStatementsWriter {

	private static final long serialVersionUID = -8401736205910361341L;

	protected static final String FORMAT_TYPE = "STATEMENTS_WITH_CONTEXT_STATEMENTS";
	protected static final String[] MIME_TYPES = new String[] { "text/plain" };
	protected static final String DEFAULT_FILE_EXTENSION = "xdi";

	public Writer write(Graph graph, Writer writer, Properties parameters) throws IOException {

		if (parameters == null) parameters = new Properties();
		
		parameters.put(XDIStatementsWriter.PARAMETER_WRITE_CONTEXT_STATEMENTS, Boolean.TRUE.toString());
		
		return super.write(graph, writer, parameters);
	}

	public String getFormat() {

		return FORMAT_TYPE;
	}

	public String[] getMimeTypes() {

		return MIME_TYPES;
	}

	public String getDefaultFileExtension() {

		return DEFAULT_FILE_EXTENSION;
	}
}
