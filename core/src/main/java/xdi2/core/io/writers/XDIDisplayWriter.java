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
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

public class XDIDisplayWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -1653073796384849940L;

	private static final Logger log = LoggerFactory.getLogger(XDIDisplayWriter.class);

	public static final String FORMAT_NAME = "XDI DISPLAY";
	public static final String FILE_EXTENSION = "xdi";
	public static final MimeType MIME_TYPE = new MimeType("text/xdi");

	private static final String HTML_COLOR_CONTEXTNODE = "#000000";
	private static final String HTML_COLOR_RELATION = "#ff8888";
	private static final String HTML_COLOR_LITERAL = "#8888ff";

	private boolean writeImplied;
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

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writeInner = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_INNER, XDIWriterRegistry.DEFAULT_INNER));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));
		this.writeHtml = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_HTML, XDIWriterRegistry.DEFAULT_HTML));

		if (log.isTraceEnabled()) log.trace("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writeInner=" + this.writeInner + ", writePretty=" + this.writePretty + ", writeHtml=" + this.writeHtml);
	}

	public void write(Graph graph, BufferedWriter bufferedWriter) throws IOException {

		// write html?

		if (this.writeHtml) {

			bufferedWriter.write("<html><head><title>XDI Graph</title></head>\n");
			bufferedWriter.write("<body style=\"font-family:monospace;font-size:14pt;font-weight:bold;\">\n");
			bufferedWriter.write("<pre>\n");
		}

		// write ordered?

		IterableIterator<Statement> statements;

		if (this.writeOrdered) {

			MemoryGraphFactory memoryGraphFactory = new MemoryGraphFactory();
			memoryGraphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ALPHA);
			Graph orderedGraph = memoryGraphFactory.openGraph();
			CopyUtil.copyGraph(graph, orderedGraph, null);
			graph = orderedGraph;

			List<Iterator<? extends Statement>> list = new ArrayList<Iterator<? extends Statement>> ();
			list.add(new MappingContextNodeStatementIterator(graph.getRootContextNode().getAllContextNodes()));
			list.add(new MappingRelationStatementIterator(graph.getRootContextNode().getAllRelations()));
			list.add(new MappingLiteralStatementIterator(graph.getRootContextNode().getAllLiterals()));

			statements = new CompositeIterator<Statement> (list.iterator());
		} else {

			statements = graph.getRootContextNode().getAllStatements();
		}

		// ignore implied statements

		if (! this.writeImplied) statements = new SelectingNotImpliedStatementIterator<Statement> (statements);

		// write the statements

		for (Statement statement : statements) {

			// HTML output

			if (this.writeHtml) {

				if (statement instanceof ContextNodeStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_CONTEXTNODE + "\">");
					this.writeStatement(bufferedWriter, statement);
					bufferedWriter.write("</span>\n");
				} else if (statement instanceof RelationStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_RELATION + "\">");
					this.writeStatement(bufferedWriter, statement);
					bufferedWriter.write("</span>\n");
				} else if (statement instanceof LiteralStatement) {

					bufferedWriter.write("<span style=\"color:" + HTML_COLOR_LITERAL + "\">");
					this.writeStatement(bufferedWriter, statement);
					bufferedWriter.write("</span>\n");
				}
			} else {

				this.writeStatement(bufferedWriter, statement);
				bufferedWriter.write("\n");
			}
		}

		if (this.writeHtml) {

			bufferedWriter.write("</pre>\n");
			bufferedWriter.write("</body></html>\n");
		}

		bufferedWriter.flush();
	}

	private void writeStatement(BufferedWriter bufferedWriter, Statement statement) throws IOException {

		XDI3Statement statementXri = statement.getXri();

		// inner root short notation?

		if (this.writeInner) statementXri = transformStatementInInnerRoot(statementXri);

		// write the statement

		StringBuilder builder = new StringBuilder();

		builder.append(statementXri.getSubject());
		builder.append(this.writePretty ? "\t" : "/");
		builder.append(statementXri.getPredicate());
		builder.append(this.writePretty ? "\t" : "/");
		builder.append(StatementUtil.statementObjectToString(statementXri.getObject()));

		String string = builder.toString();
		if (this.writePretty && this.writeHtml) string = string.replaceAll("\t", "&#9;");

		bufferedWriter.write(string);
	}

	private static XDI3Statement transformStatementInInnerRoot(XDI3Statement statementXri) {

		XDI3SubSegment subjectFirstSubSegment = statementXri.getSubject().getFirstSubSegment();

		if ((! subjectFirstSubSegment.hasXRef()) || (! subjectFirstSubSegment.getXRef().hasPartialSubjectAndPredicate())) return statementXri;

		XDI3Segment innerRootSubject = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialSubject();
		XDI3Segment innerRootPredicate = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialPredicate();

		XDI3Statement reducedStatementXri = StatementUtil.removeStartXriStatement(statementXri, XDI3Segment.create(subjectFirstSubSegment), true);
		if (reducedStatementXri == null) return statementXri;

		return XDI3Statement.create("" + innerRootSubject + "/" + innerRootPredicate + "/(" + transformStatementInInnerRoot(reducedStatementXri) + ")");
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		this.write(graph, new BufferedWriter(writer));
		writer.flush();

		return writer;
	}
}
