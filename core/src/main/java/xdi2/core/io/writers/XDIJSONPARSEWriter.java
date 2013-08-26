package xdi2.core.io.writers;

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
import xdi2.core.constants.XDIConstants;
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
import xdi2.core.xri3.XDI3XRef;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class XDIJSONPARSEWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONPARSEWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON/PARSE";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writeInner;
	private boolean writePretty;

	public XDIJSONPARSEWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writeInner = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_INNER, XDIWriterRegistry.DEFAULT_INNER));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));

		if (log.isTraceEnabled()) log.trace("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writeInner=" + this.writeInner + ", writePretty=" + this.writePretty);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

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

		JsonArray gom = makeGom(statements, this.writeInner);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.writePretty) jsonWriter.setIndent("  ");
		gson.toJson(gom, jsonWriter);
		writer.flush();

		return writer;
	}

	private static JsonArray makeGom(IterableIterator<Statement> statements, boolean writeInner) {

		JsonArray gom = new JsonArray();

		for (Statement statement : statements) {

			XDI3Statement statementXri = statement.getXri();

			// inner root short notation?

			if (writeInner) statementXri = transformStatementInInnerRoot(statementXri);

			// write the statement

			gom.add(makeGom(statementXri));
		}

		return gom;
	}

	private static JsonArray makeGom(XDI3Statement statement) {

		JsonArray gom = new JsonArray();

		gom.add(makeGom(statement.getSubject()));
		gom.add(makeGom(statement.getPredicate()));

		if (statement.getObject() instanceof XDI3Segment)
			gom.add(makeGom((XDI3Segment) statement.getObject()));
		else
			gom.add(new JsonPrimitive((String) statement.getObject()));

		return gom;
	}

	private static JsonElement makeGom(XDI3Segment segment) {

		JsonElement gom;

		if (segment.getNumSubSegments() == 1) {

			gom = makeGom(segment.getFirstSubSegment());
		} else {

			gom = new JsonArray();

			for (int i=0; i<segment.getNumSubSegments(); i++) ((JsonArray) gom).add(makeGom(segment.getSubSegment(i)));
		}

		return gom;
	}

	private static JsonElement makeGom(XDI3SubSegment subSegment) {

		JsonElement gom = null;
		
		if (subSegment.hasXRef()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(subSegment.getXRef().getXs(), makeGom(subSegment.getXRef()));
			gom = gom2;
		}

		if (subSegment.hasLiteral()) {

			gom = new JsonPrimitive(subSegment.getLiteral());
		}

		if (subSegment.hasCs()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(subSegment.getCs().toString(), gom);
			gom = gom2;
		}

		if (subSegment.isAttributeXs()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(XDIConstants.XS_ATTRIBUTE.substring(0, 1), gom);
			gom = gom2;
		}

		if (subSegment.isClassXs()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(XDIConstants.XS_CLASS.substring(0, 1), gom);
			gom = gom2;
		}

		return gom;
	}

	private static JsonElement makeGom(XDI3XRef xref) {

		if (xref.hasStatement()) {

			return makeGom(xref.getStatement());
		} else if (xref.hasPartialSubjectAndPredicate()) {

			JsonArray gom = new JsonArray();
			gom.add(makeGom(xref.getPartialSubject()));
			gom.add(makeGom(xref.getPartialPredicate()));
			return gom;
		} else if (xref.hasSegment()) {

			return makeGom(xref.getSegment());
		} else {

			return xref.getValue() == null ? new JsonPrimitive("") : new JsonPrimitive(xref.getValue());
		}
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
}
