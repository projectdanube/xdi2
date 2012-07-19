package xdi2.messaging.target.interceptor.impl;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import xdi2.core.Literal;
import xdi2.core.features.linkcontracts.LinkContract;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;

public class JSPolicyExpressionHelper extends ScriptableObject {
	private static final long serialVersionUID = 123470592527335642L;
	private LinkContract linkContract = null;
	private Message message = null;

	// The zero-argument constructor used by Rhino runtime to create instances
	@JSConstructor
	public JSPolicyExpressionHelper() {
		
	}
	@JSConstructor
	public JSPolicyExpressionHelper(LinkContract lc , Message m){
	
	linkContract = lc;
	message = m;
	}
	// The class name is defined by the getClassName method
	@Override
	public String getClassName() {
		return "JSPolicyExpressionHelper";
	}

	@JSGetter
	public Message message() {
		return message;
	}

	@JSGetter
	public LinkContract linkContract() {
		return linkContract;
	}
	@JSFunction
	public String getGraphValue(String address) {
		Scriptable scope = this.getParentScope();
		linkContract = (LinkContract)scope.get("linkContract", scope);
		if(linkContract == null){
			return "";
		}
		Literal literal = linkContract.getContextNode().getGraph().findLiteral(
				new XRI3Segment(address));
		return literal == null ? null : literal.getLiteralData();

	}
	@JSFunction
	public String getMessageProperty(String property) {
		Scriptable scope = this.getParentScope();
		message = (Message)scope.get("message", scope);
		if(message == null){
			return "";
		}
		Literal literal = message.getContextNode().findLiteral(
				new XRI3Segment(property));
		return literal == null ? null : literal.getLiteralData();
	}

}
