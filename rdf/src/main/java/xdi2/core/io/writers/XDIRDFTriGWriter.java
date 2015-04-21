package xdi2.core.io.writers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.WriterConfig;
import org.openrdf.rio.helpers.BasicWriterSettings;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralNodeStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;
import xdi2.rdf.XDI2RDF;

public class XDIRDFTriGWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -7401855287387482242L;

	public static final String FORMAT_NAME = "XDI/RDF/TriG";
	public static final String FILE_EXTENSION = "trig";
	public static final MimeType MIME_TYPE = new MimeType("application/x-trig");

	public XDIRDFTriGWriter(Properties parameters) {

		super(parameters);
	}

	public void write(Graph graph, BufferedWriter bufferedWriter) throws IOException {

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
			list.add(new MappingLiteralNodeStatementIterator(orderedGraph.getRootContextNode(true).getAllLiterals()));

			statements = new CompositeIterator<Statement> (list.iterator());
		} else {

			statements = graph.getRootContextNode(true).getAllStatements();
		}

		// ignore implied statements

		if (! this.isWriteImplied()) statements = new SelectingNotImpliedStatementIterator(statements);

		// write the statements

		Model model = XDI2RDF.graphToModel(statements);

		WriterConfig writerConfig = new WriterConfig();
		writerConfig.set(BasicWriterSettings.PRETTY_PRINT, Boolean.valueOf(this.isWritePretty()));

		try {

			Rio.write(model, bufferedWriter, RDFFormat.TRIG, writerConfig);
		} catch (RDFHandlerException ex) {

			throw new IOException("Problem writing RDF: " + ex.getMessage(), ex);
		}

		bufferedWriter.flush();

		// done

		if (orderedGraph != null) orderedGraph.close();
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		this.write(graph, new BufferedWriter(writer));
		writer.flush();

		return writer;
	}
}
