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
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class XDIJSONGOMWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONGOMWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON/GOM";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writeInner;
	private boolean writePretty;

	public XDIJSONGOMWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writeInner = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_INNER, XDIWriterRegistry.DEFAULT_INNER));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));

		if (log.isDebugEnabled()) log.debug("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writeInner=" + this.writeInner + ", writePretty=" + this.writePretty);
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

		JSONArray gom = makeGom(statements, this.writeInner);

		writer.write(JSON.toJSONString(gom, this.writePretty));
		writer.flush();

		return writer;
	}

	private static JSONArray makeGom(IterableIterator<Statement> statements, boolean writeInner) {

		JSONArray gom = new JSONArray();

		for (Statement statement : statements) {

			XDI3Statement statementXri = statement.getXri();

			// inner root short notation?

			if (writeInner) statementXri = transformStatementInInnerRoot(statementXri);

			// write the statement

			gom.add(makeGom(statementXri));
		}

		return gom;
	}

	private static JSONArray makeGom(XDI3Statement statement) {

		JSONArray gom = new JSONArray();

		gom.add(makeGom(statement.getSubject()));
		gom.add(makeGom(statement.getPredicate()));

		if (statement.getObject() instanceof XDI3Segment)
			gom.add(makeGom((XDI3Segment) statement.getObject()));
		else
			gom.add(statement.getObject());

		return gom;
	}

	private static Object makeGom(XDI3Segment segment) {

		Object gom;

		if (segment.getNumSubSegments() == 1) {

			gom = makeGom(segment.getFirstSubSegment());
		} else {

			gom = new JSONArray();

			for (int i=0; i<segment.getNumSubSegments(); i++) ((JSONArray) gom).add(makeGom(segment.getSubSegment(i)));
		}

		return gom;
	}

	private static Object makeGom(XDI3SubSegment subSegment) {

		Object gom = "";
		
		if (subSegment.hasXRef()) {

			JSONObject gom2 = new JSONObject();
			gom2.put(subSegment.getXRef().getXs(), makeGom(subSegment.getXRef()));
			gom = gom2;
		}

		if (subSegment.hasLiteral()) {

			gom = subSegment.getLiteral();
		}

		if (subSegment.hasCs()) {

			JSONObject gom2 = new JSONObject();
			gom2.put(subSegment.getCs().toString(), gom);
			gom = gom2;
		}

		if (subSegment.isAttribute()) {

			JSONObject gom2 = new JSONObject();
			gom2.put(XDI3Constants.XS_ATTRIBUTE, gom);
			gom = gom2;
		}

		if (subSegment.isSingleton()) {

			JSONObject gom2 = new JSONObject();
			gom2.put(XDI3Constants.XS_SINGLETON, gom);
			gom = gom2;
		}

		return gom;
	}

	private static Object makeGom(XDI3XRef xref) {

		if (xref.hasStatement()) {

			return makeGom(xref.getStatement());
		} else if (xref.hasPartialSubjectAndPredicate()) {

			JSONArray gom = new JSONArray();
			gom.add(makeGom(xref.getPartialSubject()));
			gom.add(makeGom(xref.getPartialPredicate()));
			return gom;
		} else if (xref.hasSegment()) {

			return makeGom(xref.getSegment());
		} else {

			return xref.getValue() == null ? "" : xref.getValue();
		}
	}

	private static XDI3Statement transformStatementInInnerRoot(XDI3Statement statementXri) {

		XDI3SubSegment subjectFirstSubSegment = statementXri.getSubject().getFirstSubSegment();

		if ((! subjectFirstSubSegment.hasXRef()) || (! subjectFirstSubSegment.getXRef().hasPartialSubjectAndPredicate())) return statementXri;

		XDI3Segment innerRootSubject = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialSubject();
		XDI3Segment innerRootPredicate = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialPredicate();

		XDI3Statement reducedStatementXri = StatementUtil.reduceStatement(statementXri, XDI3Segment.create("" + subjectFirstSubSegment));
		if (reducedStatementXri == null) return statementXri;

		return XDI3Statement.create("" + innerRootSubject + "/" + innerRootPredicate + "/(" + transformStatementInInnerRoot(reducedStatementXri) + ")");
	}
}
