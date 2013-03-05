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
import xdi2.core.features.roots.InnerRoot;
import xdi2.core.features.roots.Root;
import xdi2.core.features.roots.Roots;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

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
	private boolean writeInner;
	private boolean writePretty;
	private boolean writeHtml;

	public XDIDisplayWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeContexts = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_CONTEXTS, XDIWriterRegistry.DEFAULT_CONTEXTS));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writeInner = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_INNER, XDIWriterRegistry.DEFAULT_INNER));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));
		this.writeHtml = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_HTML, XDIWriterRegistry.DEFAULT_HTML));

		if (log.isDebugEnabled()) log.debug("Parameters: writeContexts=" + this.writeContexts + ", writeOrdered=" + this.writeOrdered + ", writeInner=" + this.writeInner + ", writePretty=" + this.writePretty + ", writeHtml=" + this.writeHtml);
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

			if ((! this.writeContexts) && statement.isImplied()) continue;

			// HTML output

			if (this.writeHtml) {

				if (statement instanceof ContextNodeStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_CONTEXTNODE + "\">");
					writeStatement(bufferedWriter, statement, this.writeInner, this.writePretty, true);
					bufferedWriter.write("</span>\n");
				} else if (statement instanceof RelationStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_RELATION + "\">");
					writeStatement(bufferedWriter, statement, this.writeInner, this.writePretty, true);
					bufferedWriter.write("</span>\n");
				} else if (statement instanceof LiteralStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_LITERAL + "\">");
					writeStatement(bufferedWriter, statement, this.writeInner, this.writePretty, true);
					bufferedWriter.write("</span>\n");
				}
			} else {

				writeStatement(bufferedWriter, statement, this.writeInner, this.writePretty, false);
			}
		}

		if (this.writeHtml) {

			bufferedWriter.write("</pre>\n");
			bufferedWriter.write("</body></html>\n");
		}

		bufferedWriter.flush();
	}

	private static void writeStatement(BufferedWriter bufferedWriter, Statement statement, boolean inner, boolean pretty, boolean html) throws IOException {

		XDI3Statement statementXri = statement.getXri();

		if (inner) {

			Root root = Roots.findLocalRoot(statement.getGraph());

			while (true) {

				Root nextRoot = root.findRoot(statementXri.getSubject(), false);
				if (nextRoot == root) break;
				if (! (nextRoot instanceof InnerRoot)) break;
				root = nextRoot;

				InnerRoot innerRoot = (InnerRoot) root;

				XDI3Segment subject = innerRoot.getSubjectOfInnerRoot();
				XDI3Segment predicate = innerRoot.getPredicateOfInnerRoot();

				XDI3Segment relativeSubject = root.getRelativePart(statement.getSubject());
				XDI3Segment relativePredicate = statement.getPredicate();
				XDI3Segment relativeObject = root.getRelativePart(statement.getObject());

				System.err.println("BEFORE: " + statementXri);
				statementXri = XDI3Statement.create("" + subject + "/" + predicate + "/(" + relativeSubject + "/" + relativePredicate + "/" + relativeObject + ")");
				System.err.println("AFTER: " + statementXri);

				root = innerRoot;
			}
		}

		StringBuilder builder = new StringBuilder();

		builder.append(statementXri.getSubject());
		builder.append(pretty ? "\t" : "/");
		builder.append(statementXri.getPredicate());
		builder.append(pretty ? "\t" : "/");
		builder.append(statementXri.getObject());
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
