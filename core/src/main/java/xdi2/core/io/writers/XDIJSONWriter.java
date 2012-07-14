package xdi2.core.io.writers;

import java.io.BufferedWriter;
import java.io.IOException;
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
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.xri3.impl.XRI3Segment;

public class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType[] MIME_TYPES = new MimeType[] { new MimeType("application/xdi+json"), new MimeType("application/xdi+json;contexts=0") };

	public static final String PARAMETER_WRITE_CONTEXT_STATEMENTS = "writeContextStatements";
	public static final String DEFAULT_WRITE_CONTEXT_STATEMENTS = "false";

	private static class State {

		private boolean first;
		private String indent;

		private State() {

			this.first = true;
			this.indent = "";
		}
	}

	private static String escape(String string) {

		return string.replace("\\", "\\\\");
	}

	private void startItem(BufferedWriter bufferedWriter, State state) throws IOException {

		if (state.first) {

			state.first = false;
		} else {

			bufferedWriter.write(",\n");
		}
	}

	private void finishItem(BufferedWriter bufferedWriter, State state) throws IOException {

	}

	private void startGraph(BufferedWriter bufferedWriter, State state) throws IOException {

		state.first = true;

		bufferedWriter.write("{\n");
	}

	private void finishGraph(BufferedWriter bufferedWriter, State state) throws IOException {

		bufferedWriter.write("\n");
		bufferedWriter.write("}\n");
	}

	private void writeContextNode(ContextNode contextNode, BufferedWriter bufferedWriter, boolean writeContextStatements, State state) throws IOException {

		String xri = contextNode.getXri().toString();

		// write context nodes

		if (contextNode.containsContextNodes()) {

			Iterator<ContextNode> needWriteContextStatements;

			if (writeContextStatements) {

				needWriteContextStatements = contextNode.getContextNodes();
			} else {

				needWriteContextStatements = new SelectingIterator<ContextNode> (contextNode.getContextNodes()) {

					@Override
					public boolean select(ContextNode item) {

						return item.isEmpty();
					}
				};
			}

			if (needWriteContextStatements.hasNext()) {

				this.startItem(bufferedWriter, state);
				bufferedWriter.write(state.indent + "\"" + xri + "/()\": [\n");
				for (; needWriteContextStatements.hasNext(); ) {

					ContextNode innerContextNode = needWriteContextStatements.next();
					bufferedWriter.write(state.indent + "   \"" + innerContextNode.getArcXri().toString() + "\"" + (needWriteContextStatements.hasNext() ? "," : "") + "\n");
				}
				bufferedWriter.write(state.indent + "]");
				this.finishItem(bufferedWriter, state);
			}

			for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

				ContextNode innerContextNode = innerContextNodes.next();
				this.writeContextNode(innerContextNode, bufferedWriter, writeContextStatements, state);
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

			this.startItem(bufferedWriter, state);
			bufferedWriter.write(state.indent + "\"" + xri + "/" + relationArcXri + "\" : [ ");

			for (int i=0; i<relationsList.size(); i++) {

				bufferedWriter.write("\"" + relationsList.get(i).getRelationXri() + "\"");
				if (i < relationsList.size() - 1) bufferedWriter.write(", ");
			}

			bufferedWriter.write(" ]");
			this.finishItem(bufferedWriter, state);
		}

		// write literal

		Literal literal = contextNode.getLiteral();

		if (literal != null) {

			this.startItem(bufferedWriter, state);
			bufferedWriter.write(state.indent + "\"" + xri + "/!\" : [ \"" + escape(literal.getLiteralData()) + "\" ]");
			this.finishItem(bufferedWriter, state);
		}
	}

	private void write(Graph graph, BufferedWriter bufferedWriter, boolean writeContextStatements) throws IOException {

		State state = new State();

		this.startGraph(bufferedWriter, state);
		this.writeContextNode(graph.getRootContextNode(), bufferedWriter, writeContextStatements, state);
		this.finishGraph(bufferedWriter, state);

		bufferedWriter.flush();
	}

	public Writer write(Graph graph, Writer writer, Properties parameters) throws IOException {

		// check parameters
		
		if (parameters == null) parameters = new Properties();

		boolean writeContextStatements = Boolean.parseBoolean(parameters.getProperty(PARAMETER_WRITE_CONTEXT_STATEMENTS, DEFAULT_WRITE_CONTEXT_STATEMENTS));

		log.debug("Parameters: writeContextStatements=" + writeContextStatements);

		// write
		
		this.write(graph, new BufferedWriter(writer), writeContextStatements);
		writer.flush();

		return writer;
	}
}
