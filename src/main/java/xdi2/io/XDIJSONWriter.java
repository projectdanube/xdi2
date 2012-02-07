/*******************************************************************************
 * Copyright (c) 2008 Parity Communications, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Sabadello - Initial API and implementation
 *******************************************************************************/
package xdi2.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;

class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	protected static final String FORMAT_TYPE = "XDI/JSON";
	protected static final String[] MIME_TYPES = new String[] { "application/xdi+json" };
	protected static final String DEFAULT_FILE_EXTENSION = "json";

	XDIJSONWriter() { }

	protected void writeContextNode(ContextNode contextNode, BufferedWriter bufferedWriter, Properties parameters, String indent) throws IOException {

		String xri = contextNode.getXri().toString();

		bufferedWriter.write(indent + "\"" + xri + "()/()\": [\n");
		for (Iterator<ContextNode> contextNodes = contextNode.getContextNodes(); contextNodes.hasNext(); ) {

			bufferedWriter.write(indent + "   \"" + contextNodes.next().getArcXri().toString() + "\"" + (contextNodes.hasNext() ? "," : "") + "\n");
		}

		for (Iterator<Relation> relations = contextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();
			bufferedWriter.write(indent + "\"" + xri + "/" + relation.getArcXri().toString() + "\" : [ \"" + relation.getRelationXri().toString() + "\" ]" + (relations.hasNext() ? "," : "") + "\n");
		}

		bufferedWriter.write(indent + "\"" + xri + "()/()\": [\n");
		for (Iterator<Literal> literals = contextNode.getLiterals(); literals.hasNext(); ) {

			Literal literal = literals.next();
			bufferedWriter.write(indent + "\"" + xri + "/" + literal.getArcXri().toString() + "\" : [ \"" + literal.getLiteralData() + "\" ]" + (literals.hasNext() ? "," : "") + "\n");
		}

		bufferedWriter.write(indent + "  ]");
	}

	public synchronized void write(Graph graph, BufferedWriter bufferedWriter, Properties parameters, String indent) throws IOException {

		bufferedWriter.write(indent + "{\n");

		this.writeContextNode(graph.getRootContextNode(), bufferedWriter, parameters, indent);

		bufferedWriter.write(indent + "}\n");

		bufferedWriter.flush();
	}

	public synchronized void write(Graph graph, Writer writer, Properties parameters) throws IOException {

		this.write(graph, new BufferedWriter(writer), parameters, "");
		writer.flush();
	}

	public synchronized void write(Graph graph, OutputStream stream, Properties parameters) throws IOException {

		this.write(graph, new OutputStreamWriter(stream), parameters);
		stream.flush();
	}

	public String getFormat() {

		return(FORMAT_TYPE);
	}

	public String[] getMimeTypes() {

		return(MIME_TYPES);
	}

	public String getDefaultFileExtension() {

		return(DEFAULT_FILE_EXTENSION);
	}
}
