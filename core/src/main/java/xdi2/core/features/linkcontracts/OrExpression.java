package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.util.JSPolicyExpressionUtil;

public class OrExpression extends PolicyExpressionComponent {
private static final long serialVersionUID = 5732150401265911411L;
	
	public OrExpression(ContextNode c){
		super(c);
	}
	
	public static boolean isValid(ContextNode node){
		return XDILinkContractConstants.XRI_SS_OR.equals(node
				.getArcXri());
	}		

	public static OrExpression fromContextNode(ContextNode c) {
		if (!isValid(c)) {
			return null;
		}
		return new OrExpression(c);
	}
	public boolean evaluate(){
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
			evalResult = evalResult || childExprEvalResult;
		}
		return evalResult;

	}
}
