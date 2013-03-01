package xdi2.core.xri3.parser.apg;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3SyntaxComponent;
import xdi2.core.xri3.XDI3XRef;
import xdi2.core.xri3.parser.apg.XDI3Grammar.RuleNames;

import com.coasttocoastresearch.apg.Ast;
import com.coasttocoastresearch.apg.Ast.AstCallback;
import com.coasttocoastresearch.apg.Grammar;
import com.coasttocoastresearch.apg.Parser;
import com.coasttocoastresearch.apg.Parser.Result;

/**
 * An XRI parser based on the aParse parser generator.
 * Parts of this parser have been automatically generated from an ABNF.  
 * @see http://www.parse2.com/
 */
public class XDI3ParserAPG extends XDI3Parser {

	private static final Logger log = LoggerFactory.getLogger(XDI3ParserAPG.class);

	private static Grammar grammar = XDI3Grammar.getInstance();

	private static Parser parser_xdi_statement = null;
	private static Parser parser_xdi_segment = null;
	private static Parser parser_subseg = null;
	private static Parser parser_xref = null;

	/*  TODO: why can't we create the parse once and re-use it? seems to cause random ParserExceptions and NullPointerExceptions

	private static Parser parser_xdi_statement = makeParser(XDI3Grammar.RuleNames.XDI_STATEMENT);
	private static Parser parser_xdi_segment = makeParser(XDI3Grammar.RuleNames.XDI_SEGMENT);
	private static Parser parser_subseg = makeParser(XDI3Grammar.RuleNames.SUBSEG);
	private static Parser parser_xref = makeParser(XDI3Grammar.RuleNames.XREF); */

	private static Parser makeParser(RuleNames ruleName) {

		Parser parser = new Parser(grammar);
		parser.setStartRule(ruleName.ruleID());

		return parser;
	}

	@Override
	public XDI3Statement parseXDI3Statement(String input) {

		return (XDI3Statement) parse(parser_xdi_statement, input, XDI3Grammar.RuleNames.XDI_STATEMENT);
	}

	@Override
	public XDI3Segment parseXDI3Segment(String input) {

		return (XDI3Segment) parse(parser_xdi_segment, input, XDI3Grammar.RuleNames.XDI_SEGMENT);
	}

	@Override
	public XDI3SubSegment parseXDI3SubSegment(String input) {

		return (XDI3SubSegment) parse(parser_subseg, input, XDI3Grammar.RuleNames.SUBSEG);
	}

	@Override
	public XDI3XRef parseXDI3XRef(String input) {

		return (XDI3XRef) parse(parser_xref, input, XDI3Grammar.RuleNames.XREF);
	}

	private XDI3SyntaxComponent parse(Parser parser, String input, RuleNames ruleName) {

		if (parser == null) parser = makeParser(ruleName);

		parser.setInputString(input);
		parser.enableAst(false);

		Ast ast = parser.enableAst(true);

		MyAstContext myAstContext = new MyAstContext(null, input);

		try {

			ast.enableRuleNode(XDI3Grammar.RuleNames.XDI_STATEMENT.ruleID(), true);
			ast.enableRuleNode(XDI3Grammar.RuleNames.XDI_SEGMENT.ruleID(), true);
			ast.enableRuleNode(XDI3Grammar.RuleNames.SUBSEG.ruleID(), true);
			ast.enableRuleNode(XDI3Grammar.RuleNames.XREF.ruleID(), true);
			ast.enableRuleNode(XDI3Grammar.RuleNames.GCS_CHAR.ruleID(), true);
			ast.enableRuleNode(XDI3Grammar.RuleNames.LCS_CHAR.ruleID(), true);
			ast.enableRuleNode(XDI3Grammar.RuleNames.LITERAL.ruleID(), true);
			ast.enableRuleNode(XDI3Grammar.RuleNames.IRI.ruleID(), true);
			ast.setRuleCallback(XDI3Grammar.RuleNames.XDI_STATEMENT.ruleID(), new MyAstCallback(ast, myAstContext, XDI3Grammar.RuleNames.XDI_STATEMENT));
			ast.setRuleCallback(XDI3Grammar.RuleNames.XDI_SEGMENT.ruleID(), new MyAstCallback(ast, myAstContext, XDI3Grammar.RuleNames.XDI_SEGMENT));
			ast.setRuleCallback(XDI3Grammar.RuleNames.SUBSEG.ruleID(), new MyAstCallback(ast, myAstContext, XDI3Grammar.RuleNames.SUBSEG));
			ast.setRuleCallback(XDI3Grammar.RuleNames.XREF.ruleID(), new MyAstCallback(ast, myAstContext, XDI3Grammar.RuleNames.XREF));
			ast.setRuleCallback(XDI3Grammar.RuleNames.GCS_CHAR.ruleID(), new MyAstCallback(ast, myAstContext, XDI3Grammar.RuleNames.GCS_CHAR));
			ast.setRuleCallback(XDI3Grammar.RuleNames.LCS_CHAR.ruleID(), new MyAstCallback(ast, myAstContext, XDI3Grammar.RuleNames.LCS_CHAR));
			ast.setRuleCallback(XDI3Grammar.RuleNames.LITERAL.ruleID(), new MyAstCallback(ast, myAstContext, XDI3Grammar.RuleNames.LITERAL));
			ast.setRuleCallback(XDI3Grammar.RuleNames.IRI.ruleID(), new MyAstCallback(ast, myAstContext, XDI3Grammar.RuleNames.IRI));
		} catch (Exception ex) {

			throw new ParserException(ex.getMessage(), ex);
		}

		Result result;

		try {

			result = parser.parse();
			if (log.isTraceEnabled()) log.trace("Result: " + result);
		} catch (Exception ex) {

			throw new ParserException(ex.getMessage(), ex);
		}

		if (! result.success()) {

			throw new ParserException("Parser error for 'rule' " + ruleName.ruleName() + " (match: " + result.getMatchedPhraseLength() + ", max: " + result.getMaxMatchedPhraseLength() + ")");
		}

		ast.translateAst();

		if (log.isTraceEnabled()) log.trace("AST: " + myAstContext.currentNode.ast());

		return myAstContext.currentNode.xri;
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

			if (log.isTraceEnabled()) log.trace("POST: " + this.ruleName.name() + " " + offset + " " + length);

			this.myAstContext.currentNode.value = this.myAstContext.input.substring(offset, offset + length);

			if (this.ruleName.equals(RuleNames.XDI_STATEMENT)) {

				List<Node> nodesSegment = this.myAstContext.currentNode.findNodes(RuleNames.XDI_SEGMENT);

				XDI3Segment subject = (XDI3Segment) nodesSegment.get(0).xri;
				XDI3Segment predicate = (XDI3Segment) nodesSegment.get(1).xri;
				XDI3Segment object = (XDI3Segment) nodesSegment.get(2).xri;

				this.myAstContext.currentNode.xri = XDI3ParserAPG.this.makeXDI3Statement(this.myAstContext.currentNode.value, subject, predicate, object);
			}

			if (this.ruleName.equals(RuleNames.XDI_SEGMENT)) {

				List<Node> nodesSubseg = this.myAstContext.currentNode.findNodes(RuleNames.SUBSEG);

				List<XDI3SubSegment> subSegments = new ArrayList<XDI3SubSegment> ();
				for (Node nodeSubseg : nodesSubseg) subSegments.add((XDI3SubSegment) nodeSubseg.xri);

				this.myAstContext.currentNode.xri = XDI3ParserAPG.this.makeXDI3Segment(this.myAstContext.currentNode.value, subSegments);
			}

			if (this.ruleName.equals(RuleNames.SUBSEG)) {

				Node nodeGcs = this.myAstContext.currentNode.findNode(RuleNames.GCS_CHAR);
				Node nodeLcs = this.myAstContext.currentNode.findNode(RuleNames.LCS_CHAR);
				Node nodeLiteral = this.myAstContext.currentNode.findNode(RuleNames.LITERAL);
				Node nodeXref = this.myAstContext.currentNode.findNode(RuleNames.XREF);

				Character gcs = nodeGcs == null ? null : Character.valueOf(nodeGcs.value.charAt(0));
				Character lcs = nodeLcs == null ? null : Character.valueOf(nodeLcs.value.charAt(0));
				String literal = nodeLiteral == null ? null : nodeLiteral.value;
				XDI3XRef xref = nodeXref == null ? null : (XDI3XRef) nodeXref.xri;

				this.myAstContext.currentNode.xri = XDI3ParserAPG.this.makeXDI3SubSegment(this.myAstContext.currentNode.value, gcs, lcs, literal, xref);
			}

			if (this.ruleName.equals(RuleNames.XREF)) {

				List<Node> nodesSegment = this.myAstContext.currentNode.findNodes(RuleNames.XDI_SEGMENT);
				Node nodeStatement = this.myAstContext.currentNode.findNode(RuleNames.XDI_STATEMENT);
				Node nodeIRI = this.myAstContext.currentNode.findNode(RuleNames.IRI);
				Node nodeLiteral = this.myAstContext.currentNode.findNode(RuleNames.LITERAL);

				XDI3Segment segment = nodesSegment.size() != 1 ? null : (XDI3Segment) nodesSegment.get(0).xri;
				XDI3Statement statement = nodeStatement == null ? null : (XDI3Statement) nodeStatement.xri;
				XDI3Segment partialSubject = nodesSegment.size() != 2 ? null : (XDI3Segment) nodesSegment.get(0).xri;
				XDI3Segment partialPredicate = nodesSegment.size() != 2 ? null : (XDI3Segment) nodesSegment.get(1).xri;
				String IRI = nodeIRI == null ? null : nodeIRI.value;
				String literal = nodeLiteral == null ? null : nodeLiteral.value;

				this.myAstContext.currentNode.xri = XDI3ParserAPG.this.makeXDI3XRef(this.myAstContext.currentNode.value, segment, statement, partialSubject, partialPredicate, IRI, literal);
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
		private XDI3SyntaxComponent xri;
		private List<Node> children;

		private Node(RuleNames ruleName) {

			this.ruleName = ruleName;
			this.parent = null;
			this.value = null;
			this.xri = null;
			this.children = new ArrayList<Node> ();
		}

		private Node(RuleNames ruleName, Node parent) {

			this.ruleName = ruleName;
			this.parent = parent;
			this.value = null;
			this.xri = null;
			this.children = new ArrayList<Node> ();

			if (parent != null) parent.children.add(this);
		}

		private List<Node> findNodes(RuleNames ruleName) {

			List<Node> nodes = new ArrayList<Node> ();

			for (Node node : this.children) if (node.ruleName.equals(ruleName)) nodes.add(node);

			return nodes;
		}

		private Node findNode(RuleNames ruleName) {

			for (Node node : this.children) if (node.ruleName.equals(ruleName)) return node;

			return null;
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
