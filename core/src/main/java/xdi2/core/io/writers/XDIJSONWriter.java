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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

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

		if (log.isDebugEnabled()) log.debug("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writeInner=" + this.writeInner + ", writePretty=" + this.writePretty);
	}

	private void writeInternal(Graph graph, JSONObject jsonObject) throws IOException {

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

			this.putStatementIntoJSONObject(statementXri, jsonObject);
		}
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		JSONObject jsonObject = new JSONObject();

		this.writeInternal(graph, jsonObject);

		writer.write(JSON.toJSONString(jsonObject, this.writePretty));
		writer.flush();

		return writer;
	}

	private void putStatementIntoJSONObject(XDI3Statement statementXri, JSONObject jsonObject) throws IOException {

		// inner root short notation?

		if (this.writeInner) if (this.tryPutStatementIntoInnerJSONObject(statementXri, jsonObject)) return;

		// find the JSON array

		String key = statementXri.getSubject() + "/" + statementXri.getPredicate();

		JSONArray jsonArray = jsonObject.getJSONArray(key);

		if (jsonArray == null) {

			jsonArray = new JSONArray();
			jsonObject.put(key, jsonArray);
		}

		addObjectToJSONArray(statementXri, jsonArray);
	}

	private boolean tryPutStatementIntoInnerJSONObject(XDI3Statement statementXri, JSONObject jsonObject) throws IOException {

		XDI3SubSegment subjectFirstSubSegment = statementXri.getSubject().getFirstSubSegment();

		if ((! subjectFirstSubSegment.hasXRef()) || (! subjectFirstSubSegment.getXRef().hasPartialSubjectAndPredicate())) return false;

		XDI3Segment innerRootSubject = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialSubject();
		XDI3Segment innerRootPredicate = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialPredicate();

		XDI3Statement reducedStatementXri = StatementUtil.reduceStatement(statementXri, XDI3Segment.create("" + subjectFirstSubSegment));
		if (reducedStatementXri == null) return false;

		// find the inner root JSON array

		String innerRootKey = "" + innerRootSubject + "/" + innerRootPredicate;

		JSONArray innerRootJsonArray = jsonObject.getJSONArray(innerRootKey);

		if (innerRootJsonArray == null) {

			innerRootJsonArray = new JSONArray();
			jsonObject.put(innerRootKey, innerRootJsonArray);
		}

		// find the inner root JSON object

		JSONObject innerRootJsonObject = findJSONObjectInJSONArray(innerRootJsonArray);

		if (innerRootJsonObject == null) {

			innerRootJsonObject = new JSONObject();
			innerRootJsonArray.add(innerRootJsonObject);
		}
		
		// put the statement into the inner root JSON object

		this.putStatementIntoJSONObject(reducedStatementXri, innerRootJsonObject);
		
		// done
		
		return true;
	}

	private static JSONObject findJSONObjectInJSONArray(JSONArray jsonArray) {

		for (Iterator<Object> objects = jsonArray.iterator(); objects.hasNext(); ) {

			Object object = objects.next();

			if (object instanceof JSONObject) return (JSONObject) object;
		}

		return null;
	}

	private static void addObjectToJSONArray(XDI3Statement statementXri, JSONArray jsonArray) throws IOException {

		if (statementXri.isLiteralStatement()) {

			String literalData = statementXri.getLiteralData(); 

			if (literalData != null) {

				jsonArray.add(literalData);
			} else {

			}
		} else {

			jsonArray.add(statementXri.getObject().toString());
		}
	}
}
