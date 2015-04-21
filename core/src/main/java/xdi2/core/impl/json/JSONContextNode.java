package xdi2.core.impl.json;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.Node;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JSONContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 1222781682444161539L;

	private static final Logger log = LoggerFactory.getLogger(JSONContextNode.class);

	private XDIArc XDIarc;
	private XDIAddress XDIaddress;

	JSONContextNode(JSONGraph graph, JSONContextNode contextNode, XDIArc XDIarc, XDIAddress XDIaddress) {

		super(graph, contextNode);

		this.XDIarc = XDIarc;
		this.XDIaddress = XDIaddress;
	}

	@Override
	public synchronized void clear() {

		if (this.isRootContextNode()) {

			((JSONGraph) this.getGraph()).jsonDelete("");
		} else {

			super.clear();
		}
	}

	@Override
	public XDIArc getXDIArc() {

		return this.XDIarc;
	}

	@Override
	public XDIAddress getXDIAddress() {

		return this.XDIaddress;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	@Override
	public ContextNode setContextNode(XDIArc XDIarc) {

		// check validity

		this.setContextNodeCheckValid(XDIarc);

		// set the context node

		((JSONGraph) this.getGraph()).jsonSaveToArray(this.getXDIAddress().toString(), XDIConstants.STRING_CONTEXT, new JsonPrimitive(XDIarc.toString()));

		XDIAddress XDIaddress = XDIAddressUtil.concatXDIAddresses(this.getXDIAddress(), XDIarc);

		JSONContextNode contextNode = new JSONContextNode((JSONGraph) this.getGraph(), this, XDIarc, XDIaddress);

		// set inner root

		this.setContextNodeSetInnerRoot(XDIarc, contextNode);

		// done

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(XDIArc XDIarc, boolean subgraph) {

		JsonObject jsonObject;

		if (subgraph)
			jsonObject = ((JSONGraph) this.getGraph()).jsonLoadWithPrefix(this.getXDIAddress().toString()).get(this.getXDIAddress().toString());
		else
			jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getXDIAddress().toString());

		if (jsonObject == null) return null;

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.STRING_CONTEXT);
		if (jsonArrayContexts == null) return null;
		if (jsonArrayContexts.size() < 1) return null;

		if (! new IteratorContains<JsonElement> (jsonArrayContexts.iterator(), new JsonPrimitive(XDIarc.toString())).contains()) return null;

		XDIAddress XDIaddress = XDIAddressUtil.concatXDIAddresses(this.getXDIAddress(), XDIarc);

		return new JSONContextNode((JSONGraph) JSONContextNode.this.getGraph(), JSONContextNode.this, XDIarc, XDIaddress);
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress relativeContextNodeXDIAddress, boolean subgraph) {

		if (XDIConstants.XDI_ADD_ROOT.equals(relativeContextNodeXDIAddress)) return this;

		XDIAddress contextNodeXDIAddress = XDIAddressUtil.concatXDIAddresses(this.getXDIAddress(), relativeContextNodeXDIAddress);

		XDIAddress parentcontextNodeXDIAddress = XDIAddressUtil.concatXDIAddresses(this.getXDIAddress(), XDIAddressUtil.parentXDIAddress(relativeContextNodeXDIAddress, -1));
		XDIArc XDIarc = relativeContextNodeXDIAddress.getLastXDIArc();

		// load the JSON object for the parent context node

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(parentcontextNodeXDIAddress.toString());

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.STRING_CONTEXT);
		if (jsonArrayContexts == null) return null;
		if (jsonArrayContexts.size() < 1) return null;

		if (! new IteratorContains<JsonElement> (jsonArrayContexts.iterator(), new JsonPrimitive(XDIarc.toString())).contains()) return null;

		JSONContextNode contextNode = this;

		for (XDIArc temparc : relativeContextNodeXDIAddress.getXDIArcs()) {

			XDIAddress tempAddress = XDIAddressUtil.concatXDIAddresses(contextNode.getXDIAddress(), temparc);

			contextNode = new JSONContextNode((JSONGraph) this.getGraph(), contextNode, temparc, tempAddress);
		}

		// retrieve subgraph?

		if (subgraph) {

			((JSONGraph) this.getGraph()).jsonLoadWithPrefix(contextNodeXDIAddress.toString());
		}

		// done

		return contextNode;
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getXDIAddress().toString());

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.STRING_CONTEXT);
		if (jsonArrayContexts == null) return new EmptyIterator<ContextNode> ();
		if (jsonArrayContexts.size() < 1) return new EmptyIterator<ContextNode> ();

		final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayContexts.iterator()).list();

		return new ReadOnlyIterator<ContextNode> (new MappingIterator<JsonElement, ContextNode> (entryList.iterator()) {

			@Override
			public ContextNode map(JsonElement jsonElement) {

				XDIArc XDIarc = XDIArc.create(((JsonPrimitive) jsonElement).getAsString());
				XDIAddress XDIaddress = XDIAddressUtil.concatXDIAddresses(JSONContextNode.this.getXDIAddress(), XDIarc);

				return new JSONContextNode((JSONGraph) JSONContextNode.this.getGraph(), JSONContextNode.this, XDIarc, XDIaddress);
			}
		});
	}

	@Override
	public void delContextNode(XDIArc XDIarc) {

		ContextNode contextNode = this.getContextNode(XDIarc, true);
		if (contextNode == null) return;

		// delete all relations and incoming relations

		((JSONContextNode) contextNode).delContextNodeDelAllRelations();
		((JSONContextNode) contextNode).delContextNodeDelAllIncomingRelations();

		// delete this context node

		((JSONGraph) this.getGraph()).jsonDelete(contextNode.getXDIAddress().toString());
		((JSONGraph) this.getGraph()).jsonDeleteFromArray(this.getXDIAddress().toString(), XDIConstants.STRING_CONTEXT, new JsonPrimitive(XDIarc.toString()));
	}

	@Override
	public synchronized Relation setRelation(XDIAddress XDIaddress, Node targetNode) {

		XDIAddress targetXDIAddress = targetNode.getXDIAddress();

		// check validity

		this.setRelationCheckValid(XDIaddress, targetXDIAddress);

		// set the relation

		((JSONGraph) this.getGraph()).jsonSaveToArray(this.getXDIAddress().toString(), XDIaddress.toString(), new JsonPrimitive(targetXDIAddress.toString()));
		((JSONGraph) this.getGraph()).jsonSaveToArray(targetXDIAddress.toString(), "/" + XDIaddress.toString(), new JsonPrimitive(this.getXDIAddress().toString()));

		JSONRelation relation = new JSONRelation(this, XDIaddress, targetXDIAddress);

		// done

		return relation;
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getXDIAddress().toString());

		final Set<Entry<String, JsonElement>> entrySet = new HashSet<Entry<String, JsonElement>> (jsonObject.entrySet());

		return new DescendingIterator<Entry<String, JsonElement>, Relation> (entrySet.iterator()) {

			@Override
			public Iterator<Relation> descend(Entry<String, JsonElement> entry) {

				if (entry.getKey().startsWith("/")) return null;

				final XDIAddress XDIaddress = XDIAddress.create(entry.getKey());
				if (XDIConstants.STRING_CONTEXT.toString().equals(XDIaddress.toString())) return null;
				if (XDIConstants.XDI_ARC_LITERAL.toString().equals(XDIaddress.toString())) return null;

				JsonArray jsonArrayRelations = (JsonArray) entry.getValue();

				final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayRelations.iterator()).list();

				return new ReadOnlyIterator<Relation> (new MappingIterator<JsonElement, Relation> (entryList.iterator()) {

					@Override
					public Relation map(JsonElement jsonElement) {

						XDIAddress targetXDIAddress = XDIAddress.create(((JsonPrimitive) jsonElement).getAsString());

						return new JSONRelation(JSONContextNode.this, XDIaddress, targetXDIAddress);
					}
				});
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getIncomingRelations() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getXDIAddress().toString());

		final Set<Entry<String, JsonElement>> entrySet = new HashSet<Entry<String, JsonElement>> (jsonObject.entrySet());

		return new DescendingIterator<Entry<String, JsonElement>, Relation> (entrySet.iterator()) {

			@Override
			public Iterator<Relation> descend(Entry<String, JsonElement> entry) {

				if (! entry.getKey().startsWith("/")) return null;

				final XDIAddress XDIaddress = XDIAddress.create(entry.getKey().substring(1));

				JsonArray jsonArrayIncomingRelations = (JsonArray) entry.getValue();

				final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayIncomingRelations.iterator()).list();

				return new NotNullIterator<Relation> (new MappingIterator<JsonElement, Relation> (entryList.iterator()) {

					@Override
					public Relation map(JsonElement jsonElement) {

						XDIAddress contextNodeXDIAddress = XDIAddress.create(((JsonPrimitive) jsonElement).getAsString());

						ContextNode contextNode = JSONContextNode.this.getGraph().getDeepContextNode(contextNodeXDIAddress, false);

						if (contextNode == null) {

							log.warn("In context node " + JSONContextNode.this.getXDIAddress() + " found incoming relation " + XDIaddress + " from non-existent context node " + contextNodeXDIAddress);

							return null;
						}

						return new JSONRelation(contextNode, XDIaddress, JSONContextNode.this.getXDIAddress());
					}
				});
			}
		};
	}

	@Override
	public void delRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		// delete the relation

		((JSONGraph) this.getGraph()).jsonDeleteFromArray(this.getXDIAddress().toString(), XDIaddress.toString(), new JsonPrimitive(targetXDIAddress.toString()));
		((JSONGraph) this.getGraph()).jsonDeleteFromArray(targetXDIAddress.toString(), "/" + XDIaddress.toString(), new JsonPrimitive(this.getXDIAddress().toString()));

		// delete inner root

		this.delRelationDelInnerRoot(XDIaddress, targetXDIAddress);
	}

	@Override
	public LiteralNode setLiteralNode(Object literalData) {

		// check validity

		this.setLiteralCheckValid(literalData);

		// set the literal

		((JSONGraph) this.getGraph()).jsonSaveToObject(this.getXDIAddress().toString(), XDIConstants.XDI_ARC_LITERAL.toString(), AbstractLiteralNode.literalDataToJsonElement(literalData));

		JSONLiteralNode literalNode = new JSONLiteralNode(this);

		// done

		return literalNode;
	}

	@Override
	public LiteralNode getLiteralNode() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getXDIAddress().toString());

		if (! jsonObject.has(XDIConstants.XDI_ARC_LITERAL.toString())) return null;

		return new JSONLiteralNode(this);
	}

	@Override
	public void delLiteralNode() {

		((JSONGraph) this.getGraph()).jsonDeleteFromObject(this.getXDIAddress().toString(), XDIConstants.XDI_ARC_LITERAL.toString());
	}
}
