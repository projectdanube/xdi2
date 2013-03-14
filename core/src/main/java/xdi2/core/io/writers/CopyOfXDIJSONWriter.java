package xdi2.core.io.writers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.features.roots.InnerRoot;
import xdi2.core.features.roots.Root;
import xdi2.core.features.roots.Roots;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.util.iterators.MappingStatementXriIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class CopyOfXDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(CopyOfXDIJSONWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	private boolean writeImplied;
	private boolean writeInner;
	private boolean writePretty;

	public CopyOfXDIJSONWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeInner = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_INNER, XDIWriterRegistry.DEFAULT_INNER));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));

		if (log.isDebugEnabled()) log.debug("Parameters: writeImplied=" + this.writeImplied + ", writeInner=" + this.writeInner + ", writePretty=" + this.writePretty);
	}

	private void writeInternal(Root root, ContextNode baseContextNode, Writer writer, State state) throws IOException {

		// recursively write context node contents

		Map<XDI3SubSegment, String> innerRootContentsMap = new HashMap<XDI3SubSegment, String> ();
		
		for (ContextNode contextNode : baseContextNode.getContextNodes()) {

			// inner root short notation?

			if (this.writeInner && InnerRoot.isValid(contextNode)) {

				continue;
			} else {

				this.writeInternal(root, contextNode, writer, state);
			}
		}

		// context node statements

		Iterator<ContextNodeStatement> contextNodeStatements = new MappingContextNodeStatementIterator(baseContextNode.getContextNodes());

		// ignore implied context nodes

		if (! this.writeImplied) contextNodeStatements = new SelectingNotImpliedStatementIterator<ContextNodeStatement> (contextNodeStatements);

		// relation statements

		Iterator<RelationStatement> relationStatements = new MappingRelationStatementIterator(baseContextNode.getRelations());

		// relations pointing to an inner root?

		for (Relation relation : baseContextNode.getRelations()) {
/*
 * 
 * TODO BROKEN NEEDS WORK
 * 
			State innerState = new State();
			Root innerRoot = InnerRoot.fromContextNode(contextNode);
			StringWriter innerWriter = new StringWriter();

			startGraph(innerWriter, innerState);
			this.writeInternal(innerRoot, contextNode, innerWriter, innerState);
			finishGraph(innerWriter, innerState);
			
			innerRootContentsMap.put(innerRoot.getContextNode().getArcXri(), innerWriter.getBuffer().toString());
			*/
		}
		
		// literal statement

		Iterator<LiteralStatement> literalStatement = baseContextNode.containsLiteral() ? new SingleItemIterator<LiteralStatement> (baseContextNode.getLiteral().getStatement()) : new EmptyIterator<LiteralStatement> ();

		// write statements

		List<Iterator<? extends Statement>> list = new ArrayList<Iterator<? extends Statement>> ();
		list.add(contextNodeStatements);
		list.add(relationStatements);
		list.add(literalStatement);

		Iterator<XDI3Statement> statementXris = new MappingStatementXriIterator(new CompositeIterator<Statement> (list.iterator()));

		Map<XDI3Segment, StringWriter> stringWritersMap = new LinkedHashMap<XDI3Segment, StringWriter> ();

		while (statementXris.hasNext()) {

			XDI3Statement statementXri = statementXris.next();

			XDI3Statement reducedStatementXri = StatementUtil.reduceStatement(statementXri, root.getContextNode().getXri());
			if (reducedStatementXri != null) statementXri = reducedStatementXri;

			StringWriter stringWriter = stringWritersMap.get(statementXri.getPredicate());

			if (stringWriter == null) {

				stringWriter = new StringWriter();
				stringWritersMap.put(statementXri.getPredicate(), stringWriter);

				startItem(stringWriter, state);
				stringWriter.write("\"" + statementXri.getSubject() + "/" + statementXri.getPredicate() + "\":[");
			} else {

				stringWriter.write(", ");
			}

			writeObject(stringWriter, statementXri);
		}

		// write inner root contents
		
		for (Iterator<Map.Entry<XDI3SubSegment, String>> innerRootContents = innerRootContentsMap.entrySet().iterator(); innerRootContents.hasNext(); ) {
			
			Map.Entry<XDI3SubSegment, String> innerRootContent = innerRootContents.next();
			
			
		}
		
		// assemble all strings
		
		for (Iterator<StringWriter> stringWriters = stringWritersMap.values().iterator(); stringWriters.hasNext(); ) {

			StringWriter stringWriter = stringWriters.next();

			stringWriter.write("]");
			finishItem(stringWriter, state);

			writer.write(stringWriter.getBuffer().toString());
		}
	}

	private void writeInternal(Graph graph, Writer writer) throws IOException {

		State state = new State();
		Root root = Roots.findLocalRoot(graph);

		startGraph(writer, state);
		this.writeInternal(root, graph.getRootContextNode(), writer, state);
		finishGraph(writer, state);

		writer.flush();
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		if (this.writePretty) {

			try {

				StringWriter stringWriter = new StringWriter();
				this.writeInternal(graph, new BufferedWriter(stringWriter));

				JSONObject json = JSON.parseObject(stringWriter.toString());
				writer.write(JSON.toJSONString(json, this.writePretty));
			} catch (JSONException ex) {

				throw new IOException("Problem while constructing JSON object: " + ex.getMessage(), ex);
			}
		} else {

			this.writeInternal(graph, new BufferedWriter(writer));
		}

		writer.flush();

		return writer;
	}

	private static void writeObject(Writer writer, XDI3Statement statementXri) throws IOException {

		if (statementXri.isLiteralStatement()) {

			String literalData = statementXri.getLiteralData(); 

			if (literalData != null) writer.write("\"" + JSON.toJSONString(literalData) + "\"");
		} else {

			writer.write("\"" + statementXri.getObject() + "\"");
		}
	}

	private static void startItem(Writer writer, State state) throws IOException {

		if (state.first) {

			state.first = false;
		} else {

			writer.write(",");
		}
	}

	private static void finishItem(Writer writer, State state) throws IOException {

	}

	private static void startGraph(Writer writer, State state) throws IOException {

		state.first = true;

		writer.write("{");
	}

	private static void finishGraph(Writer writer, State state) throws IOException {

		writer.write("}");
	}

	private static class State {

		private boolean first;

		private State() {

			this.first = true;
		}
	}
}
