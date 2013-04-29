package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

public class XDIJSONTREEWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = -7518609157052712790L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONTREEWriter.class);

	public static final String FORMAT_NAME = "XDI/JSON/TREE";
	public static final String FILE_EXTENSION = null;
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writeInner;
	private boolean writePretty;

	public XDIJSONTREEWriter(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

		// check parameters

		this.writeImplied = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_IMPLIED, XDIWriterRegistry.DEFAULT_IMPLIED));
		this.writeOrdered = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_ORDERED, XDIWriterRegistry.DEFAULT_ORDERED));
		this.writeInner = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_INNER, XDIWriterRegistry.DEFAULT_INNER));
		this.writePretty = "1".equals(this.parameters.getProperty(XDIWriterRegistry.PARAMETER_PRETTY, XDIWriterRegistry.DEFAULT_PRETTY));

		if (log.isTraceEnabled()) log.trace("Parameters: writeImplied=" + this.writeImplied + ", writeOrdered=" + this.writeOrdered + ", writeInner=" + this.writeInner + ", writePretty=" + this.writePretty);
	}

	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write the statements

		JsonObject json = makeJson(graph.getRootContextNode(), this.writeImplied, this.writeInner);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.writePretty) jsonWriter.setIndent("  ");
		gson.toJson(json, jsonWriter);
		writer.flush();

		return writer;
	}

	private static JsonObject makeJson(ContextNode contextNode, boolean writeImplied, boolean writeInner) {

		JsonObject json = new JsonObject();

		// context nodes

		for (ContextNode innerContextNode : contextNode.getContextNodes()) {

			if (! writeImplied && innerContextNode.getStatement().isImplied() && innerContextNode.isEmpty()) continue;
			
			if (XDIConstants.CS_VALUE.equals(innerContextNode.getArcXri()) && innerContextNode.containsLiteral()) {

				json.add(XDIConstants.CS_VALUE.toString(), new JsonPrimitive(innerContextNode.getLiteral().getLiteralData()));
			} else {

				json.add(innerContextNode.getArcXri().toString(), makeJson(innerContextNode, writeImplied, writeInner));
			}
		}

		// relations

		for (Relation relation : contextNode.getRelations()) {

			if (! writeImplied && relation.getStatement().isImplied()) continue;
			
			JsonObject relationsJson = json.getAsJsonObject("/");
			if (relationsJson == null) { relationsJson = new JsonObject(); json.add("/", relationsJson); }

			JsonArray relationJson = relationsJson.getAsJsonArray(relation.getArcXri().toString());
			if (relationJson == null) { relationJson = new JsonArray(); relationsJson.add(relation.getArcXri().toString(), relationJson); }

			relationJson.add(new JsonPrimitive(relation.getTargetContextNodeXri().toString()));
		}

		// done

		return json;
	}

	private static JsonArray makeGom(XDI3Statement statement) {

		JsonArray gom = new JsonArray();

		gom.add(makeGom(statement.getSubject()));
		gom.add(makeGom(statement.getPredicate()));

		if (statement.getObject() instanceof XDI3Segment)
			gom.add(makeGom((XDI3Segment) statement.getObject()));
		else
			gom.add(new JsonPrimitive((String) statement.getObject()));

		return gom;
	}

	private static JsonElement makeGom(XDI3Segment segment) {

		JsonElement gom;

		if (segment.getNumSubSegments() == 1) {

			gom = makeGom(segment.getFirstSubSegment());
		} else {

			gom = new JsonArray();

			for (int i=0; i<segment.getNumSubSegments(); i++) ((JsonArray) gom).add(makeGom(segment.getSubSegment(i)));
		}

		return gom;
	}

	private static JsonElement makeGom(XDI3SubSegment subSegment) {

		JsonElement gom = null;

		if (subSegment.hasXRef()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(subSegment.getXRef().getXs(), makeGom(subSegment.getXRef()));
			gom = gom2;
		}

		if (subSegment.hasLiteral()) {

			gom = new JsonPrimitive(subSegment.getLiteral());
		}

		if (subSegment.hasCs()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(subSegment.getCs().toString(), gom);
			gom = gom2;
		}

		if (subSegment.isAttributeXs()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(XDIConstants.XS_ATTRIBUTE.substring(0, 1), gom);
			gom = gom2;
		}

		if (subSegment.isClassXs()) {

			JsonObject gom2 = new JsonObject();
			gom2.add(XDIConstants.XS_CLASS.substring(0, 1), gom);
			gom = gom2;
		}

		return gom;
	}

	private static JsonElement makeGom(XDI3XRef xref) {

		if (xref.hasStatement()) {

			return makeGom(xref.getStatement());
		} else if (xref.hasPartialSubjectAndPredicate()) {

			JsonArray gom = new JsonArray();
			gom.add(makeGom(xref.getPartialSubject()));
			gom.add(makeGom(xref.getPartialPredicate()));
			return gom;
		} else if (xref.hasSegment()) {

			return makeGom(xref.getSegment());
		} else {

			return xref.getValue() == null ? new JsonPrimitive("") : new JsonPrimitive(xref.getValue());
		}
	}

	private static XDI3Statement transformStatementInInnerRoot(XDI3Statement statementXri) {

		XDI3SubSegment subjectFirstSubSegment = statementXri.getSubject().getFirstSubSegment();

		if ((! subjectFirstSubSegment.hasXRef()) || (! subjectFirstSubSegment.getXRef().hasPartialSubjectAndPredicate())) return statementXri;

		XDI3Segment innerRootSubject = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialSubject();
		XDI3Segment innerRootPredicate = statementXri.getSubject().getFirstSubSegment().getXRef().getPartialPredicate();

		XDI3Statement reducedStatementXri = StatementUtil.reduceStatement(statementXri, XDI3Segment.create("" + subjectFirstSubSegment));
		if (reducedStatementXri == null) return statementXri;

		return XDI3Statement.create("" + innerRootSubject + "/" + innerRootPredicate + "/(" + transformStatementInInnerRoot(reducedStatementXri) + ")");
	}
}
