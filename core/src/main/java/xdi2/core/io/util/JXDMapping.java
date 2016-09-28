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
		mapping.add(jsonObjectMapping);

		return mapping;
	}

	public static JXDMapping add(JXDMapping baseMapping, JsonObject jsonObjectMapping) {

		JXDMapping mapping = new JXDMapping(baseMapping.terms);
		mapping.add(jsonObjectMapping);

		return mapping;
	}

	public void add(JsonObject jsonObjectMapping) {

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

		public String getName() {

			return this.name;
		}

		public XDIAddress getId() {

			return this.id;
		}

		public XDIAddress getType() {

			return this.type;
		}
	}
}
