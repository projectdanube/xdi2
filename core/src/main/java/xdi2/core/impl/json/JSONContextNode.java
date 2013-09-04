package xdi2.core.impl.json;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.IteratorRemover;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JSONContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 1222781682444161539L;

	String id;
	private XDI3SubSegment arcXri;

	JSONContextNode(JSONGraph graph, JSONContextNode contextNode, String id, XDI3SubSegment arcXri) {

		super(graph, contextNode);

		this.id = id;
		this.arcXri = arcXri;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	@Override
	public XDI3SubSegment getArcXri() {

		return this.arcXri;
	}

	@Override
	public ContextNode setContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri);

		((JSONGraph) this.getGraph()).jsonSaveToArray(this.id, XDIConstants.XRI_SS_CONTEXT.toString(), new JsonPrimitive(arcXri.toString()));

		String id = this.getContextNodeId(arcXri);
		JSONContextNode jsonContextNode = new JSONContextNode((JSONGraph) this.getGraph(), this, id, arcXri);

		return jsonContextNode;
	}

	@Override
	public ContextNode getContextNode(XDI3SubSegment arcXri) {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.id);

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XRI_SS_CONTEXT.toString());
		if (jsonArrayContexts == null) return null;
		if (jsonArrayContexts.size() < 1) return null;

		if (! new IteratorContains<JsonElement> (jsonArrayContexts.iterator(), new JsonPrimitive(arcXri.toString())).contains()) return null;

		String id = JSONContextNode.this.getContextNodeId(arcXri);

		return new JSONContextNode((JSONGraph) JSONContextNode.this.getGraph(), JSONContextNode.this, id, arcXri);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.id);

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XRI_SS_CONTEXT.toString());
		if (jsonArrayContexts == null) return new EmptyIterator<ContextNode> ();
		if (jsonArrayContexts.size() < 1) return new EmptyIterator<ContextNode> ();

		final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayContexts.iterator()).list();

		return new ReadOnlyIterator<ContextNode> (new MappingIterator<JsonElement, ContextNode> (entryList.iterator()) {

			@Override
			public ContextNode map(JsonElement jsonElement) {

				XDI3SubSegment arcXri = XDI3SubSegment.create(((JsonPrimitive) jsonElement).getAsString());
				String id = JSONContextNode.this.getContextNodeId(arcXri);

				return new JSONContextNode((JSONGraph) JSONContextNode.this.getGraph(), JSONContextNode.this, id, arcXri);
			}
		});
	}

	@Override
	public void delContextNode(XDI3SubSegment arcXri) {

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) return;

		List<Relation> relations = new IteratorListMaker<Relation> (contextNode.getAllRelations()).list();
		for (Relation relation : relations) relation.delete();

		List<Relation> incomingRelations = new IteratorListMaker<Relation> (contextNode.getAllIncomingRelations()).list();
		for (Relation incomingRelation : incomingRelations) incomingRelation.delete();

		String id = this.getContextNodeId(arcXri);

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.id);

		JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XRI_SS_CONTEXT.toString());
		if (jsonArrayContexts == null) { jsonArrayContexts = new JsonArray(); jsonObject.add(XDIConstants.XRI_SS_CONTEXT.toString(), jsonArrayContexts); }
		new IteratorRemover<JsonElement> (jsonArrayContexts.iterator(), new JsonPrimitive(arcXri.toString())).remove();

		((JSONGraph) this.getGraph()).jsonDelete(id);
		((JSONGraph) this.getGraph()).jsonSave(this.id, jsonObject);
	}

	@Override
	public Relation setRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode);

		((JSONGraph) this.getGraph()).jsonSaveToArray(this.id, arcXri.toString(), new JsonPrimitive(targetContextNode.getXri().toString()));
		((JSONGraph) this.getGraph()).jsonSaveToArray(targetContextNode.getXri().toString(), "/" + arcXri.toString(), new JsonPrimitive(this.getXri().toString()));

		return new JSONRelation(this, arcXri, targetContextNode.getXri());
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.id);

		final Set<Entry<String, JsonElement>> entrySet = new HashSet<Entry<String, JsonElement>> (jsonObject.entrySet());

		return new DescendingIterator<Entry<String, JsonElement>, Relation> (entrySet.iterator()) {

			@Override
			public Iterator<Relation> descend(Entry<String, JsonElement> entry) {

				if (entry.getKey().startsWith("/")) return null;

				final XDI3Segment arcXri = XDI3Segment.create(entry.getKey());
				if (XDIConstants.XRI_SS_CONTEXT.equals(arcXri)) return null;
				if (XDIConstants.XRI_SS_LITERAL.equals(arcXri)) return null;

				JsonArray jsonArrayRelations = (JsonArray) entry.getValue();

				final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayRelations.iterator()).list();

				return new ReadOnlyIterator<Relation> (new MappingIterator<JsonElement, Relation> (entryList.iterator()) {

					@Override
					public Relation map(JsonElement jsonElement) {

						XDI3Segment targetContextNodeXri = XDI3Segment.create(((JsonPrimitive) jsonElement).getAsString());

						return new JSONRelation(JSONContextNode.this, arcXri, targetContextNodeXri);
					}
				});
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getIncomingRelations() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.id);

		final Set<Entry<String, JsonElement>> entrySet = new HashSet<Entry<String, JsonElement>> (jsonObject.entrySet());

		return new DescendingIterator<Entry<String, JsonElement>, Relation> (entrySet.iterator()) {

			@Override
			public Iterator<Relation> descend(Entry<String, JsonElement> entry) {

				if (! entry.getKey().startsWith("/")) return null;

				final XDI3Segment arcXri = XDI3Segment.create(entry.getKey().substring(1));

				JsonArray jsonArrayIncomingRelations = (JsonArray) entry.getValue();

				final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayIncomingRelations.iterator()).list();

				return new ReadOnlyIterator<Relation> (new MappingIterator<JsonElement, Relation> (entryList.iterator()) {

					@Override
					public Relation map(JsonElement jsonElement) {

						XDI3Segment contextNodeXri = XDI3Segment.create(((JsonPrimitive) jsonElement).getAsString());

						ContextNode contextNode = JSONContextNode.this.getGraph().getDeepContextNode(contextNodeXri);

						return new JSONRelation(contextNode, arcXri, JSONContextNode.this.getXri());
					}
				});
			}
		};
	}

	@Override
	public void delRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		ContextNode targetContextNode = this.getGraph().getDeepContextNode(targetContextNodeXri);

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.id);

		if (jsonObject != null) {

			JsonArray jsonArrayRelations = jsonObject.getAsJsonArray(arcXri.toString());

			if (jsonArrayRelations != null) {

				new IteratorRemover<JsonElement> (jsonArrayRelations.iterator(), new JsonPrimitive(targetContextNodeXri.toString())).remove();
				if (jsonArrayRelations.size() < 1) jsonObject.remove(arcXri.toString());

				((JSONGraph) this.getGraph()).jsonSave(this.id, jsonObject);
			}
		}

		JsonObject jsonObjectTarget = ((JSONGraph) this.getGraph()).jsonLoad(((JSONContextNode) targetContextNode).id);

		if (jsonObjectTarget != null) {

			JsonArray jsonArrayIncomingRelations = jsonObjectTarget.getAsJsonArray("/" + arcXri.toString());

			if (jsonArrayIncomingRelations != null) {

				new IteratorRemover<JsonElement> (jsonArrayIncomingRelations.iterator(), new JsonPrimitive(this.getXri().toString())).remove();
				if (jsonArrayIncomingRelations.size() < 1) jsonObjectTarget.remove("/" + arcXri.toString());

				((JSONGraph) this.getGraph()).jsonSave(((JSONContextNode) targetContextNode).id, jsonObjectTarget);
			}
		}
	}

	@Override
	public Literal setLiteral(Object literalData) {

		this.checkLiteral(literalData);

		((JSONGraph) this.getGraph()).jsonSaveToObject(this.id, XDIConstants.XRI_SS_LITERAL.toString(), AbstractLiteral.literalDataToJsonElement(literalData));

		return new JSONLiteral(this);
	}

	@Override
	public Literal getLiteral() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.id);

		if (! jsonObject.has(XDIConstants.XRI_SS_LITERAL.toString())) return null;

		return new JSONLiteral(this);
	}

	@Override
	public void delLiteral() {

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(this.id);

		jsonObject.remove(XDIConstants.XRI_SS_LITERAL.toString());

		((JSONGraph) this.getGraph()).jsonSave(this.id, jsonObject);
	}

	/*
	 * Helper methods
	 */

	private String getContextNodeId(XDI3SubSegment arcXri) {

		return (this.isRootContextNode() ? "" : this.id) + arcXri.toString();
	}
}
