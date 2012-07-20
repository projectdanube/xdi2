package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.util.JSPolicyExpressionUtil;

public class NotExpression extends PolicyExpressionComponent {
	private static final long serialVersionUID = 5732150467865911411L;

	public NotExpression(ContextNode c) {
		super(c);
	}

	public static boolean isValid(ContextNode node) {
		return XDILinkContractConstants.XRI_SS_NOT.equals(node.getArcXri());
	}

	public static NotExpression fromContextNode(ContextNode c) {
		if (!isValid(c)) {
			return null;
		}
		return new NotExpression(c);
	}

	public boolean evaluate() {
		boolean evalResult = true;
		Iterator<ContextNode> allChildrenNodes = contextNode.getContextNodes();

		for (; allChildrenNodes.hasNext();) {

			boolean childExprEvalResult = true;

			ContextNode childNode = allChildrenNodes.next();

			if (AndExpression.isValid(childNode)) {
				AndExpression andChild = AndExpression
						.fromContextNode(childNode);

				childExprEvalResult = andChild.evaluate();

			} else if (OrExpression.isValid(childNode)) {
				OrExpression orChild = OrExpression.fromContextNode(childNode);

				childExprEvalResult = orChild.evaluate();
			} else if (NotExpression.isValid(childNode)) {
				NotExpression notChild = NotExpression
						.fromContextNode(childNode);

				childExprEvalResult = notChild.evaluate();
			} else if (childNode.getLiteral() != null) {

				String literalValue = childNode.getLiteral().getLiteralData();

				childExprEvalResult = JSPolicyExpressionUtil
						.evaluateJSExpression(literalValue);

			}
			evalResult = !childExprEvalResult;
		}
		return evalResult;

	}

//	public void addLiteralExpression(String expr) {
//		if(contextNode.getLiteral() != null){
//			contextNode.deleteLiteral();
//		}
//		contextNode.createLiteral(expr);
//	}
//
//	public void setLiteralExpression(String expr) {
//		Literal literal = null;
//		if ((literal = contextNode.getLiteral()) == null) {
//			literal = contextNode.createLiteral(expr);
//		} else {
//			literal.setLiteralData(expr);
//		}
//	}

}
