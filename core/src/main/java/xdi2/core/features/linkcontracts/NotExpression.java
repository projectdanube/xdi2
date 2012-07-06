package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;


public class NotExpression extends PolicyExpressionComponent {
	private static final long serialVersionUID = 5732150467865911411L;
	
	public NotExpression(ContextNode c){
		super(c);		
	}
	
	public static boolean isValid(ContextNode node){
		return XDILinkContractConstants.XRI_SS_NOT.equals(node
				.getArcXri());
	}
	public static NotExpression fromContextNode(ContextNode c) {
		if (!isValid(c)) {
			return null;
		}
		return new NotExpression(c);
	}
	public String getLogicExpression(){
		StringBuffer expr = new StringBuffer("(");
		expr.append(" not ");
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
		
			
		}
		
		if(contextNode.getLiteral() != null){
			expr.append(contextNode.getLiteral().getLiteralData());
		}
			
		expr.append(")");
		return expr.toString();
	}
	public void addLiteralExpression(String expr){
		contextNode.createLiteral(expr);
	}
	
}
