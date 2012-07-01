package xdi2.core.features.linkcontracts;


import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.features.linkcontracts.util.XDILinkContractConstants;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class LiteralExpression extends PolicyExpressionComponent {
	private static final long serialVersionUID = 5732150498065922211L;
	
	

	public LiteralExpression(ContextNode c) {
		super(c);
	}

	public static boolean isValid(ContextNode node) {
		return XDILinkContractConstants.XRI_SS_LITERAL_EXP.equals(node.getArcXri());
	}

	public static LiteralExpression fromContextNode(ContextNode c) {
		if (!isValid(c)) {
			return null;
		}
		
		return new LiteralExpression(c);
	}
	
	
	public void addExpression(String str){
		int nodeCount = contextNode.getAllLiteralCount() + 1;
		XRI3SubSegment arcXri = new XRI3SubSegment("$!" + nodeCount);
		contextNode.createLiteralInContextNode(arcXri, str);
	}
	public void removeExpression(String str){
		Iterator<Literal> allLiterals = contextNode.getAllLiterals();
		Literal toDelete = null;
		for(;allLiterals.hasNext();){
			Literal l = allLiterals.next();
			if(l.getLiteralData().equalsIgnoreCase(str)){
				toDelete = l;
				break;
			}
		}
		if(toDelete != null){		
			toDelete.delete();
			toDelete.getContextNode().delete();
			
		}
		
	}
	public int getNumberOfLiterals(){
		return contextNode.getAllLiteralCount();
	}
	public Iterator<Literal> getAllLiterals(){
		
		return contextNode.getAllLiterals();
	}
	public String getLogicExpression(String parentClause){
		StringBuffer expr = new StringBuffer("(");
		Iterator<Literal> allLiterals = contextNode.getAllLiterals();
		for(;allLiterals.hasNext();){
			Literal l = allLiterals.next();
			if(allLiterals.hasNext()){
			expr.append(l.getLiteralData() + " " + parentClause + " ");
			}
			else{
				expr.append(l.getLiteralData());
			}
		}
		expr.append(")");
		return expr.toString();
	}
}
