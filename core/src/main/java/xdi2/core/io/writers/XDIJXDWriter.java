package xdi2.core.io.writers;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
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
	public static final MimeType MIME_TYPE = new MimeType("application/xdi+jxd");

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	private Map<String, JXDMapping> bootstrapJXDMappings;

	public XDIJXDWriter(Properties parameters) {

		super(parameters);

		this.bootstrapJXDMappings = new HashMap<String, JXDMapping> (JXDMapping.bootstrapJXDMappings);
	}

	private void writeInternal(Graph graph, JsonArray jsonArray) throws IOException {

		// start with the common root node

		for (ContextNode childContextNode : graph.getRootContextNode().getContextNodes()) {

			// create JSON object for this context node

			JsonObject jsonObject = new JsonObject();

			// create mapping

			JXDMapping JXDmapping = JXDMapping.empty(null);

			JsonArray jsonArrayMapping = new JsonArray();
			jsonObject.add(JXDConstants.JXD_MAPPING, jsonArrayMapping);

			if (this.getBootstrapJXDMappings() != null) {

				for (JXDMapping bootstrapJXDMapping : this.getBootstrapJXDMappings().values()) {

					if (bootstrapJXDMapping.getReference() != null) {

						jsonArrayMapping.add(new JsonPrimitive(bootstrapJXDMapping.getReference()));
					}

					JXDmapping.merge(bootstrapJXDMapping);
				}
			}

			JsonObject jsonObjectMapping = JXDmapping.begin();
			jsonArrayMapping.add(jsonObjectMapping);

			// process context node

			this.putContextNodeIntoJsonObject(childContextNode, jsonObject, JXDmapping, true);

			// finish mapping

			if (this.getBootstrapJXDMappings() != null) {

				for (JXDMapping bootstrapJXDMapping : this.getBootstrapJXDMappings().values()) {

					JXDmapping.unmerge(bootstrapJXDMapping);
				}
			}

			JXDmapping.finish();
			if (jsonObjectMapping.entrySet().isEmpty()) jsonObject.remove(JXDConstants.JXD_MAPPING);
			if (jsonObject.entrySet().isEmpty()) jsonObject.remove(JXDConstants.JXD_MAPPING);

			// finish JSON object for this context node

			if (! jsonObject.entrySet().isEmpty()) jsonArray.add(jsonObject);
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

		// include it?

		if (! includeContextNode(contextNode)) return;

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
			jsonObject.add(mapContextNode(localXDIAddress, mapping), childJsonObject);
			jsonObject = childJsonObject;
		}

		// literal node

		if (contextNode.containsLiteralNode()) {

			LiteralNode literalNode = contextNode.getLiteralNode();

			this.putLiteralNodeIntoJsonObject(literalNode, jsonObject);
		}

		// context nodes

		for (ContextNode childContextNode : contextNode.getContextNodes()) {

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

	private static String mapContextNode(XDIAddress XDIaddress, JXDMapping mapping) {

		// determine term name

		String termName = mapTermName(XDIaddress);
		if (termName == null) termName = XDIaddress.toString();

		// determine term ID

		XDIAddress termId = XDIaddress;

		// create term

		JXDTerm term = new JXDTerm(termName, termId, XDIAddress.create(JXDConstants.JXD_ID));
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

	private static String mapInnerRoot(XDIAddress XDIaddress, JXDMapping mapping) {

		// determine term name

		String termName = mapTermName(XDIaddress);
		if (termName == null) termName = XDIaddress.toString();

		// create term

		JXDTerm term = new JXDTerm(termName, XDIaddress, XDIAddress.create(JXDConstants.JXD_GRAPH));
		term = mapping.addOrReuse(term);

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

	private static boolean allStatementsImplied(ContextNode contextNode) {

		for (Statement statement : contextNode.getAllStatements()) {

			if (! statement.isImplied()) return false;
		}

		return true;
	}

	private static boolean includeContextNode(ContextNode contextNode) {

		if (! contextNode.getStatement().isImplied()) return true;

		if (XdiInnerRoot.fromContextNode(contextNode) != null) return false;

		for (Relation relation : contextNode.getRelations()) {

			XdiInnerRoot xdiInnerRoot = XdiInnerRoot.fromContextNode(relation.followContextNode());
			if (xdiInnerRoot != null && xdiInnerRoot.getSubjectContextNode() == contextNode) {

				if (! allStatementsImplied(xdiInnerRoot.getContextNode())) return true;
			}
		}

		if (! allStatementsImplied(contextNode)) return true;

		//if (contextNode.getAllLiterals().hasNext()) return true;
		//if (contextNode.getAllRelations().hasNext()) return true;


		/*		if (contextNode.isEmpty() && contextNode.containsIncomingRelations()) return false;
		if (XdiInnerRoot.isValid(contextNode)) return false;*/

		return false;

		/*		if (contextNode.containsLiteralNode()) return true;
		if (contextNode.containsRelations()) return true;
		if (contextNode.containsIncomingRelations()) return false;

		for (ContextNode childContextNode : contextNode.getContextNodes()) {

			if (includeContextNode(childContextNode)) return true;
		}

		if (contextNode.containsContextNodes()) return false;

		return false;*/
	}

	private static boolean includeInnerRoot(Relation relation) {

		if (! allStatementsImplied(relation.followContextNode())) return true;

		return false;
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

		String key = mapRelation(relation.getXDIAddress(), mapping);

		// determine child JSON array

		JsonElement childJsonElement = jsonObject.get(key);
		JsonArray childJsonArray;

		if (childJsonElement == null) {

			childJsonArray = new JsonArray();
			jsonObject.add(key, childJsonArray);
		} else if (childJsonElement instanceof JsonArray) {

			childJsonArray = (JsonArray) childJsonElement;
		} else if (childJsonElement instanceof JsonObject) {

			childJsonArray = new JsonArray();
			childJsonArray.add((JsonObject) childJsonElement);
			jsonObject.remove(key);
			jsonObject.add(key, childJsonArray);
		} else {

			throw new Xdi2RuntimeException("Unexpected JSON element for relation at key " + key + ": " + childJsonElement);
		}

		// fill child JSON array

		childJsonArray.add(new JsonPrimitive(relation.getTargetXDIAddress().toString()));
	}

	private void putLiteralNodeIntoJsonObject(LiteralNode literalNode, JsonObject jsonObject) {

		// fill child JSON object

		jsonObject.add(JXDConstants.JXD_VALUE, AbstractLiteralNode.literalDataToJsonElement(literalNode.getLiteralData()));
	}

	private void putInnerRootIntoJsonObject(Relation relation, JsonObject jsonObject, JXDMapping mapping) {

		// include it?

		if (! includeInnerRoot(relation)) return;

		// determine key

		String key = mapInnerRoot(relation.getXDIAddress(), mapping);

		// determine child JSON object

		JsonElement childJsonElement = jsonObject.get(key);
		JsonObject childJsonObject;

		if (childJsonElement == null) {

			childJsonObject = new JsonObject();
			jsonObject.add(key, childJsonObject);
		} else if (childJsonElement instanceof JsonArray) {

			childJsonObject = new JsonObject();
			((JsonArray) childJsonElement).add(childJsonElement);
		} else {

			throw new Xdi2RuntimeException("Unexpected JSON element for inner root at key " + key + ": " + childJsonElement);
		}

		// type information for child JSON object

		childJsonObject.add(JXDConstants.JXD_TYPE, new JsonPrimitive(JXDConstants.JXD_GRAPH));

		// fill child JSON object

		for (ContextNode childContextNode : relation.followContextNode().getContextNodes()) {

			this.putContextNodeIntoJsonObject(childContextNode, childJsonObject, mapping, false);
		}
	}

	/*
	 * Getters and setters
	 */

	public Map<String, JXDMapping> getBootstrapJXDMappings() {

		return this.bootstrapJXDMappings;
	}

	public void setBootstrapJXGMappings(Map<String, JXDMapping> bootstrapJXDMappings) {

		this.bootstrapJXDMappings = bootstrapJXDMappings;
	}
}
