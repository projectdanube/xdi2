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

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAbstractRoot;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.impl.AbstractLiteral;
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

	private static final String HTML_COLOR_ROOT = "#ff7f7f";
	private static final String HTML_COLOR_ENTITY = "#7fff7f";
	private static final String HTML_COLOR_ATTRIBUTE = "#7f7fff";

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

			this.writeStatement(bufferedWriter, statement.getXri());

			// HTML output

			if (this.writeHtml) {

				bufferedWriter.write("<br>\n");
			} else {

				bufferedWriter.write("\n");
			}
		}

		if (this.writeHtml) {

			bufferedWriter.write("</pre>\n");
			bufferedWriter.write("</body></html>\n");
		}

		bufferedWriter.flush();
	}

	private void writeStatement(BufferedWriter bufferedWriter, XDI3Statement statementXri) throws IOException {

		// inner root short notation?

		if (this.writeInner) statementXri = transformStatementInInnerRoot(statementXri);

		// write the statement

		this.writeContextNodeXri(bufferedWriter, statementXri.getSubject());
		this.writeSeparator(bufferedWriter);
		this.writePredicateXri(bufferedWriter, statementXri.getPredicate());
		this.writeSeparator(bufferedWriter);

		if (statementXri.isContextNodeStatement()) {

			this.writeContextNodeArcXri(bufferedWriter, statementXri.getSubject(), (XDI3SubSegment) statementXri.getObject());
		} else if (statementXri.isRelationStatement()) {

			if (statementXri.hasInnerRootStatement()) {

				this.writeInnerRootStatement(bufferedWriter, statementXri.getInnerRootStatement());
			} else {

				this.writeContextNodeXri(bufferedWriter, (XDI3Segment) statementXri.getObject());
			}
		} else if (statementXri.isLiteralStatement()) {

			this.writeLiteralData(bufferedWriter, statementXri.getObject());
		}
	}

	private void writeSeparator(BufferedWriter bufferedWriter) throws IOException {

		if (this.writePretty && this.writeHtml) {

			bufferedWriter.write("&#9;/&#9;");
		} else if (this.writePretty) {

			bufferedWriter.write("\t/\t");
		} else {

			bufferedWriter.write("/");
		}
	}

	private void writeOpenInnerRoot(BufferedWriter bufferedWriter) throws IOException {

		if (this.writePretty && this.writeHtml) {

			bufferedWriter.write("(&#9;");
		} else if (this.writePretty) {

			bufferedWriter.write("(\t");
		} else {

			bufferedWriter.write("(");
		}
	}

	private void writeCloseInnerRoot(BufferedWriter bufferedWriter) throws IOException {

		if (this.writePretty && this.writeHtml) {

			bufferedWriter.write("&#9;)");
		} else if (this.writePretty) {

			bufferedWriter.write("\t)");
		} else {

			bufferedWriter.write(")");
		}
	}

	private void writeContextNodeXri(BufferedWriter bufferedWriter, XDI3Segment contextNodeXri) throws IOException {

		if (this.writeHtml) {

			ContextNode contextNode = MemoryGraphFactory.getInstance().openGraph().getRootContextNode();

			for (XDI3SubSegment contextNodeArcXri : contextNodeXri.getSubSegments()) {

				this.writeContextNodeArcXri(bufferedWriter, contextNode, contextNodeArcXri);

				if (! XDIConstants.XRI_S_ROOT.equals(contextNodeArcXri)) {

					contextNode = contextNode.setContextNode(contextNodeArcXri);
				}
			}
		} else {

			bufferedWriter.write(contextNodeXri.toString());
		}
	}

	private void writeContextNodeArcXri(BufferedWriter bufferedWriter, XDI3Segment contextNodeXri, XDI3SubSegment contextNodeArcXri) throws IOException {

		ContextNode contextNode = MemoryGraphFactory.getInstance().openGraph().getRootContextNode();

		if (! XDIConstants.XRI_S_ROOT.equals(contextNodeXri)) {

			contextNode = contextNode.setDeepContextNode(contextNodeXri);
		}

		this.writeContextNodeArcXri(bufferedWriter, contextNode, contextNodeArcXri);
	}

	private void writeContextNodeArcXri(BufferedWriter bufferedWriter, ContextNode contextNode, XDI3SubSegment contextNodeArcXri) throws IOException {

		if (! XDIConstants.XRI_S_ROOT.equals(contextNodeArcXri)) {

			contextNode = contextNode.setContextNode(contextNodeArcXri);
		}

		String htmlColorString = null;

		if (this.writeHtml) {

			if (XdiAbstractRoot.isValid(contextNode)) htmlColorString = HTML_COLOR_ROOT;
			if (XdiEntityCollection.isValid(contextNode) || XdiAbstractEntity.isValid(contextNode)) htmlColorString = HTML_COLOR_ENTITY;
			if (XdiAttributeCollection.isValid(contextNode) || XdiAbstractAttribute.isValid(contextNode)) htmlColorString = HTML_COLOR_ATTRIBUTE;
		}

		if (htmlColorString != null) bufferedWriter.write("<span style=\"background-color:" + htmlColorString + "\">");
		bufferedWriter.write(contextNodeArcXri.toString());
		if (htmlColorString != null) bufferedWriter.write("</span>");
	}

	private void writeInnerRootStatement(BufferedWriter bufferedWriter, XDI3Statement statementXri) throws IOException {

		this.writeOpenInnerRoot(bufferedWriter);

		if (this.writePretty || this.writeHtml) {

			this.writeStatement(bufferedWriter, statementXri);
		} else {

			bufferedWriter.write(statementXri.toString());
		}

		this.writeCloseInnerRoot(bufferedWriter);
	}

	@SuppressWarnings("static-method")
	private void writePredicateXri(BufferedWriter bufferedWriter, XDI3Segment predicateXri) throws IOException {

		bufferedWriter.write(predicateXri.toString());
	}

	@SuppressWarnings("static-method")
	private void writeLiteralData(BufferedWriter bufferedWriter, Object literalData) throws IOException {

		bufferedWriter.write(AbstractLiteral.literalDataToString(literalData));
	}

	private static XDI3Statement transformStatementInInnerRoot(XDI3Statement statementXri) {

		XDI3SubSegment subjectFirstSubSegment = statementXri.getSubject().getFirstSubSegment();

		if ((! subjectFirstSubSegment.hasXRef()) || (! subjectFirstSubSegment.getXRef().hasPartialSubjectAndPredicate())) return statementXri;

		XDI3Segment innerRootSubject = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialSubject();
		XDI3Segment innerRootPredicate = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialPredicate();

		XDI3Statement reducedStatementXri = StatementUtil.removeStartXriStatement(statementXri, XDI3Segment.fromComponent(subjectFirstSubSegment), true);
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
