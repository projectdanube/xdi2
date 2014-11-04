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
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralNodeStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class XDIJSONPARSEWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONPARSEWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON/PARSE";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writePretty;

	public XDIJSONPARSEWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));

		if (log.isTraceEnabled()) log.trace("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writePretty=" + this.writePretty);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write ordered?

		Graph orderedGraph = null;
		IterableIterator<Statement> statements;

		if (this.writeOrdered) {

			MemoryGraphFactory memoryGraphFactory = new MemoryGraphFactory();
			memoryGraphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ALPHA);
			orderedGraph = memoryGraphFactory.openGraph();
			CopyUtil.copyGraph(graph, orderedGraph, null);

			List<Iterator<? extends Statement>> list = new ArrayList<Iterator<? extends Statement>> ();
			list.add(new MappingContextNodeStatementIterator(orderedGraph.getRootContextNode(true).getAllContextNodes()));
			list.add(new MappingRelationStatementIterator(orderedGraph.getRootContextNode(true).getAllRelations()));
			list.add(new MappingLiteralNodeStatementIterator(orderedGraph.getRootContextNode(true).getAllLiterals()));

			statements = new CompositeIterator<Statement> (list.iterator());
		} else {

			statements = graph.getRootContextNode(true).getAllStatements();
		}

		// ignore implied statements

		if (! this.writeImplied) statements = new SelectingNotImpliedStatementIterator(statements);

		// write the statements

		JsonArray gom = makeGom(statements);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.writePretty) jsonWriter.setIndent("  ");
		gson.toJson(gom, jsonWriter);
		jsonWriter.flush();
		jsonWriter.close();
		writer.flush();

		// done

		if (orderedGraph != null) orderedGraph.close();

		return writer;
	}

	private static JsonArray makeGom(IterableIterator<Statement> statements) {

		JsonArray gom = new JsonArray();

		for (Statement statement : statements) {

			XDIStatement XDIstatement = statement.getXDIStatement();

			// write the statement

			gom.add(makeGom(XDIstatement));
		}

		return gom;
	}

	private static JsonArray makeGom(XDIStatement statement) {

		JsonArray gom = new JsonArray();

		gom.add(makeGom(statement.getSubject()));
		gom.add(makeGom(statement.getPredicate()));

		if (statement.getObject() instanceof XDIAddress)
			gom.add(makeGom((XDIAddress) statement.getObject()));
		else if (statement.getObject() instanceof XDIArc)
			gom.add(makeGom((XDIArc) statement.getObject()));
		else
			gom.add(AbstractLiteralNode.literalDataToJsonElement(statement.getObject()));

		return gom;
	}

	private static JsonElement makeGom(XDIAddress XDIaddress) {

		JsonElement gom;

		if (XDIaddress.getNumXDIArcs() == 1) {

			gom = makeGom(XDIaddress.getFirstXDIArc());
		} else {

			gom = new JsonArray();

			for (int i=0; i<XDIaddress.getNumXDIArcs(); i++) ((JsonArray) gom).add(makeGom(XDIaddress.getXDIArc(i)));
		}

		return gom;
	}

	private static JsonElement makeGom(XDIArc XDIarc) {

		JsonElement gom = null;

		if (XDIarc.hasXRef()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(XDIarc.getXRef().getXs(), makeGom(XDIarc.getXRef()));
			gom = gom2;
		}

		if (XDIarc.hasLiteralNode()) {

			gom = new JsonPrimitive(XDIarc.getLiteralNode());
		}

		if (XDIarc.hasCs()) {

			if (gom != null) {

				JsonObject gom2 = new JsonObject();
				gom2.add(XDIarc.getCs().toString(), gom);
				gom = gom2;
			} else {

				gom = new JsonPrimitive(XDIarc.getCs().toString());
			}
		}

		if (XDIarc.isAttributeXs()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(XDIConstants.XS_ATTRIBUTE.substring(0, 1), gom);
			gom = gom2;
		}

		if (XDIarc.isClassXs()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(XDIConstants.XS_CLASS.substring(0, 1), gom);
			gom = gom2;
		}

		return gom;
	}

	private static JsonElement makeGom(XDIXRef xref) {

		if (xref.hasPartialSubjectAndPredicate()) {

			JsonArray gom = new JsonArray();
			gom.add(makeGom(xref.getPartialSubject()));
			gom.add(makeGom(xref.getPartialPredicate()));
			return gom;
		} else if (xref.hasXDIAddress()) {

			return makeGom(xref.getXDIAddress());
		} else {

			return xref.getValue() == null ? new JsonPrimitive("") : new JsonPrimitive(xref.getValue());
		}
	}
}
