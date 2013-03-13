package xdi2.core.io.writers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
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
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	private boolean writeImplied;
	private boolean writeInner;
	private boolean writePretty;

	public XDIJSONWriter(Properties parameters) {

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

	private void writeContextNode(Root root, ContextNode baseContextNode, BufferedWriter bufferedWriter, State state) throws IOException {

		// write context nodes
		
		Iterator<ContextNodeStatement> contextNodeStatements = new MappingContextNodeStatementIterator(baseContextNode.getContextNodes());

		// ignore implied context nodes

		if (! this.writeImplied) contextNodeStatements = new SelectingNotImpliedStatementIterator<ContextNodeStatement> (contextNodeStatements);

		// write them

		if (contextNodeStatements.hasNext()) {

			ContextNodeStatement contextNodeStatement = contextNodeStatements.next();
			XDI3Statement contextNodeStatementXri = StatementUtil.reduceStatement(contextNodeStatement.getXri(), root.getContextNode().getXri());
			if (contextNodeStatementXri == null) contextNodeStatementXri = contextNodeStatement.getXri();

			startItem(bufferedWriter, state);
			bufferedWriter.write("\"" + contextNodeStatementXri.getSubject() + "/()\":[");

			do {

				bufferedWriter.write("\"" + contextNodeStatementXri.getObject() + "\"" + (contextNodeStatements.hasNext() ? "," : ""));
			} while (contextNodeStatements.hasNext() && ((contextNodeStatement = contextNodeStatements.next()) != null));

			bufferedWriter.write("]");
			finishItem(bufferedWriter, state);
		}

		// recursively write context node contents

		Iterator<ContextNode> contextNodes = baseContextNode.getContextNodes();

		while (contextNodes.hasNext()) {

			ContextNode contextNode = contextNodes.next();

			// inner root short notation?

			if (this.writeInner && InnerRoot.isValid(contextNode)) {

				root = InnerRoot.fromContextNode(contextNode);
			}

			this.writeContextNode(root, contextNode, bufferedWriter, state);
		}

		// write relations

		Map<XDI3Segment, List<Relation>> relationsMap = new HashMap<XDI3Segment, List<Relation>> ();

		for (Iterator<Relation> relations = baseContextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();

			List<Relation> relationsList = relationsMap.get(relation.getArcXri());

			if (relationsList == null) {

				relationsList = new ArrayList<Relation> ();
				relationsMap.put(relation.getArcXri(), relationsList);
			}

			relationsList.add(relation);
		}

		for (Entry<XDI3Segment, List<Relation>> entry : relationsMap.entrySet()) {

			XDI3Segment relationArcXri = entry.getKey();
			List<Relation> relationsList = entry.getValue();

			Iterator<RelationStatement> relationStatements = new MappingRelationStatementIterator(relationsList.iterator());

			RelationStatement relationStatement = relationStatements.next();
			XDI3Statement relationStatementXri = StatementUtil.reduceStatement(relationStatement.getXri(), root.getContextNode().getXri());
			if (relationStatementXri == null) relationStatementXri = relationStatement.getXri();

			startItem(bufferedWriter, state);
			bufferedWriter.write("\"" + relationStatementXri.getSubject() + "/" + relationArcXri + "\":[");

			do {

				bufferedWriter.write("\"" + relationStatementXri.getObject() + "\"" + (relationStatements.hasNext() ? "," : ""));
			} while (relationStatements.hasNext() && ((relationStatement = relationStatements.next()) != null));

			bufferedWriter.write("]");
			finishItem(bufferedWriter, state);
		}

		// write literal

		Literal literal = baseContextNode.getLiteral();
		LiteralStatement literalStatement = literal == null ? null : literal.getStatement();

		if (literal != null && literalStatement != null) {

			startItem(bufferedWriter, state);
			bufferedWriter.write("\"" + literalStatement.getSubject() + "/!\":[" + (literal.getLiteralData() == null ? "" : JSON.toJSONString(literal.getLiteralData())) + "]");
			finishItem(bufferedWriter, state);
		}
	}

	private void write(Graph graph, BufferedWriter bufferedWriter) throws IOException {

		State state = new State();

		Root root = Roots.findLocalRoot(graph);

		startGraph(bufferedWriter, state);
		this.writeContextNode(root, graph.getRootContextNode(), bufferedWriter, state);
		finishGraph(bufferedWriter, state);

		bufferedWriter.flush();
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		if (this.writePretty) {

			try {

				StringWriter stringWriter = new StringWriter();
				this.write(graph, new BufferedWriter(stringWriter));

				JSONObject json = JSON.parseObject(stringWriter.toString());
				writer.write(JSON.toJSONString(json, this.writePretty));
			} catch (JSONException ex) {

				throw new IOException("Problem while constructing JSON object: " + ex.getMessage(), ex);
			}
		} else {

			this.write(graph, new BufferedWriter(writer));
		}

		writer.flush();

		return writer;
	}

	private static void startItem(BufferedWriter bufferedWriter, State state) throws IOException {

		if (state.first) {

			state.first = false;
		} else {

			bufferedWriter.write(",");
		}
	}

	private static void finishItem(BufferedWriter bufferedWriter, State state) throws IOException {

	}

	private static void startGraph(BufferedWriter bufferedWriter, State state) throws IOException {

		state.first = true;

		bufferedWriter.write("{");
	}

	private static void finishGraph(BufferedWriter bufferedWriter, State state) throws IOException {

		bufferedWriter.write("}");
	}

	private static class State {

		private boolean first;

		private State() {

			this.first = true;
		}
	}
}
