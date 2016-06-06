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
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.io.AbstractXDIReader;
import xdi2.core.io.MimeType;
import xdi2.core.io.readers.XDIJSONXDReader.Context.Term;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.parser.ParserException;

public class XDIJSONXDReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	private static final Logger log = LoggerFactory.getLogger(XDIJSONXDReader.class);

	public static final String FORMAT_NAME = "JSON-XD";
	public static final String FILE_EXTENSION = "jsonxd";
	public static final MimeType MIME_TYPE = null;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

	public XDIJSONXDReader(Properties parameters) {

		super(parameters);
	}

	@Override
	protected void init() {

	}

	public void read(ContextNode contextNode, JsonObject jsonGraphObject, Context context, State state) throws IOException, Xdi2ParseException {

		// read context

		JsonObject jsonContextObject = jsonGraphObject.getAsJsonObject("$context");

		if (context == null) {

			if (jsonContextObject == null) throw new Xdi2ParseException("No $context found.");

			context = Context.create(jsonContextObject);
		} else if (jsonContextObject != null) {

			context = Context.add(context, jsonContextObject);
		}

		// read id and type

		XDIAddress id = jsonGraphObject.has("$id") ? makeXDIAddress(jsonGraphObject.get("$id").getAsString(), state) : null;
		String typeString = jsonGraphObject.has("$type") ? jsonGraphObject.get("$type").getAsString() : null;

		XDIAddress type = null;

		Term typeTerm = typeString == null ? null : context.getTerm(typeString);
		if (typeTerm != null) type = typeTerm.getId();
		else if (typeString != null) type = makeXDIAddress(typeString, state);

		if (id != null) {

			contextNode = contextNode.setDeepContextNode(id);
		}

		if (type != null) {

			Dictionary.setContextNodeType(contextNode, type);
		}

		// parse graph

		for (Entry<String, JsonElement> entry : jsonGraphObject.entrySet()) {

			String key = entry.getKey();
			JsonElement jsonEntryElement = entry.getValue();

			if ("$context".equals(key)) continue;
			if ("$id".equals(key)) continue;
			if ("$type".equals(key)) continue;

			// look up term

			Term term = context.getTerm(key);
			if (term == null) throw new Xdi2ParseException("Term '" + key + "' not defined.");

			// context or relation or literal?

			if (XDIAddress.create("()").equals(term.getType())) {

				XDIAddress XDIadress = term.getId();

				ContextNode childContextNode = contextNode.setDeepContextNode(XDIadress);

				if (jsonEntryElement instanceof JsonObject) {

					this.read(childContextNode, (JsonObject) jsonEntryElement, context, state);
				}
			} else if (XDIAddress.create("$id").equals(term.getType())) {

				XDIAddress XDIaddress = term.getId();

				if (jsonEntryElement instanceof JsonPrimitive && ((JsonPrimitive) jsonEntryElement).isString()) {

					XDIAddress targetXDIAddress = makeXDIAddress(jsonEntryElement.getAsString(), state);
					contextNode.setRelation(XDIaddress, targetXDIAddress);
				} else if (jsonEntryElement instanceof JsonArray) {

					for (JsonElement jsonEntryArrayElement : ((JsonArray) jsonEntryElement)) {

						if (jsonEntryArrayElement instanceof JsonPrimitive && ((JsonPrimitive) jsonEntryArrayElement).isString()) {

							XDIAddress targetXDIAddress = makeXDIAddress(jsonEntryArrayElement.getAsString(), state);
							contextNode.setRelation(XDIaddress, targetXDIAddress);
						}
					}
				}
			} else {

				XDIAddress XDIaddress = term.getId();
				Object literalData = AbstractLiteralNode.jsonElementToLiteralData(jsonEntryElement);

				LiteralNode literalNode = contextNode.setDeepContextNode(XDIaddress).setLiteralNode(literalData);

				if (term.getType() != null) {

					Dictionary.setContextNodeType(literalNode.getContextNode(), term.getType());
				}
			}
		}
	}

	private void read(Graph graph, BufferedReader bufferedReader, State state) throws IOException, Xdi2ParseException {

		JsonElement jsonGraphElement = gson.getAdapter(JsonObject.class).fromJson(bufferedReader);

		if (! (jsonGraphElement instanceof JsonObject)) throw new Xdi2ParseException("JSON must be an object: " + jsonGraphElement);

		this.read(graph.getRootContextNode(), (JsonObject) jsonGraphElement, null, state);
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

	public static class Context {

		private Map<String, Term> terms;

		public Context(Map<String, Term> terms) {

			this.terms = terms;
		}

		public static Context create(JsonObject jsonContextObject) {

			Context context = new Context(new HashMap<String, Term> ());
			context.add(jsonContextObject);

			return context;
		}

		public static Context add(Context baseContext, JsonObject jsonContextObject) {

			Context context = new Context(baseContext.terms);
			context.add(jsonContextObject);

			return context;
		}

		public void add(JsonObject jsonContextObject) {

			for (Entry<String, JsonElement> entry : jsonContextObject.entrySet()) {

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
				if (id == null) throw new IllegalArgumentException("Term has no $id: " + name);

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

				XDIAddress id = jsonObject.has("$id") ? XDIAddress.create(jsonObject.get("$id").getAsString()) : null;
				XDIAddress type = jsonObject.has("$type") ? XDIAddress.create(jsonObject.get("$type").getAsString()) : null;

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
