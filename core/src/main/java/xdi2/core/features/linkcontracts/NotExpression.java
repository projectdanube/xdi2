package xdi2.core.features.linkcontracts;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.util.JSPolicyExpressionUtil;
import xdi2.core.features.multiplicity.AttributeSingleton;

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
		boolean evalResult = false;
		if (this.getContextNode().getLiteral() != null) {
			String literalValue = this.getContextNode().getLiteral()
					.getLiteralData();
			evalResult = !(JSPolicyExpressionUtil
					.evaluateJSExpression(literalValue));
		}
		{
			Iterator<ContextNode> allChildrenNodes = contextNode
					.getContextNodes();

			for (; allChildrenNodes.hasNext();) {
				ContextNode childNode = allChildrenNodes.next();
				boolean childExprEvalResult = evaluateChildBranch(childNode);
				evalResult = !childExprEvalResult;
			}

		}
		return evalResult;

	}

	@Override
	public void addLiteralExpression(String exprX) {
		if(null == exprX || exprX.isEmpty()){
			return;
		}
		String expr = "";
		try {
			expr = URLEncoder.encode(exprX, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ContextNode c = AttributeSingleton.fromContextNode(contextNode)
				.getContextNode();
		c.createLiteral(expr);
	}
}
