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

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.xri3.impl.XRI3Reference;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3XRef;

public class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	private boolean writeContexts;

	private int prettyIndent;

	public XDIJSONWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeContexts = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_CONTEXTS, XDIWriterRegistry.DEFAULT_CONTEXTS));

		try {

			this.prettyIndent = Integer.parseInt(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));
		} catch (NumberFormatException nfe) {

			this.prettyIndent = Integer.parseInt(XDIWriterRegistry.DEFAULT_PRETTY);
		}

		log.debug("Parameters: writeContexts=" + this.writeContexts + ", prettyIndent=" + this.prettyIndent);
	}

	private void writeContextNode(ContextNode contextNode, BufferedWriter bufferedWriter, State state) throws IOException {

		String xri = contextNode.getXri().toString();

		// write context nodes

		if (contextNode.containsContextNodes()) {

			Iterator<ContextNode> needWriteContextStatements;

			if (this.writeContexts) {

				needWriteContextStatements = contextNode.getContextNodes();
			} else {

				// ignore implied context nodes

				needWriteContextStatements = new SelectingIterator<ContextNode> (contextNode.getContextNodes()) {

					@Override
					public boolean select(ContextNode contextNode) {

						return ! StatementUtil.isImplied(contextNode.getStatement());
					}
				};
			}

			if (needWriteContextStatements.hasNext()) {

				startItem(bufferedWriter, state);
				bufferedWriter.write("\"" + xri + "/()\":[");
				for (; needWriteContextStatements.hasNext(); ) {

					ContextNode innerContextNode = needWriteContextStatements.next();
					bufferedWriter.write("\"" + innerContextNode.getArcXri().toString() + "\"" + (needWriteContextStatements.hasNext() ? "," : ""));
				}
				bufferedWriter.write("]");
				finishItem(bufferedWriter, state);
			}

			for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

				ContextNode innerContextNode = innerContextNodes.next();
				this.writeContextNode(innerContextNode, bufferedWriter, state);
			}
		}

		// write relations

		Map<XRI3Segment, List<Relation>> relationsMap = new HashMap<XRI3Segment, List<Relation>> ();

		for (Iterator<Relation> relations = contextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();

			List<Relation> relationsList = relationsMap.get(relation.getArcXri());

			if (relationsList == null) {

				relationsList = new ArrayList<Relation> ();
				relationsMap.put(relation.getArcXri(), relationsList);
			}

			relationsList.add(relation);
		}

		for (Entry<XRI3Segment, List<Relation>> entry : relationsMap.entrySet()) {

			XRI3Segment relationArcXri = entry.getKey();
			List<Relation> relationsList = entry.getValue();

			startItem(bufferedWriter, state);
			bufferedWriter.write("\"" + xri + "/" + relationArcXri + "\":[");

			Graph tempGraph = null;

			boolean missingTrailingComma = false;

			for (int i = 0; i < relationsList.size(); i++) {

				XRI3Segment targetContextNodeXri = relationsList.get(i).getTargetContextNodeXri();
				XRI3XRef xref = (XRI3XRef) targetContextNodeXri.getFirstSubSegment().getXRef();

				XRI3Reference xriXref = xref == null ? null : (XRI3Reference) xref.getXRIReference();

				Statement statement = null;

				if (xriXref != null) {

					try {

						statement = StatementUtil.fromString(xriXref.toString());
					} catch (Xdi2ParseException ex) {

					}
				}

				// if the target context node XRI is a valid statement in a cross-reference, add it to the temporary graph
				if (statement != null) {

					if (tempGraph == null) {
						tempGraph = MemoryGraphFactory.getInstance().openGraph();
					}

					tempGraph.addStatement(statement);
				} else {

					bufferedWriter.write("\"" + targetContextNodeXri + "\"");

					if (i < relationsList.size() - 1) {

						bufferedWriter.write(",");
					} else {

						missingTrailingComma = true;
					}
				}
			}

			if (tempGraph != null) {

				if (missingTrailingComma) bufferedWriter.write(",");

				// write the temporary graph recursively
				this.write(tempGraph, bufferedWriter);
			}

			bufferedWriter.write("]");
			finishItem(bufferedWriter, state);
		}

		// write literal

		Literal literal = contextNode.getLiteral();

		if (literal != null) {

			startItem(bufferedWriter, state);
			bufferedWriter.write("\"" + xri + "/!\":[\"" + escape(literal.getLiteralData()) + "\"]");
			finishItem(bufferedWriter, state);
		}
	}

	private void write(Graph graph, BufferedWriter bufferedWriter) throws IOException {

		State state = new State();

		startGraph(bufferedWriter, state);
		this.writeContextNode(graph.getRootContextNode(), bufferedWriter, state);
		finishGraph(bufferedWriter, state);

		bufferedWriter.flush();
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		if (this.prettyIndent > 0) {

			try {

				StringWriter stringWriter = new StringWriter();
				this.write(graph, new BufferedWriter(stringWriter));

				JSONObject json = new JSONObject(stringWriter.toString());
				writer.write(json.toString(this.prettyIndent));
			} catch (JSONException ex) {

				throw new IOException("Problem while constructing JSON object: " + ex.getMessage(), ex);
			}
		} else {

			this.write(graph, new BufferedWriter(writer));
		}

		writer.flush();

		return writer;
	}

	private static String escape(String string) {

		return string
				.replace("\\", "\\\\")
				.replace("\"", "\\\"");
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
