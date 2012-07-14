package xdi2.core.io.writers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;

public class XDIStatementsWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -1653073796384849940L;

	private static final Logger log = LoggerFactory.getLogger(XDIStatementsWriter.class);

	public static final String FORMAT_NAME = "STATEMENTS";
	public static final String FILE_EXTENSION = "xdi";
	public static final MimeType[] MIME_TYPES = new MimeType[] { new MimeType("text/xdi"), new MimeType("text/xdi;contexts=0") };

	public static final String PARAMETER_WRITE_CONTEXT_STATEMENTS = "writeContextStatements";
	public static final String PARAMETER_WRITE_HTML = "writeHtml";
	public static final String PARAMETER_WRITE_ORDERED = "writeOrdered";
	public static final String DEFAULT_WRITE_CONTEXT_STATEMENTS = "false";
	public static final String DEFAULT_WRITE_HTML = "false";
	public static final String DEFAULT_WRITE_ORDERED = "false";

	private static final String HTML_COLOR_CONTEXTNODE = "#000000";
	private static final String HTML_COLOR_RELATION = "#ff8888";
	private static final String HTML_COLOR_LITERAL = "#8888ff";

	public static void write(Graph graph, BufferedWriter bufferedWriter, boolean writeContextStatements, boolean writeHtml, boolean writeOrdered) throws IOException {

		if (writeHtml) {

			bufferedWriter.write("<html><head><title>XDI Graph</title></head>\n");
			bufferedWriter.write("<body style=\"font-family:monospace;font-size:14pt;font-weight:bold;\">\n");
		}

		Iterator<Statement> statements;

		if (writeOrdered) {

			MemoryGraphFactory memoryGraphFactory = new MemoryGraphFactory();
			memoryGraphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ALPHA);
			Graph orderedGraph = memoryGraphFactory.openGraph();
			CopyUtil.copyGraph(graph, orderedGraph, null);
			graph = orderedGraph;

			List<Iterator<Statement>> list = new ArrayList<Iterator<Statement>> ();
			list.add(new MappingContextNodeStatementIterator(graph.getRootContextNode().getAllContextNodes()));
			list.add(new MappingRelationStatementIterator(graph.getRootContextNode().getAllRelations()));
			list.add(new MappingLiteralStatementIterator(graph.getRootContextNode().getAllLiterals()));

			statements = new CompositeIterator<Statement> (list.iterator());
		} else {

			statements = graph.getRootContextNode().getAllStatements();
		}

		while (statements.hasNext()) {

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

		// check parameters

		if (parameters == null) parameters = new Properties();

		boolean writeContextStatements = Boolean.parseBoolean(parameters.getProperty(PARAMETER_WRITE_CONTEXT_STATEMENTS, DEFAULT_WRITE_CONTEXT_STATEMENTS));
		boolean writeHtml = Boolean.parseBoolean(parameters.getProperty(PARAMETER_WRITE_HTML, DEFAULT_WRITE_HTML));
		boolean writeOrdered = Boolean.parseBoolean(parameters.getProperty(PARAMETER_WRITE_ORDERED, DEFAULT_WRITE_ORDERED));

		log.debug("Parameters: writeContextStatements=" + writeContextStatements + ", writeHtml=" + writeHtml + ", writeOrdered=" + writeOrdered);

		// write

		write(graph, new BufferedWriter(writer), writeContextStatements, writeHtml, writeOrdered);
		writer.flush();

		return writer;
	}
}
