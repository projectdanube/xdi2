package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;

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
		boolean evalResult = false;
		Iterator<ContextNode> allChildrenNodes = contextNode.getContextNodes();

		for (; allChildrenNodes.hasNext();) {			
			ContextNode childNode = allChildrenNodes.next();
			boolean childExprEvalResult = evaluateChildBranch(childNode);
			evalResult = evalResult || childExprEvalResult;
		}
		return evalResult;

	}
}
