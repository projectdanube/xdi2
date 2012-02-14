package xdi2.core.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;

class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	protected static final String FORMAT_TYPE = "XDI/JSON";
	protected static final String[] MIME_TYPES = new String[] { "application/xdi+json" };
	protected static final String DEFAULT_FILE_EXTENSION = "json";

	XDIJSONWriter() { }

	private boolean first;

	private static String escape(String string) {
		
		return string.replace("\\", "\\\\");
	}
	
	private synchronized void startItem(BufferedWriter bufferedWriter) throws IOException {

		if (this.first) {

			this.first = false;
		} else {

			bufferedWriter.write(",\n");
		}
	}

	private synchronized void finishItem(BufferedWriter bufferedWriter) throws IOException {

	}

	private synchronized void startGraph(BufferedWriter bufferedWriter) throws IOException {

		this.first = true;

		bufferedWriter.write("{\n");
	}

	private synchronized void finishGraph(BufferedWriter bufferedWriter) throws IOException {

		bufferedWriter.write("\n");
		bufferedWriter.write("}\n");

		this.first = true;
	}

	private synchronized void writeContextNode(ContextNode contextNode, BufferedWriter bufferedWriter, Properties parameters, String indent) throws IOException {

		String xri = contextNode.getXri().toString();

		// write context nodes
		
		if (contextNode.containsContextNodes()) {

			this.startItem(bufferedWriter);
			bufferedWriter.write(indent + "\"" + xri + "/()\": [\n");
			for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

				ContextNode innerContextNode = innerContextNodes.next();
				bufferedWriter.write(indent + "   \"" + innerContextNode.getArcXri().toString() + "\"" + (innerContextNodes.hasNext() ? "," : "") + "\n");
			}
			bufferedWriter.write(indent + "]");
			this.finishItem(bufferedWriter);

			for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

				ContextNode innerContextNode = innerContextNodes.next();
				this.writeContextNode(innerContextNode, bufferedWriter, parameters, indent);
			}
		}

		// write relations
		
		for (Iterator<Relation> relations = contextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();

			this.startItem(bufferedWriter);
			bufferedWriter.write(indent + "\"" + xri + "/" + relation.getArcXri().toString() + "\" : [ \"" + relation.getRelationXri().toString() + "\" ]");
			this.finishItem(bufferedWriter);
		}

		// write literal
		
		Literal literal = contextNode.getLiteral();

		if (literal != null) {

			this.startItem(bufferedWriter);
			bufferedWriter.write(indent + "\"" + xri + "/!\" : [ \"" + escape(literal.getLiteralData()) + "\" ]");
			this.finishItem(bufferedWriter);
		}
	}

	public synchronized void write(Graph graph, BufferedWriter bufferedWriter, Properties parameters, String indent) throws IOException {

		this.startGraph(bufferedWriter);
		this.writeContextNode(graph.getRootContextNode(), bufferedWriter, parameters, indent);
		this.finishGraph(bufferedWriter);

		bufferedWriter.flush();
	}

	public synchronized Writer write(Graph graph, Writer writer, Properties parameters) throws IOException {

		this.write(graph, new BufferedWriter(writer), parameters, "");
		writer.flush();

		return writer;
	}

	public String getFormat() {

		return FORMAT_TYPE;
	}

	public String[] getMimeTypes() {

		return MIME_TYPES;
	}

	public String getDefaultFileExtension() {

		return DEFAULT_FILE_EXTENSION;
	}
}
