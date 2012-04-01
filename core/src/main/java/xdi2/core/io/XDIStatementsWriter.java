package xdi2.core.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;

public class XDIStatementsWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -1653073796384849940L;

	private static final String HTML_COLOR_CONTEXTNODE = "#2f2853";
	private static final String HTML_COLOR_RELATION = "#722f38";
	private static final String HTML_COLOR_LITERAL = "#38652a";
	
	public static final String PARAMETER_WRITE_CONTEXT_STATEMENTS = "writeContextStatements";
	public static final String PARAMETER_WRITE_HTML = "writeHtml";
	public static final String DEFAULT_WRITE_CONTEXT_STATEMENTS = "false";
	public static final String DEFAULT_WRITE_HTML = "false";

	public static final String FORMAT_NAME = "STATEMENTS";
	public static final String MIME_TYPE = "text/plain";
	public static final String DEFAULT_FILE_EXTENSION = "xdi";

	public static void write(Graph graph, BufferedWriter bufferedWriter, Properties parameters) throws IOException {

		boolean writeContextStatements = Boolean.parseBoolean(parameters.getProperty(PARAMETER_WRITE_CONTEXT_STATEMENTS, DEFAULT_WRITE_CONTEXT_STATEMENTS));
		boolean writeHtml = Boolean.parseBoolean(parameters.getProperty(PARAMETER_WRITE_HTML, DEFAULT_WRITE_HTML));

		if (writeHtml) {

			bufferedWriter.write("<html><head><title>XDI Graph</title></head>\n");
			bufferedWriter.write("<body style=\"font-family:monospace;font-size:14pt;font-weight:bold;\">\n");
		}

		for (Iterator<Statement> statements = graph.getRootContextNode().getAllStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();

			if ((! writeContextStatements) &&
					(statement instanceof ContextNodeStatement) &&
					(! ((ContextNodeStatement) statement).getContextNode().isEmpty())) {

				continue;
			}

			if (writeHtml) {

				if (statement instanceof ContextNodeStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_CONTEXTNODE + "\">");
					bufferedWriter.write(statement.toString());
					bufferedWriter.write("</span><br>\n");
				} else if (statement instanceof RelationStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_RELATION + "\">");
					bufferedWriter.write(statement.toString());
					bufferedWriter.write("</span><br>\n");
				} else if (statement instanceof LiteralStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_LITERAL + "\">");
					bufferedWriter.write(statement.toString());
					bufferedWriter.write("</span><br>\n");
				}
			} else {

				bufferedWriter.write(statement.toString() + "\n");
			}
		}

		if (writeHtml) {

			bufferedWriter.write("</body></html>\n");
		}

		bufferedWriter.flush();
	}

	public Writer write(Graph graph, Writer writer, Properties parameters) throws IOException {

		if (parameters == null) parameters = new Properties();

		write(graph, new BufferedWriter(writer), parameters);
		writer.flush();

		return writer;
	}
}
