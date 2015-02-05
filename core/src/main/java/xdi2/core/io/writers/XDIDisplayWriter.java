package xdi2.core.io.writers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;

public class XDIDisplayWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -1653073796384849940L;

	public static final String FORMAT_NAME = "XDI DISPLAY";
	public static final String FILE_EXTENSION = "xdi";
	public static final MimeType MIME_TYPE = new MimeType("text/xdi");

	private static final String HTML_COLOR_ROOT = "#ff7f7f";
	private static final String HTML_COLOR_ENTITY = "#7fff7f";
	private static final String HTML_COLOR_ATTRIBUTE = "#7f7fff";

	public XDIDisplayWriter(Properties parameters) {

		super(parameters);
	}

	public void write(Graph graph, BufferedWriter bufferedWriter) throws IOException {

		// write html?

		if (this.isWriteHtml()) {

			bufferedWriter.write("<html><head><title>XDI Graph</title></head>\n");
			bufferedWriter.write("<body style=\"font-family:monospace;font-size:14pt;font-weight:bold;\">\n");
			bufferedWriter.write("<pre>\n");
		}

		// write ordered?

		Graph orderedGraph = null;
		IterableIterator<Statement> statements;

		if (this.isWriteOrdered()) {

			MemoryGraphFactory memoryGraphFactory = new MemoryGraphFactory();
			memoryGraphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ALPHA);
			orderedGraph = memoryGraphFactory.openGraph();
			CopyUtil.copyGraph(graph, orderedGraph, null);

			List<Iterator<? extends Statement>> list = new ArrayList<Iterator<? extends Statement>> ();
			list.add(new MappingContextNodeStatementIterator(orderedGraph.getRootContextNode(true).getAllContextNodes()));
			list.add(new MappingRelationStatementIterator(orderedGraph.getRootContextNode(true).getAllRelations()));
			list.add(new MappingLiteralStatementIterator(orderedGraph.getRootContextNode(true).getAllLiterals()));

			statements = new CompositeIterator<Statement> (list.iterator());
		} else {

			statements = graph.getRootContextNode(true).getAllStatements();
		}

		// ignore implied statements

		if (! this.isWriteImplied()) statements = new SelectingNotImpliedStatementIterator(statements);

		// write the statements

		for (Statement statement : statements) {

			this.writeStatement(bufferedWriter, statement.getXDIStatement());

			// HTML output

			if (this.isWriteHtml()) {

				bufferedWriter.write("<br>\n");
			} else {

				bufferedWriter.write("\n");
			}
		}

		if (this.isWriteHtml()) {

			bufferedWriter.write("</pre>\n");
			bufferedWriter.write("</body></html>\n");
		}

		bufferedWriter.flush();

		// done

		if (orderedGraph != null) orderedGraph.close();
	}

	private void writeStatement(BufferedWriter bufferedWriter, XDIStatement XDIstatement) throws IOException {

		// write the statement

		this.writeContextNodeXDIAddress(bufferedWriter, XDIstatement.getSubject());
		this.writeSeparator(bufferedWriter);
		this.writePredicateAddress(bufferedWriter, XDIstatement.getPredicate());
		this.writeSeparator(bufferedWriter);

		if (XDIstatement.isContextNodeStatement()) {

			this.writecontextNodeXDIArc(bufferedWriter, XDIstatement.getSubject(), (XDIArc) XDIstatement.getObject());
		} else if (XDIstatement.isRelationStatement()) {

			this.writeContextNodeXDIAddress(bufferedWriter, (XDIAddress) XDIstatement.getObject());
		} else if (XDIstatement.isLiteralStatement()) {

			this.writeLiteralData(bufferedWriter, XDIstatement.getObject());
		}
	}

	private void writeSeparator(BufferedWriter bufferedWriter) throws IOException {

		if (this.isWritePretty() && this.isWriteHtml()) {

			bufferedWriter.write("&#9;/&#9;");
		} else if (this.isWritePretty()) {

			bufferedWriter.write("\t/\t");
		} else {

			bufferedWriter.write("/");
		}
	}

	private void writeContextNodeXDIAddress(BufferedWriter bufferedWriter, XDIAddress contextNodeXDIAddress) throws IOException {

		if (this.isWriteHtml()) {

			ContextNode contextNode = MemoryGraphFactory.getInstance().openGraph().getRootContextNode(false);

			for (XDIArc contextNodeXDIArc : contextNodeXDIAddress.getXDIArcs()) {

				this.writecontextNodeXDIArc(bufferedWriter, contextNode, contextNodeXDIArc);

				if (! XDIConstants.XDI_ADD_ROOT.equals(contextNodeXDIArc)) {

					contextNode = contextNode.setContextNode(contextNodeXDIArc);
				}
			}
		} else {

			bufferedWriter.write(contextNodeXDIAddress.toString());
		}
	}

	private void writecontextNodeXDIArc(BufferedWriter bufferedWriter, XDIAddress contextNodeXDIAddress, XDIArc contextNodeXDIArc) throws IOException {

		ContextNode contextNode = MemoryGraphFactory.getInstance().openGraph().getRootContextNode(false);

		if (! XDIConstants.XDI_ADD_ROOT.equals(contextNodeXDIAddress)) {

			contextNode = contextNode.setDeepContextNode(contextNodeXDIAddress);
		}

		this.writecontextNodeXDIArc(bufferedWriter, contextNode, contextNodeXDIArc);
	}

	private void writecontextNodeXDIArc(BufferedWriter bufferedWriter, ContextNode contextNode, XDIArc contextNodeXDIArc) throws IOException {

		if (! XDIConstants.XDI_ADD_ROOT.equals(contextNodeXDIArc)) {

			contextNode = contextNode.setContextNode(contextNodeXDIArc);
		}

		String htmlColorString = null;

		if (this.isWriteHtml()) {

			if (XdiAbstractRoot.isValid(contextNode)) htmlColorString = HTML_COLOR_ROOT;
			if (XdiEntityCollection.isValid(contextNode) || XdiAbstractEntity.isValid(contextNode)) htmlColorString = HTML_COLOR_ENTITY;
			if (XdiAttributeCollection.isValid(contextNode) || XdiAbstractAttribute.isValid(contextNode)) htmlColorString = HTML_COLOR_ATTRIBUTE;
		}

		if (htmlColorString != null) bufferedWriter.write("<span style=\"background-color:" + htmlColorString + "\">");
		bufferedWriter.write(contextNodeXDIArc.toString());
		if (htmlColorString != null) bufferedWriter.write("</span>");
	}

	@SuppressWarnings("static-method")
	private void writePredicateAddress(BufferedWriter bufferedWriter, XDIAddress predicateAddress) throws IOException {

		bufferedWriter.write(predicateAddress.toString());
	}

	@SuppressWarnings("static-method")
	private void writeLiteralData(BufferedWriter bufferedWriter, Object literalData) throws IOException {

		bufferedWriter.write(AbstractLiteral.literalDataToString(literalData));
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		this.write(graph, new BufferedWriter(writer));
		writer.flush();

		return writer;
	}
}
