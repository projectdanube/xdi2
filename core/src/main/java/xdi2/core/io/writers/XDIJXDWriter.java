package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AbstractXDIWriter;
import xdi2.core.io.MimeType;
import xdi2.core.io.util.JXDConstants;
import xdi2.core.io.util.JXDMapping;
import xdi2.core.io.util.JXDMapping.JXDTerm;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIAddressUtil;

public class XDIJXDWriter extends AbstractXDIWriter {

	private static final long serialVersionUID = 1077789049204778292L;

	public static final String FORMAT_NAME = "JXD";
	public static final String FILE_EXTENSION = "jxd";
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJXDWriter(Properties parameters) {

		super(parameters);
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

		if (this.isWriteOrdered()) {

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
		if (this.isWritePretty()) jsonWriter.setIndent("  ");
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

		if (containsOnlyLiteralNode(contextNode)) {

			jsonObject.add(mapLiteralNode(localXDIAddress, mapping), AbstractLiteralNode.literalDataToJsonElement(contextNode.getLiteralNode().getLiteralData()));
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

		if (contextNode.containsLiteralNode()) {

			LiteralNode literalNode = contextNode.getLiteralNode();

			this.putLiteralNodeIntoJsonObject(literalNode, jsonObject);
		}

		// context nodes

		for (ContextNode childContextNode : contextNode.getContextNodes()) {

			if (! includeContextNode(childContextNode)) continue;

			this.putContextNodeIntoJsonObject(childContextNode, jsonObject, mapping, false);
		}

		// relations

		for (Relation relation : contextNode.getRelations()) {

			if (XdiInnerRoot.isValid(relation.followContextNode())) {

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

	private static String mapLiteralNode(XDIAddress XDIaddress, JXDMapping mapping) {

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
		if (contextNode.containsLiteralNode()) return contextNode;

		ContextNode childContextNode = contextNode.getContextNodes().next();
		if (! (XdiAbstractEntity.isValid(contextNode) || XdiEntityCollection.isValid(contextNode)) && (XdiAbstractEntity.isValid(childContextNode) || XdiEntityCollection.isValid(childContextNode))) return contextNode;
		if (! (XdiAbstractAttribute.isValid(contextNode) || XdiAttributeCollection.isValid(contextNode)) && (XdiAbstractAttribute.isValid(childContextNode) || XdiAttributeCollection.isValid(childContextNode))) return contextNode;

		return collapseContextNode(childContextNode);
	}

	private static boolean containsOnlyLiteralNode(ContextNode contextNode) {

		if (contextNode.containsContextNodes()) return false;
		if (contextNode.containsRelations()) return false;
		if (! contextNode.containsLiteralNode()) return false;

		return true;
	}

	private void putRelationIntoJsonObject(Relation relation, JsonObject jsonObject, JXDMapping mapping) {

		JsonArray childJsonArray = jsonObject.getAsJsonArray(mapRelation(relation.getXDIAddress(), mapping));

		if (childJsonArray == null) {

			childJsonArray = new JsonArray();
			jsonObject.add(mapRelation(relation.getXDIAddress(), mapping), childJsonArray);
		}

		childJsonArray.add(new JsonPrimitive(relation.getTargetXDIAddress().toString()));
	}

	private void putLiteralNodeIntoJsonObject(LiteralNode literalNode, JsonObject jsonObject) {

		jsonObject.add(JXDConstants.JXD_VALUE, AbstractLiteralNode.literalDataToJsonElement(literalNode.getLiteralData()));
	}

	private void putInnerRootIntoJsonObject(Relation relation, JsonObject jsonObject, JXDMapping mapping) {

		JsonObject childJsonObject = new JsonObject();
		jsonObject.add(mapInnerRoot(relation.getXDIAddress(), childJsonObject, mapping), childJsonObject);

		for (ContextNode childContextNode : relation.followContextNode().getContextNodes()) {

			if (! includeContextNode(childContextNode)) continue;

			this.putContextNodeIntoJsonObject(childContextNode, childJsonObject, mapping, false);
		}
	}
}
