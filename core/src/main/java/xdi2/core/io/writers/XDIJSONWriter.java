package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIStatementUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingContextNodeStatementIterator;
import xdi2.core.util.iterators.MappingLiteralStatementIterator;
import xdi2.core.util.iterators.MappingRelationStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class XDIJSONWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -5510592554616900152L;

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String FILE_EXTENSION = "json";
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+json");

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJSONWriter(Properties parameters) {

		super(parameters);
	}

	private void writeInternal(Graph graph, JsonObject jsonObject) throws IOException {

		// write ordered?

		Graph orderedGraph = null;
		IterableIterator<Statement> statements;

		if (this.isWriteOrdered()) {

			MemoryGraphFactory memoryGraphFactory = new MemoryGraphFactory();
			memoryGraphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ALPHA);
			orderedGraph = memoryGraphFactory.openGraph();
			CopyUtil.copyGraph(graph, orderedGraph, null);

			List<Iterator<? extends Statement>> list = new ArrayList<Iterator<? extends Statement>> ();
			list.add(new MappingContextNodeStatementIterator(orderedGraph.getRootContextNode(true).getAllContextNodes()));
			list.add(new MappingRelationStatementIterator(orderedGraph.getRootContextNode(true).getAllRelations()));
			list.add(new MappingLiteralStatementIterator(orderedGraph.getRootContextNode(true).getAllLiterals()));

			statements = new CompositeIterator<Statement> (list.iterator());
		} else {

			statements = graph.getRootContextNode(true).getAllStatements();
		}

		// ignore implied statements

		if (! this.isWriteImplied()) statements = new SelectingNotImpliedStatementIterator(statements);

		// write the statements

		for (Statement statement : statements) {

			XDIStatement XDIstatement = statement.getXDIStatement();

			// put the statement into the JSON object

			this.putStatementIntoJsonObject(XDIstatement, jsonObject);
		}

		// done

		if (orderedGraph != null) orderedGraph.close();
	}

	@SuppressWarnings("resource")
	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write

		JsonObject jsonObject = new JsonObject();

		this.writeInternal(graph, jsonObject);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.isWritePretty()) jsonWriter.setIndent("  ");
		gson.toJson(jsonObject, jsonWriter);
		jsonWriter.flush();
		writer.flush();

		return writer;
	}

	private void putStatementIntoJsonObject(XDIStatement XDIstatement, JsonObject jsonObject) throws IOException {

		// nested JSON object?

		if (this.tryPutStatementIntoInnerJsonObject(XDIstatement, jsonObject)) return;

		// add the object

		String key = XDIstatement.getSubject() + "/" + XDIstatement.getPredicate();

		addObjectToJsonObject(XDIstatement, jsonObject, key);
	}

	private boolean tryPutStatementIntoInnerJsonObject(XDIStatement XDIstatement, JsonObject jsonObject) throws IOException {

		XDIArc subjectFirstArc = XDIstatement.getSubject().getFirstXDIArc();

		if (subjectFirstArc == null || (! subjectFirstArc.hasXRef()) || (! subjectFirstArc.getXRef().hasPartialSubjectAndPredicate())) return false;

		XDIAddress innerRootSubject = XDIstatement.getSubject().getFirstXDIArc().getXRef().getPartialSubject();
		XDIAddress innerRootPredicate = XDIstatement.getSubject().getFirstXDIArc().getXRef().getPartialPredicate();

		XDIStatement reducedStatementAddress = XDIStatementUtil.removeStartXDIStatement(XDIstatement, XDIAddress.fromComponent(subjectFirstArc));
		if (reducedStatementAddress == null) return false;

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

		this.putStatementIntoJsonObject(reducedStatementAddress, innerRootJsonObject);

		// done

		return true;
	}

	private static JsonObject findJsonObjectInJsonArray(JsonArray jsonArray) {

		for (JsonElement jsonElement : jsonArray) {

			if (jsonElement instanceof JsonObject) return (JsonObject) jsonElement;
		}

		return null;
	}

	private static void addObjectToJsonObject(XDIStatement XDIstatement, JsonObject jsonObject, String key) {

		if (XDIstatement.isLiteralStatement()) {

			Object literalData = XDIstatement.getLiteralData(); 

			jsonObject.add(key, AbstractLiteral.literalDataToJsonElement(literalData));
		} else {

			JsonArray jsonArray = (JsonArray) jsonObject.get(key);

			if (jsonArray == null) {

				jsonArray = new JsonArray();
				jsonObject.add(key, jsonArray);
			}

			jsonArray.add(new JsonPrimitive(XDIstatement.getObject().toString()));
		}
	}
}
