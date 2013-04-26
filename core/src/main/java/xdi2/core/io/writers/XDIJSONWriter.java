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
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writeInner;
	private boolean writePretty;

	public XDIJSONWriter(Properties parameters) {

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

	private void writeInternal(Graph graph, JsonObject jsonObject) throws IOException {

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

			XDI3Statement statementXri = statement.getXri();

			// put the statement into the JSON object

			this.putStatementIntoJsonObject(statementXri, jsonObject);
		}
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		JsonObject jsonObject = new JsonObject();

		this.writeInternal(graph, jsonObject);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.writePretty) jsonWriter.setIndent("  ");
		gson.toJson(jsonObject, jsonWriter);
		writer.flush();

		return writer;
	}

	private void putStatementIntoJsonObject(XDI3Statement statementXri, JsonObject jsonObject) throws IOException {

		// inner root short notation?

		if (this.writeInner) if (this.tryPutStatementIntoInnerJsonObject(statementXri, jsonObject)) return;

		// add the object

		String key = statementXri.getSubject() + "/" + statementXri.getPredicate();

		addObjectToJsonObject(statementXri, jsonObject, key);
	}

	private boolean tryPutStatementIntoInnerJsonObject(XDI3Statement statementXri, JsonObject jsonObject) throws IOException {

		XDI3SubSegment subjectFirstSubSegment = statementXri.getSubject().getFirstSubSegment();

		if ((! subjectFirstSubSegment.hasXRef()) || (! subjectFirstSubSegment.getXRef().hasPartialSubjectAndPredicate())) return false;

		XDI3Segment innerRootSubject = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialSubject();
		XDI3Segment innerRootPredicate = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialPredicate();

		XDI3Statement reducedStatementXri = StatementUtil.reduceStatement(statementXri, XDI3Segment.create("" + subjectFirstSubSegment));
		if (reducedStatementXri == null) return false;

		// find the inner root JSON array

		String innerRootKey = "" + innerRootSubject + "/" + innerRootPredicate;

		JsonArray innerRootJsonArray = (JsonArray) jsonObject.get(innerRootKey);

		if (innerRootJsonArray == null) {

			innerRootJsonArray = new JsonArray();
			jsonObject.add(innerRootKey, innerRootJsonArray);
		}

		// find the inner root JSON object

		JsonObject innerRootJsonObject = findJsonObjectInJsonArray(innerRootJsonArray);

		if (innerRootJsonObject == null) {

			innerRootJsonObject = new JsonObject();
			innerRootJsonArray.add(innerRootJsonObject);
		}

		// put the statement into the inner root JSON object

		this.putStatementIntoJsonObject(reducedStatementXri, innerRootJsonObject);

		// done

		return true;
	}

	private static JsonObject findJsonObjectInJsonArray(JsonArray jsonArray) {

		for (JsonElement jsonElement : jsonArray) {

			if (jsonElement instanceof JsonObject) return (JsonObject) jsonElement;
		}

		return null;
	}

	private static void addObjectToJsonObject(XDI3Statement statementXri, JsonObject jsonObject, String key) throws IOException {

		if (statementXri.isLiteralStatement()) {

			String literalData = statementXri.getLiteralData(); 

			jsonObject.add(key, new JsonPrimitive(literalData));
		} else {

			JsonArray jsonArray = (JsonArray) jsonObject.get(key);

			if (jsonArray == null) {

				jsonArray = new JsonArray();
				jsonObject.add(key, jsonArray);
			}

			jsonArray.add(new JsonPrimitive(statementXri.getObject().toString()));
		}
	}
}
