package xdi2.core.io.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.io.readers.XDIJXDReader.Mapping.Term;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.parser.ParserException;

public class XDIJXDReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	private static final Logger log = LoggerFactory.getLogger(XDIJXDReader.class);

	public static final String FORMAT_NAME = "JXD";
	public static final String FILE_EXTENSION = "jxd";
	public static final MimeType MIME_TYPE = null;

	public static final String JXD_MAPPING = "@xdi";
	public static final String JXD_ID = "@id";
	public static final String JXD_TYPE = "@type";
	public static final String JXD_GRAPH = "@graph";

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJXDReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	public void read(ContextNode contextNode, JsonObject jsonGraphObject, Mapping mapping, State state) throws IOException, Xdi2ParseException {

		// read mapping

		JsonObject jsonObjectMapping = jsonGraphObject.getAsJsonObject(JXD_MAPPING);

		if (jsonObjectMapping == null) {

			if (mapping == null) {

				mapping = Mapping.empty();
			}
		} else {

			if (mapping == null) {

				mapping = Mapping.create(jsonObjectMapping);
			} else if (jsonObjectMapping != null) {

				mapping = Mapping.add(mapping, jsonObjectMapping);
			}
		}

		// read id

		XDIAddress id = jsonGraphObject.has(JXD_ID) ? makeXDIAddress(jsonGraphObject.get(JXD_ID).getAsString(), state) : null;

		if (id != null) {

			contextNode = contextNode.setDeepContextNode(id);
		}

		// read type

		XDIAddress type = null;

		String typeString = jsonGraphObject.has(JXD_TYPE) ? jsonGraphObject.get(JXD_TYPE).getAsString() : null;
		Term typeTerm = typeString == null ? null : mapping.getTerm(typeString);
		if (typeTerm != null && typeTerm.getId() != null) type = typeTerm.getId();
		else if (typeString != null) type = makeXDIAddress(typeString, state);

		if (type != null && ! JXD_ID.equals(type.toString()) && ! JXD_GRAPH.equals(type.toString())) {

			Dictionary.setContextNodeType(contextNode, type);
		}

		// parse graph

		for (Entry<String, JsonElement> entry : jsonGraphObject.entrySet()) {

			String entryString = entry.getKey();
			JsonElement entryJsonElement = entry.getValue();

			if (JXD_MAPPING.equals(entryString)) continue;
			if (JXD_ID.equals(entryString)) continue;
			if (JXD_TYPE.equals(entryString)) continue;

			// look up entry term

			Term entryTerm = mapping.getTerm(entryString);

			// read entry id

			XDIAddress entryId = null;

			if (entryTerm != null && entryTerm.getId() != null) entryId = entryTerm.getId();
			else entryId = makeXDIAddress(entryString, state);

			// read entry type

			XDIAddress entryType = null;

			if (entryTerm != null && entryTerm.getType() != null) entryType = entryTerm.getType();

			if (entryJsonElement instanceof JsonObject) {

				JsonObject entryJsonObject = (JsonObject) entryJsonElement;

				String entryTypeString = entryJsonObject.has(JXD_TYPE) ? entryJsonObject.get(JXD_TYPE).getAsString() : null;
				Term entryTypeTerm = entryTypeString == null ? null : mapping.getTerm(entryTypeString);
				if (entryTypeTerm != null && entryTypeTerm.getId() != null) type = entryTypeTerm.getId();
				else if (entryTypeString != null) entryType = makeXDIAddress(entryTypeString, state);
			}

			// context or relation or inner root or literal?

			if (entryType != null && JXD_GRAPH.equals(entryType.toString())) {

				// inner root

				XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(contextNode);
				ContextNode nestedContextNode = xdiContext.getXdiInnerRoot(entryId, true).getContextNode();

				if (entryJsonElement instanceof JsonObject) {

					this.read(nestedContextNode, (JsonObject) entryJsonElement, mapping, state);
				}
			} else if ((entryType != null && JXD_ID.equals(entryType.toString())) || entryType != null) {

				if (entryJsonElement instanceof JsonPrimitive && ((JsonPrimitive) entryJsonElement).isString()) {

					// one relation

					XDIAddress targetXDIAddress = makeXDIAddress(entryJsonElement.getAsString(), state);

					contextNode.setRelation(entryId, targetXDIAddress);
				} else if (entryJsonElement instanceof JsonArray) {

					// multiple relations

					for (JsonElement jsonEntryArrayElement : ((JsonArray) entryJsonElement)) {

						if (jsonEntryArrayElement instanceof JsonPrimitive && ((JsonPrimitive) jsonEntryArrayElement).isString()) {

							XDIAddress targetXDIAddress = makeXDIAddress(jsonEntryArrayElement.getAsString(), state);

							contextNode.setRelation(entryId, targetXDIAddress);
						}
					}
				} else if (entryJsonElement instanceof JsonObject) {

					// nested context node

					ContextNode nestedContextNode = contextNode.setDeepContextNode(entryId);

					if (entryJsonElement instanceof JsonObject) {

						this.read(nestedContextNode, (JsonObject) entryJsonElement, mapping, state);
					}
				}
			} else {

				// literal

				Object literalData = AbstractLiteralNode.jsonElementToLiteralData(entryJsonElement);

				LiteralNode literalNode = contextNode.setDeepContextNode(entryId).setLiteralNode(literalData);

				if (entryType != null && ! JXD_ID.equals(entryType.toString()) && ! JXD_GRAPH.equals(entryType.toString())) {

					Dictionary.setContextNodeType(literalNode.getContextNode(), entryType);
				}
			}
		}
	}

	private void read(Graph graph, BufferedReader bufferedReader, State state) throws IOException, Xdi2ParseException {

		JsonElement graphJsonElement = gson.getAdapter(JsonObject.class).fromJson(bufferedReader);

		if (graphJsonElement instanceof JsonObject) {

			this.read(graph.getRootContextNode(), (JsonObject) graphJsonElement, null, state);
		} else if (graphJsonElement instanceof JsonArray) {

			for (JsonElement graphEntryJsonElement : ((JsonArray) graphJsonElement)) {

				if (! (graphEntryJsonElement instanceof JsonObject)) throw new Xdi2ParseException("JSON array must only contain objects: " + graphJsonElement);

				this.read(graph.getRootContextNode(), (JsonObject) graphEntryJsonElement, null, state);
			}
		} else {

			throw new Xdi2ParseException("JSON must be an object or array: " + graphJsonElement);
		}
	}

	@Override
	public Reader read(Graph graph, Reader reader) throws IOException, Xdi2ParseException {

		State state = new State();

		try {

			this.read(graph, new BufferedReader(reader), state);
		} catch (Xdi2GraphException ex) {

			throw new Xdi2ParseException("Graph problem: " + ex.getMessage(), ex);
		} catch (ParserException ex) {

			throw new Xdi2ParseException("Cannot parse string " + state.lastString + ": " + ex.getMessage(), ex);
		}

		return reader;
	}

	private static class State {

		private String lastString;
	}

	private static XDIAddress makeXDIAddress(String addressString, State state) {

		state.lastString = addressString;
		return XDIAddress.create(addressString);
	}

	public static class Mapping {

		private Map<String, Term> terms;

		public Mapping(Map<String, Term> terms) {

			this.terms = terms;
		}

		public static Mapping empty() {

			Mapping mapping = new Mapping(new HashMap<String, Term> ());

			return mapping;
		}

		public static Mapping create(JsonObject jsonObjectMapping) {

			Mapping mapping = new Mapping(new HashMap<String, Term> ());
			mapping.add(jsonObjectMapping);

			return mapping;
		}

		public static Mapping add(Mapping baseContext, JsonObject jsonObjectMapping) {

			Mapping mapping = new Mapping(baseContext.terms);
			mapping.add(jsonObjectMapping);

			return mapping;
		}

		public void add(JsonObject jsonObjectMapping) {

			for (Entry<String, JsonElement> entry : jsonObjectMapping.entrySet()) {

				String name = entry.getKey();
				JsonElement jsonElement = entry.getValue();

				Term term = null;

				if (jsonElement instanceof JsonPrimitive) term = Term.create(name, (JsonPrimitive) jsonElement);
				if (jsonElement instanceof JsonObject) term = Term.create(name, (JsonObject) jsonElement);

				if (term == null) throw new IllegalArgumentException("Invalid term: " + name);

				this.terms.put(name, term);
			}
		}

		public Term getTerm(String name) {

			return this.terms.get(name);
		}

		public static class Term {

			private String name;
			private XDIAddress id;
			private XDIAddress type;

			public Term(String name, XDIAddress id, XDIAddress type) {

				if (name == null) throw new IllegalArgumentException("Term has no name: " + id);

				this.name = name;
				this.id = id;
				this.type = type;
			}

			public static Term create(String name, JsonPrimitive jsonPrimitive) {

				XDIAddress id = XDIAddress.create(jsonPrimitive.getAsString());
				XDIAddress type = null;

				return new Term(name, id, type);
			}

			public static Term create(String name, JsonObject jsonObject) {

				XDIAddress id = jsonObject.has(JXD_ID) ? XDIAddress.create(jsonObject.get(JXD_ID).getAsString()) : null;
				XDIAddress type = jsonObject.has(JXD_TYPE) ? XDIAddress.create(jsonObject.get(JXD_TYPE).getAsString()) : null;

				return new Term(name, id, type);
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
}
