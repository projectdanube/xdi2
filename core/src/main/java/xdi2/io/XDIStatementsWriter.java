package xdi2.io;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import xdi2.Graph;
import xdi2.Statement;
import xdi2.Statement.ContextNodeStatement;

class XDIStatementsWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -1653073796384849940L;

	public static final String PARAMETER_WRITE_CONTEXT_STATEMENTS = "writeContextStatements";
	public static final String DEFAULT_WRITE_CONTEXT_STATEMENTS = "false";

	protected static final String FORMAT_TYPE = "STATEMENTS";
	protected static final String[] MIME_TYPES = new String[] { "text/plain" };
	protected static final String DEFAULT_FILE_EXTENSION = "xdi";

	XDIStatementsWriter() { }

	public static void write(Graph graph, BufferedWriter bufferedWriter, Properties parameters) throws IOException {

		boolean writeContextStatements = Boolean.parseBoolean(parameters.getProperty(PARAMETER_WRITE_CONTEXT_STATEMENTS, DEFAULT_WRITE_CONTEXT_STATEMENTS));

		for (Iterator<Statement> statements = graph.getRootContextNode().getAllStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();

			if ((! writeContextStatements) &&
					(statement instanceof ContextNodeStatement) &&
					(! ((ContextNodeStatement) statement).getContextNode().isEmpty())) {

				continue;
			}

			bufferedWriter.write(statement.toString() + "\n");
		}

		bufferedWriter.flush();
	}

	public Writer write(Graph graph, Writer writer, Properties parameters) throws IOException {

		if (parameters == null) parameters = new Properties();

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
