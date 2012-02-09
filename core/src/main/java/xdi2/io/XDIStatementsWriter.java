package xdi2.io;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import xdi2.Graph;
import xdi2.Statement;

class XDIStatementsWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -1653073796384849940L;

	protected static final String FORMAT_TYPE = "STATEMENTS";
	protected static final String[] MIME_TYPES = new String[] { "text/plain" };
	protected static final String DEFAULT_FILE_EXTENSION = ".xdi";

	XDIStatementsWriter() { }

	public static void write(Graph graph, BufferedWriter bufferedWriter, Properties parameters) throws IOException {

		for (Iterator<Statement> statements = graph.getRootContextNode().getAllStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();

			bufferedWriter.write(statement.toString() + "\n");
		}
		
		bufferedWriter.flush();
	}

	public Writer write(Graph graph, Writer writer, Properties parameters) throws IOException {

		write(graph, new BufferedWriter(writer), parameters);
		writer.flush();

		return writer;
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
