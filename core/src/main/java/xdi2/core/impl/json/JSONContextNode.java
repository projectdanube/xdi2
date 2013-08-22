package xdi2.core.impl.json;

import java.io.IOException;
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
import xdi2.core.exceptions.Xdi2RuntimeException;
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

	private static final Logger log = LoggerFactory.getLogger(JSONContextNode.class);

	private JSONStore jsonStore;
	private String id;

	private XDI3SubSegment arcXri;

	JSONContextNode(JSONGraph graph, JSONContextNode contextNode, JSONStore jsonStore, String id, XDI3SubSegment arcXri) {

		super(graph, contextNode);

		this.jsonStore = jsonStore;
		this.id = id;

		this.arcXri = arcXri;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	private synchronized ContextNode createContextNodeInternal(XDI3SubSegment arcXri) {

		JsonObject jsonObject = this.loadJson();
		if (jsonObject == null) jsonObject = new JsonObject();

		String id = this.getContextNodeId(arcXri);

		JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XRI_SS_CONTEXT.toString());
		if (jsonArrayContexts == null) { jsonArrayContexts = new JsonArray(); jsonObject.add(XDIConstants.XRI_SS_CONTEXT.toString(), jsonArrayContexts); }
		if (! new IteratorContains<JsonElement> (jsonArrayContexts.iterator(), new JsonPrimitive(arcXri.toString())).contains()) jsonArrayContexts.add(new JsonPrimitive(arcXri.toString()));

		this.saveJson(jsonObject);

		JSONContextNode jsonContextNode = new JSONContextNode((JSONGraph) this.getGraph(), this, this.jsonStore, id, arcXri);

		return jsonContextNode;
	}

	@Override
	public XDI3SubSegment getArcXri() {

		return this.arcXri;
	}

	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri, true);

		return this.createContextNodeInternal(arcXri);
	}

	@Override
	public ContextNode setContextNode(XDI3SubSegment arcXri) {

		this.checkContextNode(arcXri, false);

		return this.createContextNodeInternal(arcXri);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		JsonObject jsonObject = this.loadJson();
		if (jsonObject == null) return new EmptyIterator<ContextNode> ();

		final JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XRI_SS_CONTEXT.toString());
		if (jsonArrayContexts == null) return new EmptyIterator<ContextNode> ();
		if (jsonArrayContexts.size() < 1) return new EmptyIterator<ContextNode> ();

		final List<JsonElement> entryList = new IteratorListMaker<JsonElement> (jsonArrayContexts.iterator()).list();

		return new ReadOnlyIterator<ContextNode> (new MappingIterator<JsonElement, ContextNode> (entryList.iterator()) {

			@Override
			public ContextNode map(JsonElement jsonElement) {

				XDI3SubSegment arcXri = XDI3SubSegment.create(((JsonPrimitive) jsonElement).getAsString());
				String id = JSONContextNode.this.getContextNodeId(arcXri);

				return new JSONContextNode((JSONGraph) JSONContextNode.this.getGraph(), JSONContextNode.this, JSONContextNode.this.jsonStore, id, arcXri);
			}
		});
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) return;

		List<Relation> relations = new IteratorListMaker<Relation> (contextNode.getAllRelations()).list();
		for (Relation relation : relations) relation.delete();

		List<Relation> incomingRelations = new IteratorListMaker<Relation> (contextNode.getAllIncomingRelations()).list();
		for (Relation incomingRelation : incomingRelations) incomingRelation.delete();

		String id = this.getContextNodeId(arcXri);

		JsonObject jsonObject = this.loadJson();

		JsonArray jsonArrayContexts = jsonObject.getAsJsonArray(XDIConstants.XRI_SS_CONTEXT.toString());
		if (jsonArrayContexts == null) { jsonArrayContexts = new JsonArray(); jsonObject.add(XDIConstants.XRI_SS_CONTEXT.toString(), jsonArrayContexts); }
		new IteratorRemover<JsonElement> (jsonArrayContexts.iterator(), new JsonPrimitive(arcXri.toString())).remove();

		this.deleteJson(id);
		this.saveJson(jsonObject);
	}

	@Override
	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode, true);

		JsonObject jsonObject = this.loadJson();
		JsonObject jsonObjectTarget = ((JSONContextNode) targetContextNode).loadJson();
		if (jsonObject == null) jsonObject = new JsonObject();
		if (jsonObjectTarget == null) jsonObjectTarget = new JsonObject();

		JsonArray jsonArrayRelations = jsonObject.getAsJsonArray(arcXri.toString());

		if (jsonArrayRelations == null) {

			jsonArrayRelations = new JsonArray();
			jsonObject.add(arcXri.toString(), jsonArrayRelations);
		}

		jsonArrayRelations.add(new JsonPrimitive(targetContextNode.getXri().toString()));

		JsonArray jsonArrayIncomingRelations = jsonObjectTarget.getAsJsonArray("_" + arcXri.toString());

		if (jsonArrayIncomingRelations == null) {

			jsonArrayIncomingRelations = new JsonArray();
			jsonObjectTarget.add("_" + arcXri.toString(), jsonArrayIncomingRelations);
		}

		jsonArrayIncomingRelations.add(new JsonPrimitive(this.getXri().toString()));

		this.saveJson(jsonObject);
		((JSONContextNode) targetContextNode).saveJson(jsonObjectTarget);

		return new JSONRelation(this, arcXri, targetContextNode.getXri());
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		JsonObject jsonObject = this.loadJson();
		if (jsonObject == null) return new EmptyIterator<Relation> ();

		final Set<Entry<String, JsonElement>> entrySet = new HashSet<Entry<String, JsonElement>> (jsonObject.entrySet());

		return new DescendingIterator<Entry<String, JsonElement>, Relation> (entrySet.iterator()) {

			@Override
			public Iterator<Relation> descend(Entry<String, JsonElement> entry) {

				if (entry.getKey().startsWith("_")) return null;

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

		JsonObject jsonObject = this.loadJson();
		if (jsonObject == null) return new EmptyIterator<Relation> ();

		final Set<Entry<String, JsonElement>> entrySet = new HashSet<Entry<String, JsonElement>> (jsonObject.entrySet());

		return new DescendingIterator<Entry<String, JsonElement>, Relation> (entrySet.iterator()) {

			@Override
			public Iterator<Relation> descend(Entry<String, JsonElement> entry) {

				if (! entry.getKey().startsWith("_")) return null;

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
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		ContextNode targetContextNode = this.getGraph().getDeepContextNode(targetContextNodeXri);

		JsonObject jsonObject = this.loadJson();

		if (jsonObject != null) {

			JsonArray jsonArrayRelations = jsonObject.getAsJsonArray(arcXri.toString());
			new IteratorRemover<JsonElement> (jsonArrayRelations.iterator(), new JsonPrimitive(targetContextNodeXri.toString())).remove();
			if (jsonArrayRelations.size() < 1) jsonObject.remove(arcXri.toString());

			this.saveJson(jsonObject);
		}

		JsonObject jsonObjectTarget = ((JSONContextNode) targetContextNode).loadJson();

		if (jsonObjectTarget != null) {

			JsonArray jsonArrayIncomingRelations = jsonObjectTarget.getAsJsonArray("_" + arcXri.toString());
			new IteratorRemover<JsonElement> (jsonArrayIncomingRelations.iterator(), new JsonPrimitive(this.getXri().toString())).remove();
			if (jsonArrayIncomingRelations.size() < 1) jsonObjectTarget.remove("_" + arcXri.toString());

			((JSONContextNode) targetContextNode).saveJson(jsonObjectTarget);
		}
	}

	@Override
	public Literal createLiteral(Object literalData) {

		this.checkLiteral(literalData, true);

		JsonObject jsonObject = this.loadJson();
		if (jsonObject == null) jsonObject = new JsonObject();

		jsonObject.add(XDIConstants.XRI_SS_LITERAL.toString(), AbstractLiteral.literalDataToJsonElement(literalData));

		this.saveJson(jsonObject);

		return new JSONLiteral(this);
	}

	@Override
	public Literal getLiteral() {

		JsonObject jsonObject = this.loadJson();
		if (jsonObject == null) return null;

		if (! jsonObject.has(XDIConstants.XRI_SS_LITERAL.toString())) return null;

		return new JSONLiteral(this);
	}

	@Override
	public void deleteLiteral() {

		JsonObject jsonObject = this.loadJson();
		if (jsonObject == null) return;

		jsonObject.remove(XDIConstants.XRI_SS_LITERAL.toString());

		this.saveJson(jsonObject);
	}

	/*
	 * Helper methods
	 */

	private String getContextNodeId(XDI3SubSegment arcXri) {

		return (this.isRootContextNode() ? "" : this.id) + arcXri.toString();
	}

	JsonObject loadJson() {

		if (log.isTraceEnabled()) log.trace("Loading JSON " + this.id);

		try {

			return JSONContextNode.this.jsonStore.load(this.id);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot load JSON at " + this.id + ": " + ex.getMessage(), ex);
		}
	}

	void saveJson(JsonObject jsonObject) {

		if (log.isTraceEnabled()) log.trace("Saving JSON " + this.id);

		try {

			this.jsonStore.save(this.id, jsonObject);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot save JSON at " + this.id + ": " + ex.getMessage(), ex);
		}
	}

	void deleteJson(String id) {

		if (log.isTraceEnabled()) log.trace("Deleting JSON " + id);

		try {

			this.jsonStore.delete(id);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot delete JSON at " + id + ": " + ex.getMessage(), ex);
		}
	}
}
