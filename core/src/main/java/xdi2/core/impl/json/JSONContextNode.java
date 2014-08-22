package xdi2.core.impl.json;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.AddressUtil;
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

	private XDIArc arc;
	private XDIAddress address;

	JSONContextNode(JSONGraph graph, JSONContextNode contextNode, XDIArc arc, XDIAddress address) {

		super(graph, contextNode);

		this.arc = arc;
		this.address = address;
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
	public XDIArc getArc() {

		return this.arc;
	}

	@Override
	public XDIAddress getAddress() {

		return this.address;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	@Override
	public ContextNode setContextNode(XDIArc arc) {

		// check validity

		this.setContextNodeCheckValid(arc);

		// set the context node

		((JSONGraph) this.getGraph()).jsonSaveToArray(this.getAddress().toString(), XDIConstants.XDI_ADD_CONTEXT.toString(), new JsonPrimitive(arc.toString()));

		XDIAddress address = AddressUtil.concatAddresses(this.getAddress(), arc);

		JSONContextNode contextNode = new JSONContextNode((JSONGraph) this.getGraph(), this, arc, address);

		// set inner root

		this.setContextNodeSetInnerRoot(arc, contextNode);

		// done

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(XDIArc arc, boolean subgraph) {

		JsonObject jsonObject;

		if (subgraph)
			jsonObject = ((JSONGraph) this.getGraph()).jsonLoadWithPrefix(this.getAddress().toString()).get(this.getAddress().toString());
		else
			jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getAddress().toString());

		if (jsonObject == null) return null;

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XDI_ADD_CONTEXT.toString());
		if (jsonArrayContexts == null) return null;
		if (jsonArrayContexts.size() < 1) return null;

		if (! new IteratorContains<JsonElement> (jsonArrayContexts.iterator(), new JsonPrimitive(arc.toString())).contains()) return null;

		XDIAddress address = AddressUtil.concatAddresses(this.getAddress(), arc);

		return new JSONContextNode((JSONGraph) JSONContextNode.this.getGraph(), JSONContextNode.this, arc, address);
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress relativecontextNodeAddress, boolean subgraph) {

		if (XDIConstants.XDI_ADD_ROOT.equals(relativecontextNodeAddress)) return this;

		XDIAddress contextNodeAddress = AddressUtil.concatAddresses(this.getAddress(), relativecontextNodeAddress);

		XDIAddress parentcontextNodeAddress = AddressUtil.concatAddresses(this.getAddress(), AddressUtil.parentAddress(relativecontextNodeAddress, -1));
		XDIArc arc = relativecontextNodeAddress.getLastArc();

		// load the JSON object for the parent context node

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(parentcontextNodeAddress.toString());

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XDI_ADD_CONTEXT.toString());
		if (jsonArrayContexts == null) return null;
		if (jsonArrayContexts.size() < 1) return null;

		if (! new IteratorContains<JsonElement> (jsonArrayContexts.iterator(), new JsonPrimitive(arc.toString())).contains()) return null;

		JSONContextNode contextNode = this;

		for (XDIArc temparc : relativecontextNodeAddress.getArcs()) {

			XDIAddress tempAddress = AddressUtil.concatAddresses(contextNode.getAddress(), temparc);

			contextNode = new JSONContextNode((JSONGraph) this.getGraph(), contextNode, temparc, tempAddress);
		}

		// retrieve subgraph?

		if (subgraph) {

			((JSONGraph) this.getGraph()).jsonLoadWithPrefix(contextNodeAddress.toString());
		}

		// done

		return contextNode;
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getAddress().toString());

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XDI_ADD_CONTEXT.toString());
		if (jsonArrayContexts == null) return new EmptyIterator<ContextNode> ();
		if (jsonArrayContexts.size() < 1) return new EmptyIterator<ContextNode> ();

		final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayContexts.iterator()).list();

		return new ReadOnlyIterator<ContextNode> (new MappingIterator<JsonElement, ContextNode> (entryList.iterator()) {

			@Override
			public ContextNode map(JsonElement jsonElement) {

				XDIArc arc = XDIArc.create(((JsonPrimitive) jsonElement).getAsString());
				XDIAddress address = AddressUtil.concatAddresses(JSONContextNode.this.getAddress(), arc);

				return new JSONContextNode((JSONGraph) JSONContextNode.this.getGraph(), JSONContextNode.this, arc, address);
			}
		});
	}

	@Override
	public void delContextNode(XDIArc arc) {

		ContextNode contextNode = this.getContextNode(arc, true);
		if (contextNode == null) return;

		// delete all relations and incoming relations

		((JSONContextNode) contextNode).delContextNodeDelAllRelations();
		((JSONContextNode) contextNode).delContextNodeDelAllIncomingRelations();

		// delete this context node

		((JSONGraph) this.getGraph()).jsonDelete(contextNode.getAddress().toString());
		((JSONGraph) this.getGraph()).jsonDeleteFromArray(this.getAddress().toString(), XDIConstants.XDI_ADD_CONTEXT.toString(), new JsonPrimitive(arc.toString()));
	}

	@Override
	public synchronized Relation setRelation(XDIAddress arc, ContextNode targetContextNode) {

		XDIAddress targetContextNodeAddress = targetContextNode.getAddress();

		// check validity

		this.setRelationCheckValid(arc, targetContextNodeAddress);

		// set the relation

		((JSONGraph) this.getGraph()).jsonSaveToArray(this.getAddress().toString(), arc.toString(), new JsonPrimitive(targetContextNodeAddress.toString()));
		((JSONGraph) this.getGraph()).jsonSaveToArray(targetContextNodeAddress.toString(), "/" + arc.toString(), new JsonPrimitive(this.getAddress().toString()));

		JSONRelation relation = new JSONRelation(this, arc, targetContextNodeAddress);

		// done

		return relation;
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getAddress().toString());

		final Set<Entry<String, JsonElement>> entrySet = new HashSet<Entry<String, JsonElement>> (jsonObject.entrySet());

		return new DescendingIterator<Entry<String, JsonElement>, Relation> (entrySet.iterator()) {

			@Override
			public Iterator<Relation> descend(Entry<String, JsonElement> entry) {

				if (entry.getKey().startsWith("/")) return null;

				final XDIAddress arc = XDIAddress.create(entry.getKey());
				if (XDIConstants.XDI_ADD_CONTEXT.equals(arc)) return null;
				if (XDIConstants.XDI_ADD_LITERAL.equals(arc)) return null;

				JsonArray jsonArrayRelations = (JsonArray) entry.getValue();

				final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayRelations.iterator()).list();

				return new ReadOnlyIterator<Relation> (new MappingIterator<JsonElement, Relation> (entryList.iterator()) {

					@Override
					public Relation map(JsonElement jsonElement) {

						XDIAddress targetContextNodeAddress = XDIAddress.create(((JsonPrimitive) jsonElement).getAsString());

						return new JSONRelation(JSONContextNode.this, arc, targetContextNodeAddress);
					}
				});
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getIncomingRelations() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getAddress().toString());

		final Set<Entry<String, JsonElement>> entrySet = new HashSet<Entry<String, JsonElement>> (jsonObject.entrySet());

		return new DescendingIterator<Entry<String, JsonElement>, Relation> (entrySet.iterator()) {

			@Override
			public Iterator<Relation> descend(Entry<String, JsonElement> entry) {

				if (! entry.getKey().startsWith("/")) return null;

				final XDIAddress arc = XDIAddress.create(entry.getKey().substring(1));

				JsonArray jsonArrayIncomingRelations = (JsonArray) entry.getValue();

				final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayIncomingRelations.iterator()).list();

				return new NotNullIterator<Relation> (new MappingIterator<JsonElement, Relation> (entryList.iterator()) {

					@Override
					public Relation map(JsonElement jsonElement) {

						XDIAddress contextNodeAddress = XDIAddress.create(((JsonPrimitive) jsonElement).getAsString());

						ContextNode contextNode = JSONContextNode.this.getGraph().getDeepContextNode(contextNodeAddress, false);

						if (contextNode == null) {

							log.warn("In context node " + JSONContextNode.this.getAddress() + " found incoming relation " + arc + " from non-existent context node " + contextNodeAddress);

							return null;
						}

						return new JSONRelation(contextNode, arc, JSONContextNode.this.getAddress());
					}
				});
			}
		};
	}

	@Override
	public void delRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		// delete the relation

		((JSONGraph) this.getGraph()).jsonDeleteFromArray(this.getAddress().toString(), arc.toString(), new JsonPrimitive(targetContextNodeAddress.toString()));
		((JSONGraph) this.getGraph()).jsonDeleteFromArray(targetContextNodeAddress.toString(), "/" + arc.toString(), new JsonPrimitive(this.getAddress().toString()));

		// delete inner root

		this.delRelationDelInnerRoot(arc, targetContextNodeAddress);
	}

	@Override
	public Literal setLiteral(Object literalData) {

		// check validity

		this.setLiteralCheckValid(literalData);

		// set the literal

		((JSONGraph) this.getGraph()).jsonSaveToObject(this.getAddress().toString(), XDIConstants.XDI_ARC_LITERAL.toString(), AbstractLiteral.literalDataToJsonElement(literalData));

		JSONLiteral literal = new JSONLiteral(this);

		// done

		return literal;
	}

	@Override
	public Literal getLiteral() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.getAddress().toString());

		if (! jsonObject.has(XDIConstants.XDI_ARC_LITERAL.toString())) return null;

		return new JSONLiteral(this);
	}

	@Override
	public void delLiteral() {

		((JSONGraph) this.getGraph()).jsonDeleteFromObject(this.getAddress().toString(), XDIConstants.XDI_ARC_LITERAL.toString());
	}
}
