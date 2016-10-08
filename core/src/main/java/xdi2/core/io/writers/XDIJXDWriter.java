package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.util.JXDConstants;
import xdi2.core.io.util.JXDMapping;
import xdi2.core.io.util.JXDMapping.JXDTerm;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIAddressUtil;

public class XDIJXDWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = 1077789049204778292L;

	private static final Logger log = LoggerFactory.getLogger(XDIJXDWriter.class);

	public static final String FORMAT_NAME = "JXD";
	public static final String FILE_EXTENSION = "jxd";
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	private boolean writeImplied;
	private boolean writeOrdered;
	private boolean writePretty;

	public XDIJXDWriter(Properties parameters) {

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

	private void writeInternal(Graph graph, JsonArray jsonArray) throws IOException {

		// start with the common root node

		for (ContextNode childContextNode : graph.getRootContextNode().getContextNodes()) {

			if (! includeContextNode(childContextNode)) continue;

			JsonObject jsonObject = new JsonObject();
			jsonArray.add(jsonObject);

			// create mapping

			JXDMapping mapping = JXDMapping.empty();
			jsonObject.add(JXDConstants.JXD_MAPPING, mapping.begin());

			// process context node

			this.putContextNodeIntoJsonObject(childContextNode, jsonObject, mapping, true);

			// finish mapping

			if (! mapping.finish()) jsonObject.remove(JXDConstants.JXD_MAPPING);
		}
	}

	@SuppressWarnings("resource")
	@Override
	public Writer write(Graph graph, Writer writer) throws IOException {

		// write ordered?

		Graph orderedGraph = null;

		if (this.writeOrdered) {

			MemoryGraphFactory memoryGraphFactory = new MemoryGraphFactory();
			memoryGraphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ALPHA);
			orderedGraph = memoryGraphFactory.openGraph();
			CopyUtil.copyGraph(graph, orderedGraph, null);

			graph = orderedGraph;
		}

		// write

		JsonArray jsonArray  = new JsonArray();

		this.writeInternal(graph, jsonArray);

		JsonWriter jsonWriter = new JsonWriter(writer);
		if (this.writePretty) jsonWriter.setIndent("  ");
		gson.toJson(jsonArray , jsonWriter);
		jsonWriter.flush();
		writer.flush();

		return writer;
	}

	private void putContextNodeIntoJsonObject(ContextNode contextNode, JsonObject jsonObject, JXDMapping mapping, boolean first) {

		// collapse context node

		ContextNode collapsedContextNode = collapseContextNode(contextNode);
		XDIAddress localXDIAddress = XDIAddressUtil.localXDIAddress(collapsedContextNode.getXDIAddress(), collapsedContextNode.getXDIAddress().getNumXDIArcs() - contextNode.getXDIAddress().getNumXDIArcs() + 1);
		contextNode = collapsedContextNode;

		// only literal node?

		if (containsOnlyLiteral(contextNode)) {

			jsonObject.add(mapLiteral(localXDIAddress, mapping), AbstractLiteral.literalDataToJsonElement(contextNode.getLiteral().getLiteralData()));
			return;
		}

		// set up context node

		if (first) {

			jsonObject.addProperty(JXDConstants.JXD_ID, localXDIAddress.toString());
		} else {

			JsonObject childJsonObject = new JsonObject();
			jsonObject.add(mapContextNode(localXDIAddress, childJsonObject, mapping), childJsonObject);
			jsonObject = childJsonObject;
		}

		// literal node

		if (contextNode.containsLiteral()) {

			Literal literalNode = contextNode.getLiteral();

			this.putLiteralIntoJsonObject(literalNode, jsonObject);
		}

		// context nodes

		for (ContextNode childContextNode : contextNode.getContextNodes()) {

			if (! includeContextNode(childContextNode)) continue;

			this.putContextNodeIntoJsonObject(childContextNode, jsonObject, mapping, false);
		}

		// relations

		for (Relation relation : contextNode.getRelations()) {

			if (XdiInnerRoot.isValid(relation.follow())) {

				this.putInnerRootIntoJsonObject(relation, jsonObject, mapping);
			} else {

				this.putRelationIntoJsonObject(relation, jsonObject, mapping);
			}
		}
	}

	private static String mapContextNode(XDIAddress XDIaddress, JsonObject childJsonObject, JXDMapping mapping) {

		// determine term name

		String termName = mapTermName(XDIaddress);
		if (termName == null) termName = XDIaddress.toString();

		// create term

		JXDTerm term = new JXDTerm(termName, XDIaddress, XDIAddress.create(JXDConstants.JXD_ID));
		term = mapping.addOrReuse(term);

		// done

		return term.getName();
	}

	private static String mapLiteral(XDIAddress XDIaddress, JXDMapping mapping) {

		// determine term name

		String termName = mapTermName(XDIaddress);
		if (termName == null) termName = XDIaddress.toString();

		// create term

		JXDTerm term = new JXDTerm(termName, XDIaddress, null);
		term = mapping.addOrReuse(term);

		// done

		return term.getName();
	}

	private static String mapRelation(XDIAddress XDIaddress, JXDMapping mapping) {

		// determine term name

		String termName = mapTermName(XDIaddress);
		if (termName == null) termName = XDIaddress.toString();

		// create term

		JXDTerm term = new JXDTerm(termName, XDIaddress, XDIAddress.create(JXDConstants.JXD_ID));
		term = mapping.addOrReuse(term);

		// done

		return term.getName();
	}

	private static String mapInnerRoot(XDIAddress XDIaddress, JsonObject childJsonObject, JXDMapping mapping) {

		// determine term name

		String termName = mapTermName(XDIaddress);
		if (termName == null) termName = XDIaddress.toString();

		// create term

		JXDTerm term = new JXDTerm(termName, XDIaddress, null);
		term = mapping.addOrReuse(term);

		// augment child JSON object

		childJsonObject.add(JXDConstants.JXD_TYPE, new JsonPrimitive(JXDConstants.JXD_GRAPH));

		// done

		return term.getName();
	}

	private static String mapTermName(XDIAddress XDIaddress) {

		StringBuffer termName = new StringBuffer();
		for (XDIArc XDIarc : XDIaddress.getXDIArcs()) {

			if (XDIConstants.CS_AUTHORITY_PERSONAL.equals(XDIarc.getCs())) return null;
			if (XDIConstants.CS_AUTHORITY_LEGAL.equals(XDIarc.getCs())) return null;
			if (XDIarc.hasXRef()) return null;

			if (XDIarc.hasLiteral()) termName.append(XDIarc.getLiteral());
		}

		if (termName.length() == 0) return null;

		return termName.toString();
	}
	
	private static boolean includeContextNode(ContextNode contextNode) {

		if (contextNode.containsIncomingRelations() && contextNode.isEmpty()) return false;
		if (XdiInnerRoot.isValid(contextNode)) return false;

		return true;
	}

	private static ContextNode collapseContextNode(ContextNode contextNode) {

		if (contextNode.getContextNodeCount() != 1) return contextNode;
		if (contextNode.containsRelations()) return contextNode;
		if (contextNode.containsLiteral()) return contextNode;

		ContextNode childContextNode = contextNode.getContextNodes().next();
		if (! (XdiAbstractEntity.isValid(contextNode) || XdiEntityCollection.isValid(contextNode)) && (XdiAbstractEntity.isValid(childContextNode) || XdiEntityCollection.isValid(childContextNode))) return contextNode;
		if (! (XdiAbstractAttribute.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode)) && (XdiAbstractAttribute.isValid(childContextNode) || XdiAttributeCollection.isValid(childContextNode))) return contextNode;

		return collapseContextNode(childContextNode);
	}

	private static boolean containsOnlyLiteral(ContextNode contextNode) {

		if (contextNode.containsContextNodes()) return false;
		if (contextNode.containsRelations()) return false;
		if (! contextNode.containsLiteral()) return false;

		return true;
	}

	private void putRelationIntoJsonObject(Relation relation, JsonObject jsonObject, JXDMapping mapping) {

		JsonArray childJsonArray = jsonObject.getAsJsonArray(mapRelation(relation.getXDIAddress(), mapping));

		if (childJsonArray == null) {

			childJsonArray = new JsonArray();
			jsonObject.add(mapRelation(relation.getXDIAddress(), mapping), childJsonArray);
		}

		childJsonArray.add(new JsonPrimitive(relation.getTargetContextNodeXDIAddress().toString()));
	}

	private void putLiteralIntoJsonObject(Literal literalNode, JsonObject jsonObject) {

		jsonObject.add(JXDConstants.JXD_VALUE, AbstractLiteral.literalDataToJsonElement(literalNode.getLiteralData()));
	}

	private void putInnerRootIntoJsonObject(Relation relation, JsonObject jsonObject, JXDMapping mapping) {

		JsonObject childJsonObject = new JsonObject();
		jsonObject.add(mapInnerRoot(relation.getXDIAddress(), childJsonObject, mapping), childJsonObject);

		for (ContextNode childContextNode : relation.follow().getContextNodes()) {

			if (! includeContextNode(childContextNode)) continue;

			this.putContextNodeIntoJsonObject(childContextNode, childJsonObject, mapping, false);
		}
	}
}
