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
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;

public class XDIDisplayWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -1653073796384849940L;

	private static final Logger log = LoggerFactory.getLogger(XDIDisplayWriter.class);

	public static final String FORMAT_NAME = "XDI DISPLAY";
	public static final String FILE_EXTENSION = "xdi";
	public static final MimeType MIME_TYPE = new MimeType("text/xdi");

	private static final String HTML_COLOR_CONTEXTNODE = "#000000";
	private static final String HTML_COLOR_RELATION = "#ff8888";
	private static final String HTML_COLOR_LITERAL = "#8888ff";

	private boolean writeContexts;
	private boolean writeOrdered;
	private boolean writeHtml;
	private boolean prettyPrint;

	public XDIDisplayWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeContexts = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_CONTEXTS, XDIWriterRegistry.DEFAULT_CONTEXTS));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writeHtml = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_HTML, XDIWriterRegistry.DEFAULT_HTML));

		try {

			int prettyParam = Integer.parseInt(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));
			this.prettyPrint = prettyParam > 0 ? true : false;
		} catch (NumberFormatException nfe) {

			this.prettyPrint = false;
		}

		if (log.isDebugEnabled()) log.debug("Parameters: writeContexts=" + this.writeContexts + ", writeOrdered=" + this.writeOrdered + ", writeHtml=" + this.writeHtml + ", prettyPrint=" + this.prettyPrint);
	}

	public void write(Graph graph, BufferedWriter bufferedWriter) throws IOException {

		if (this.writeHtml) {

			bufferedWriter.write("<html><head><title>XDI Graph</title></head>\n");
			bufferedWriter.write("<body style=\"font-family:monospace;font-size:14pt;font-weight:bold;\">\n");
			bufferedWriter.write("<pre>\n");
		}

		Iterator<Statement> statements;

		if (this.writeOrdered) {

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

			// ignore implied context nodes

			if ((! this.writeContexts) && StatementUtil.isImplied(statement)) continue;

			// HTML output

			if (this.writeHtml) {

				if (statement instanceof ContextNodeStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_CONTEXTNODE + "\">");
					writeStatement(bufferedWriter, statement, this.prettyPrint, true);
					bufferedWriter.write("</span>\n");
				} else if (statement instanceof RelationStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_RELATION + "\">");
					writeStatement(bufferedWriter, statement, this.prettyPrint, true);
					bufferedWriter.write("</span>\n");
				} else if (statement instanceof LiteralStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_LITERAL + "\">");
					writeStatement(bufferedWriter, statement, this.prettyPrint, true);
					bufferedWriter.write("</span>\n");
				}
			} else {

				writeStatement(bufferedWriter, statement, this.prettyPrint, false);
			}
		}

		if (this.writeHtml) {

			bufferedWriter.write("</pre>\n");
			bufferedWriter.write("</body></html>\n");
		}

		bufferedWriter.flush();
	}

	private static void writeStatement(BufferedWriter bufferedWriter, Statement statement, boolean pretty, boolean html) throws IOException {

		StringBuilder builder = new StringBuilder();

		builder.append(statement.getSubject());
		builder.append(pretty ? "\t" : "/");
		builder.append(statement.getPredicate());
		builder.append(pretty ? "\t" : "/");
		builder.append(statement.getObject());
		builder.append(html ? "<br>\n" : "\n");

		String string = builder.toString();
		if (html) string = string.replaceAll("\t", "&#9;");

		bufferedWriter.write(string);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		this.write(graph, new BufferedWriter(writer));
		writer.flush();

		return writer;
	}
}
