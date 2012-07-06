package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;

public class AndExpression extends PolicyExpressionComponent {
	private static final long serialVersionUID = 5732150498065911411L;

	public AndExpression(ContextNode c) {
		super(c);
	}

	public static boolean isValid(ContextNode node) {
		return XDILinkContractConstants.XRI_SS_AND.equals(node.getArcXri());
	}

	public static AndExpression fromContextNode(ContextNode c) {
		if (!isValid(c)) {
			return null;
		}
		return new AndExpression(c);
	}
	
	public String getLogicExpression(){
		StringBuffer expr = new StringBuffer("(");
		Iterator<ContextNode> allChildrenNodes = contextNode.getContextNodes();
		
		for(;allChildrenNodes.hasNext();){
			
			ContextNode childNode = allChildrenNodes.next();
			
			if(AndExpression.isValid(childNode)){
				AndExpression andChild = AndExpression.fromContextNode(childNode);
				expr.append(andChild.getLogicExpression());		
				
			}
			else if(OrExpression.isValid(childNode)){
				OrExpression orChild = OrExpression.fromContextNode(childNode);
				expr.append(orChild.getLogicExpression());
			}
			else if(NotExpression.isValid(childNode)){
				NotExpression notChild = NotExpression.fromContextNode(childNode);
				expr.append(notChild.getLogicExpression());
			}
			else if(childNode.getLiteral() != null){
				expr.append("(");
				String literalValue = childNode.getLiteral().getLiteralData();
				expr.append(literalValue);
				expr.append(")");
			}
			if(allChildrenNodes.hasNext()){
			expr.append(" and ");
			}
			
		}
		expr.append(")");
		return expr.toString();
	}
}
