package xdi2.core.io.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import xdi2.core.syntax.XDIAddress;

public class JXDMapping {

	private Map<String, JXDTerm> terms;

	public JXDMapping(Map<String, JXDTerm> terms) {

		this.terms = terms;
	}

	public static JXDMapping empty() {

		JXDMapping mapping = new JXDMapping(new HashMap<String, JXDTerm> ());

		return mapping;
	}

	public static JXDMapping create(JsonObject jsonObjectMapping) {

		JXDMapping mapping = new JXDMapping(new HashMap<String, JXDTerm> ());
		mapping.merge(jsonObjectMapping);

		return mapping;
	}

	public static JXDMapping merge(JXDMapping baseMapping, JsonObject jsonObjectMapping) {

		JXDMapping mapping = new JXDMapping(baseMapping.terms);
		mapping.merge(jsonObjectMapping);

		return mapping;
	}

	public void merge(JXDMapping otherMapping) {

		this.terms.putAll(otherMapping.terms);
	}

	public void unmerge(JXDMapping otherMapping) {

		for (String key : otherMapping.terms.keySet()) {

			this.terms.remove(key);
		}
	}

	public JXDTerm addOrReuse(JXDTerm term) {

		// don't add term if it has no additional information

		if (term.getId() != null && term.getName().equals(term.getId().toString())) term.setId(null);
		if (term.getType() == null && term.getId() == null) return term;

		// see if we can re-use a term

		for (JXDTerm existingTerm : this.terms.values()) {

			if (existingTerm.equalsIdAndType(term)) return existingTerm;
		}

		// choose term name

		for (int i=1; ; i++) {

			// don't use this name if it exists already

			String termName = term.getName() + (i > 1 ? ("-" + String.valueOf(i)) : "");
			JXDTerm existingTerm = this.terms.get(termName);
			if (existingTerm != null) continue;

			// add and return term

			term.setName(termName);
			this.terms.put(termName, term);
			return term;
		}
	}

	public void merge(JsonObject jsonObjectMapping) {

		for (Entry<String, JsonElement> entry : jsonObjectMapping.entrySet()) {

			String name = entry.getKey();
			JsonElement jsonElement = entry.getValue();

			JXDTerm term = null;

			if (jsonElement instanceof JsonPrimitive) term = JXDTerm.create(name, (JsonPrimitive) jsonElement);
			if (jsonElement instanceof JsonObject) term = JXDTerm.create(name, (JsonObject) jsonElement);

			if (term == null) throw new IllegalArgumentException("Invalid term: " + name);

			this.terms.put(name, term);
		}
	}

	private JsonObject jsonObjectMapping;

	public JsonObject begin() {

		this.jsonObjectMapping = new JsonObject();
		return this.jsonObjectMapping;
	}

	public boolean finish() {

		for (JXDTerm term : this.terms.values()) {

			if (term.getId() != null && term.getType() == null) {

				this.jsonObjectMapping.add(term.getName(), new JsonPrimitive(term.getId().toString()));
			} else {

				JsonObject jsonObjectTerm = new JsonObject();
				if (term.getId() != null) jsonObjectTerm.add(JXDConstants.JXD_ID, new JsonPrimitive(term.getId().toString()));
				if (term.getType() != null) jsonObjectTerm.add(JXDConstants.JXD_TYPE, new JsonPrimitive(term.getType().toString()));

				this.jsonObjectMapping.add(term.getName(), jsonObjectTerm);
			}
		}

		return ! this.jsonObjectMapping.entrySet().isEmpty();
	}

	public JXDTerm getTerm(String name) {

		return this.terms.get(name);
	}

	public static class JXDTerm {

		private String name;
		private XDIAddress id;
		private XDIAddress type;

		public JXDTerm(String name, XDIAddress id, XDIAddress type) {

			if (name == null) throw new IllegalArgumentException("Term has no name: " + id);

			this.name = name;
			this.id = id;
			this.type = type;
		}

		public static JXDTerm create(String name, JsonPrimitive jsonPrimitive) {

			XDIAddress id = XDIAddress.create(jsonPrimitive.getAsString());
			XDIAddress type = null;

			return new JXDTerm(name, id, type);
		}

		public static JXDTerm create(String name, JsonObject jsonObject) {

			XDIAddress id = jsonObject.has(JXDConstants.JXD_ID) ? XDIAddress.create(jsonObject.get(JXDConstants.JXD_ID).getAsString()) : null;
			XDIAddress type = jsonObject.has(JXDConstants.JXD_TYPE) ? XDIAddress.create(jsonObject.get(JXDConstants.JXD_TYPE).getAsString()) : null;

			return new JXDTerm(name, id, type);
		}

		public boolean equalsIdAndType(JXDTerm other) {

			if (this.getId() == null && other.getId() != null) return false;
			if (this.getId() != null && other.getId() == null) return false;
			if (this.getId() != null && other.getId() != null && ! this.getId().equals(other.getId())) return false;

			if (this.getType() == null && other.getType() != null) return false;
			if (this.getType() != null && other.getType() == null) return false;
			if (this.getType() != null && other.getType() != null && ! this.getType().equals(other.getType())) return false;

			return true;
		}

		public String getName() {

			return this.name;
		}

		private void setName(String name) {

			this.name = name;
		}

		public XDIAddress getId() {

			return this.id;
		}

		private void setId(XDIAddress id) {

			this.id = id;
		}

		public XDIAddress getType() {

			return this.type;
		}

		private void setType(XDIAddress type) {

			this.type = type;
		}
	}
}
