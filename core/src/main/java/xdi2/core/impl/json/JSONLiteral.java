package xdi2.core.impl.json;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteral;

import com.google.gson.JsonObject;

public class JSONLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 5656043671598618588L;

	public JSONLiteral(ContextNode contextNode) {

		super(contextNode);
	}

	@Override
	public Object getLiteralData() {

		JSONContextNode jsonContextNode = (JSONContextNode) this.getContextNode();

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(jsonContextNode.getXri().toString());

		return AbstractLiteral.jsonElementToLiteralData(jsonObject.get(XDIConstants.XRI_SS_LITERAL.toString()));
	}

	@Override
	public void setLiteralData(Object literalData) {

		JSONContextNode jsonContextNode = (JSONContextNode) this.getContextNode();

		((JSONGraph) this.getGraph()).jsonSaveToObject(jsonContextNode.getXri().toString(), XDIConstants.XRI_SS_LITERAL.toString(), AbstractLiteral.literalDataToJsonElement(literalData));
	}
}
