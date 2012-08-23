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
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.AbstractStatement;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3XRef;

public class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	private boolean writeContexts;

	public XDIJSONWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeContexts = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_CONTEXTS, XDIWriterRegistry.DEFAULT_CONTEXTS));

		log.debug("Parameters: writeContexts=" + this.writeContexts);
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

						if (! contextNode.isEmpty()) return false;
						if (contextNode.getIncomingRelations().hasNext()) return false;

						return true;
					}
				};
			}

			if (needWriteContextStatements.hasNext()) {

				startItem(bufferedWriter, state);
				bufferedWriter.write(state.indent + "\"" + xri + "/()\": [\n");
				for (; needWriteContextStatements.hasNext(); ) {

					ContextNode innerContextNode = needWriteContextStatements.next();
					bufferedWriter.write(state.indent + "   \"" + innerContextNode.getArcXri().toString() + "\"" + (needWriteContextStatements.hasNext() ? "," : "") + "\n");
				}
				bufferedWriter.write(state.indent + "]");
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
            bufferedWriter.write(state.indent + "\"" + xri + "/" + relationArcXri + "\" : [ ");

            Graph tempGraph = MemoryGraphFactory.getInstance().openGraph();
           
            for (int i=0; i<relationsList.size(); i++) {
            	
                XRI3Segment targetContextNodeXri = relationsList.get(i).getTargetContextNodeXri();
                XRI3XRef xref = (XRI3XRef) targetContextNodeXri.getFirstSubSegment().getXRef();
                
                Statement statement = null;
                
                if (xref != null) {
                	
                    try {
                    	
                        statement = AbstractStatement.fromString(xref.getXRIReference().toString());
                    } catch (Xdi2ParseException ex) {
                       
                    }
                }
                
                // if the target context node XRI is a valid statement in a cross-reference, add it to the temporary graph
                if (statement != null) {
                	
                    tempGraph.addStatement(statement);
                } else {
                	
                	tempGraph = null;
                    bufferedWriter.write("\"" + targetContextNodeXri + "\"");
                    if (i < relationsList.size() - 1) bufferedWriter.write(", ");
                }
            }

            if (tempGraph != null) {

            	// write the temporary graph recursively
            	this.write(tempGraph, bufferedWriter);
            }

            bufferedWriter.write(" ]");
            finishItem(bufferedWriter, state);
        }

		// write literal

		Literal literal = contextNode.getLiteral();

		if (literal != null) {

			startItem(bufferedWriter, state);
			bufferedWriter.write(state.indent + "\"" + xri + "/!\" : [ \"" + escape(literal.getLiteralData()) + "\" ]");
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

		this.write(graph, new BufferedWriter(writer));
		writer.flush();

		return writer;
	}

	private static String escape(String string) {

		return string.replace("\\", "\\\\");
	}

	private static void startItem(BufferedWriter bufferedWriter, State state) throws IOException {

		if (state.first) {
			
			state.first = false;
		} else {

			bufferedWriter.write(",\n");
		}
	}

	private static void finishItem(BufferedWriter bufferedWriter, State state) throws IOException {

	}

	private static void startGraph(BufferedWriter bufferedWriter, State state) throws IOException {

		state.first = true;

		bufferedWriter.write("{\n");
	}

	private static void finishGraph(BufferedWriter bufferedWriter, State state) throws IOException {

		bufferedWriter.write("\n");
		bufferedWriter.write("}\n");
	}

	private static class State {

		private boolean first;
		private String indent;

		private State() {

			this.first = true;
			this.indent = "";
		}
	}
}
