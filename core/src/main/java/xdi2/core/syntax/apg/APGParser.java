package xdi2.core.syntax.apg;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIIdentifier;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.syntax.apg.APGGrammar.RuleNames;

import com.coasttocoastresearch.apg.Ast;
import com.coasttocoastresearch.apg.Ast.AstCallback;
import com.coasttocoastresearch.apg.Grammar;
import com.coasttocoastresearch.apg.Parser.Result;

/**
 * An XDI parser generated automatically by the APG parser generator based on an ABNF.
 * @see http://www.coasttocoastresearch.com/
 */
public class APGParser extends xdi2.core.syntax.AbstractParser implements xdi2.core.syntax.Parser {

	private static final Logger log = LoggerFactory.getLogger(APGParser.class);

	private static Grammar grammar = APGGrammar.getInstance();

	private static com.coasttocoastresearch.apg.Parser parser_xdi_statement = null;
	private static com.coasttocoastresearch.apg.Parser parser_xdi_address = null;
	private static com.coasttocoastresearch.apg.Parser parser_arc_address = null;
	private static com.coasttocoastresearch.apg.Parser parser_xref = null;

	/*  TODO: why can't we create the parse once and re-use it? seems to cause random ParserExceptions and NullPointerExceptions

	private static Parser parser_xdi_statement = makeParser(XDI3Grammar.RuleNames.XDI_STATEMENT);
	private static Parser parser_xdi_segment = makeParser(XDI3Grammar.RuleNames.XDI_SEGMENT);
	private static Parser parser_subseg = makeParser(XDI3Grammar.RuleNames.SUBSEG);
	private static Parser parser_xref = makeParser(XDI3Grammar.RuleNames.XREF); */

	private static RuleNames[] rulenames = new RuleNames[] {

		APGGrammar.RuleNames.CONTEXTUAL_STATEMENT,
		APGGrammar.RuleNames.RELATIONAL_STATEMENT,
		APGGrammar.RuleNames.LITERAL_STATEMENT,
		APGGrammar.RuleNames.STMT,
		APGGrammar.RuleNames.XDI_ADDRESS,
		APGGrammar.RuleNames.ARC_ADDRESS,
		APGGrammar.RuleNames.COMMON_ARC_ADDRESS,
		APGGrammar.RuleNames.LITERAL_ARC_ADDRESS,
		APGGrammar.RuleNames.PEER_ROOT,
		APGGrammar.RuleNames.INNER_ROOT,
		APGGrammar.RuleNames.ENTITY_SINGLETON,
		APGGrammar.RuleNames.ENTITY_COLLECTION,
		APGGrammar.RuleNames.ENTITY_MEMBER,
		APGGrammar.RuleNames.ATTR_SINGLETON,
		APGGrammar.RuleNames.ATTR_COLLECTION,
		APGGrammar.RuleNames.ATTR_MEMBER,
		APGGrammar.RuleNames.XREF,
		APGGrammar.RuleNames.JSON_VALUE,
		APGGrammar.RuleNames.XDI_NAME,
		APGGrammar.RuleNames.IRI_CHAR
	};

	private static List<RuleNames> rulenames_XDIStatement = Arrays.asList(new RuleNames[] {

			APGGrammar.RuleNames.CONTEXTUAL_STATEMENT,
			APGGrammar.RuleNames.RELATIONAL_STATEMENT,
			APGGrammar.RuleNames.LITERAL_STATEMENT
	});

	private static List<RuleNames> rulenames_XDIAddress = Arrays.asList(new RuleNames[] {

			APGGrammar.RuleNames.XDI_ADDRESS
	});

	private static List<RuleNames> rulenames_XDIArc = Arrays.asList(new RuleNames[] {

			APGGrammar.RuleNames.ARC_ADDRESS,
			APGGrammar.RuleNames.COMMON_ARC_ADDRESS,
			APGGrammar.RuleNames.LITERAL_ARC_ADDRESS,
			APGGrammar.RuleNames.PEER_ROOT,
			APGGrammar.RuleNames.INNER_ROOT,
			APGGrammar.RuleNames.ENTITY_SINGLETON,
			APGGrammar.RuleNames.ENTITY_COLLECTION,
			APGGrammar.RuleNames.ENTITY_MEMBER,
			APGGrammar.RuleNames.ATTR_SINGLETON,
			APGGrammar.RuleNames.ATTR_COLLECTION,
			APGGrammar.RuleNames.ATTR_MEMBER,
	});

	private static List<RuleNames> rulenames_XDIXRef = Arrays.asList(new RuleNames[] {

			APGGrammar.RuleNames.XREF,
	});

	private static List<RuleNames> rulenames_XDIArc_STMT_JSON_VALUE;

	static {

		rulenames_XDIArc_STMT_JSON_VALUE = new ArrayList<RuleNames> ();
		rulenames_XDIArc_STMT_JSON_VALUE.addAll(rulenames_XDIArc);
		rulenames_XDIArc_STMT_JSON_VALUE.add(APGGrammar.RuleNames.STMT);
		rulenames_XDIArc_STMT_JSON_VALUE.add(APGGrammar.RuleNames.JSON_VALUE);
	}

	private static com.coasttocoastresearch.apg.Parser makeParser(RuleNames parserRuleName) {

		com.coasttocoastresearch.apg.Parser parser = new com.coasttocoastresearch.apg.Parser(grammar);
		parser.setStartRule(parserRuleName.ruleID());

		return parser;
	}

	@Override
	public XDIStatement parseXDIStatement(String input) {

		return (XDIStatement) parse(parser_xdi_statement, input, APGGrammar.RuleNames.XDI_STATEMENT, rulenames);
	}

	@Override
	public XDIAddress parseXDIAddress(String input) {

		return (XDIAddress) parse(parser_xdi_address, input, APGGrammar.RuleNames.XDI_ADDRESS, rulenames);
	}

	@Override
	public XDIArc parseXDIArc(String input) {

		return (XDIArc) parse(parser_arc_address, input, APGGrammar.RuleNames.ARC_ADDRESS, rulenames);
	}

	@Override
	public XDIXRef parseXDIXRef(String input) {

		return (XDIXRef) parse(parser_xref, input, APGGrammar.RuleNames.XREF, rulenames);
	}

	private XDIIdentifier parse(com.coasttocoastresearch.apg.Parser parser, String input, RuleNames parseRuleName, RuleNames ruleNames[]) {

		if (parser == null) parser = makeParser(parseRuleName);

		parser.setInputString(input);
		parser.enableAst(false);

		Ast ast = parser.enableAst(true);

		MyAstContext myAstContext = new MyAstContext(null, input);

		try {

			for (RuleNames ruleName : ruleNames) {

				ast.enableRuleNode(ruleName.ruleID(), true);
				ast.setRuleCallback(ruleName.ruleID(), new MyAstCallback(ast, myAstContext, ruleName));
			}
		} catch (Exception ex) {

			throw new xdi2.core.syntax.ParserException(ex.getMessage(), ex);
		}

		Result result;

		try {

			result = parser.parse();
			if (log.isTraceEnabled()) log.trace("Result: " + result);
		} catch (Exception ex) {

			throw new xdi2.core.syntax.ParserException(ex.getMessage(), ex);
		}

		if (! result.success()) {

			throw new xdi2.core.syntax.ParserException("Parser error for 'rule' " + parseRuleName.ruleName() + " (match: " + result.getMatchedPhraseLength() + ", max: " + result.getMaxMatchedPhraseLength() + ")");
		}

		ast.translateAst();

		if (log.isTraceEnabled()) log.trace("AST: " + myAstContext.currentNode.ast());

		return myAstContext.currentNode.xdi;
	}

	private class MyAstCallback extends AstCallback {

		private MyAstContext myAstContext;
		private RuleNames ruleName;

		private MyAstCallback(Ast ast, MyAstContext myAstContext, RuleNames ruleName) {

			super(ast);

			this.myAstContext = myAstContext;
			this.ruleName = ruleName;
		}

		@Override
		public boolean preBranch(int offset, int length) {

			if (log.isTraceEnabled()) log.trace("BRANCH: " + this.ruleName.name() + " " + offset + " " + length);

			this.myAstContext.currentNode = new Node(this.ruleName, this.myAstContext.currentNode);

			return false;
		}

		@Override
		public void postBranch(int offset, int length) {

			if (log.isInfoEnabled()) log.info("POST: " + this.ruleName.name() + " " + offset + " " + length);

			this.myAstContext.currentNode.value = this.myAstContext.input.substring(offset, offset + length);

			if (rulenames_XDIStatement.contains(this.ruleName)) {

				List<Node> nodesXDIArcsSTMT = this.myAstContext.currentNode.findNodes(rulenames_XDIArc_STMT_JSON_VALUE);

				if (APGGrammar.RuleNames.CONTEXTUAL_STATEMENT.equals(this.ruleName)) {

					int i = 0;
					List<XDIArc> subjectXDIarcs = new ArrayList<XDIArc> ();
					while (! APGGrammar.RuleNames.STMT.equals(nodesXDIArcsSTMT.get(i).ruleName)) subjectXDIarcs.add((XDIArc) nodesXDIArcsSTMT.get(i++).xdi);
					i++; i++;
					XDIArc objectXDIarc = (XDIArc) nodesXDIArcsSTMT.get(i).xdi;

					XDIAddress subject = APGParser.this.newXDIAddress(null, subjectXDIarcs);
					String predicate = XDIConstants.STRING_CONTEXT;
					XDIArc object = objectXDIarc;

					this.myAstContext.currentNode.xdi = APGParser.this.newXDIStatement(this.myAstContext.currentNode.value, subject, predicate, object);
				} else if (APGGrammar.RuleNames.RELATIONAL_STATEMENT.equals(this.ruleName)) {

					int i = 0;
					List<XDIArc> subjectXDIarcs = new ArrayList<XDIArc> ();
					while (! APGGrammar.RuleNames.STMT.equals(nodesXDIArcsSTMT.get(i).ruleName)) subjectXDIarcs.add((XDIArc) nodesXDIArcsSTMT.get(i++).xdi);
					i++;
					List<XDIArc> predicateXDIarcs = new ArrayList<XDIArc> ();
					while (! APGGrammar.RuleNames.STMT.equals(nodesXDIArcsSTMT.get(i).ruleName)) predicateXDIarcs.add((XDIArc) nodesXDIArcsSTMT.get(i++).xdi);
					i++;
					List<XDIArc> objectXDIarcs = new ArrayList<XDIArc> ();
					while (i <= nodesXDIArcsSTMT.size()) subjectXDIarcs.add((XDIArc) nodesXDIArcsSTMT.get(i++).xdi);

					XDIAddress subject = APGParser.this.newXDIAddress(null, subjectXDIarcs);
					XDIAddress predicate = APGParser.this.newXDIAddress(null, predicateXDIarcs);
					XDIAddress object = APGParser.this.newXDIAddress(null, objectXDIarcs);

					this.myAstContext.currentNode.xdi = APGParser.this.newXDIStatement(this.myAstContext.currentNode.value, subject, predicate, object);
				} else if (APGGrammar.RuleNames.LITERAL_STATEMENT.equals(this.ruleName)) {

					int i = 0;
					List<XDIArc> subjectXDIarcs = new ArrayList<XDIArc> ();
					while (! APGGrammar.RuleNames.STMT.equals(nodesXDIArcsSTMT.get(i).ruleName)) subjectXDIarcs.add((XDIArc) nodesXDIArcsSTMT.get(i++).xdi);
					i++; i++; i++;
					String objectString = nodesXDIArcsSTMT.get(i).value;

					XDIAddress subject = APGParser.this.newXDIAddress(null, subjectXDIarcs);
					XDIArc predicate = XDIConstants.XDI_ARC_LITERAL;
					Object object = AbstractLiteralNode.stringToLiteralData(objectString);

					this.myAstContext.currentNode.xdi = APGParser.this.newXDIStatement(this.myAstContext.currentNode.value, subject, predicate, object);
				}
			}

			if (rulenames_XDIAddress.contains(this.ruleName)) {

				List<Node> nodesXDIArcs = this.myAstContext.currentNode.findNodes(rulenames_XDIArc);

				List<XDIArc> XDIarcs = new ArrayList<XDIArc> ();
				for (Node nodeArcAddress : nodesXDIArcs) XDIarcs.add((XDIArc) nodeArcAddress.xdi);

				this.myAstContext.currentNode.xdi = APGParser.this.newXDIAddress(this.myAstContext.currentNode.value, XDIarcs);
			}

			if (rulenames_XDIArc.contains(this.ruleName)) {

				Node nodeCs = this.myAstContext.currentNode;
				Node node_xdi_name = this.myAstContext.currentNode.findNode(RuleNames.XDI_NAME);
				Node nodeXDIXref = this.myAstContext.currentNode.findNode(rulenames_XDIXRef);

				Character cs = nodeCs == null ? null : Character.valueOf(nodeCs.value.charAt(0));
				boolean classXs = false;
				boolean attributeXs = false;
				String literal = node_xdi_name == null ? null : node_xdi_name.value;
				XDIXRef xref = nodeXDIXref == null ? null : (XDIXRef) nodeXDIXref.xdi;

				this.myAstContext.currentNode.xdi = APGParser.this.newXDIArc(this.myAstContext.currentNode.value, cs, classXs, attributeXs, literal, xref);
			}

			if (rulenames_XDIXRef.contains(this.ruleName)) {

				List<Node> nodesAddress = this.myAstContext.currentNode.findNodes(RuleNames.XDI_ADDRESS);
				Node nodeIRI = this.myAstContext.currentNode.findNode(RuleNames.XDI_IRI);
				Node nodeLiteral = this.myAstContext.currentNode.findNode(RuleNames.XDI_NAME);

				XDIAddress XDIaddress = nodesAddress.size() != 1 ? null : (XDIAddress) nodesAddress.get(0).xdi;
				XDIAddress partialSubject = nodesAddress.size() != 2 ? null : (XDIAddress) nodesAddress.get(0).xdi;
				XDIAddress partialPredicate = nodesAddress.size() != 2 ? null : (XDIAddress) nodesAddress.get(1).xdi;
				String iri = nodeIRI == null ? null : nodeIRI.value;
				String literal = nodeLiteral == null ? null : nodeLiteral.value;

				this.myAstContext.currentNode.xdi = APGParser.this.newXDIXRef(this.myAstContext.currentNode.value, "", XDIaddress, partialSubject, partialPredicate, iri, literal);
			}

			if (this.myAstContext.currentNode.parent != null) this.myAstContext.currentNode = this.myAstContext.currentNode.parent;
		}
	}

	private static class MyAstContext {

		private Node currentNode;
		private String input;

		private MyAstContext(Node currentNode, String input) {

			this.currentNode = currentNode;
			this.input = input;
		}
	}

	private static class Node {

		private RuleNames ruleName;
		private Node parent;
		private String value;
		private XDIIdentifier xdi;
		private List<Node> children;

		private Node(RuleNames ruleName) {

			this.ruleName = ruleName;
			this.parent = null;
			this.value = null;
			this.xdi = null;
			this.children = new ArrayList<Node> ();
		}

		private Node(RuleNames ruleName, Node parent) {

			this.ruleName = ruleName;
			this.parent = parent;
			this.value = null;
			this.xdi = null;
			this.children = new ArrayList<Node> ();

			if (parent != null) parent.children.add(this);
		}

		private List<Node> findNodes(List<RuleNames> ruleNames) {

			List<Node> nodes = new ArrayList<Node> ();

			for (Node node : this.children) if (ruleNames.contains(node.ruleName)) nodes.add(node);

			return nodes;
		}

		private List<Node> findNodes(RuleNames ruleName) {

			return this.findNodes(Collections.singletonList(ruleName));
		}

		private Node findNode(List<RuleNames> ruleNames) {

			for (Node node : this.children) if (ruleNames.contains(node.ruleName)) return node;

			return null;
		}

		private Node findNode(RuleNames ruleName) {

			return this.findNode(Collections.singletonList(ruleName));
		}

		private void ast(PrintStream stream, int indent) {

			for (int i=0; i<indent; i++) stream.print(" ");
			stream.println(this.toString());

			for (Node node : this.children) node.ast(stream, indent + 1);
		}

		private String ast() {

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			PrintStream stream = new PrintStream(bytes);

			ast(stream, 0);

			return new String(bytes.toByteArray());
		}

		@Override
		public String toString() {

			return this.ruleName.name() + ": " + this.value;
		}
	}
}
