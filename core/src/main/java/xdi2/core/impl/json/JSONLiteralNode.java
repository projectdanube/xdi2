package xdi2.core.impl.json;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteralNode;

import com.google.gson.JsonObject;

public class JSONLiteralNode extends AbstractLiteralNode implements LiteralNode {

	private static final long serialVersionUID = 5656043671598618588L;

	public JSONLiteralNode(ContextNode contextNode) {

		super(contextNode);
	}

	@Override
	public Object getLiteralData() {

		JSONContextNode jsonContextNode = (JSONContextNode) this.getContextNode();

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(jsonContextNode.getXDIAddress().toString());

		return AbstractLiteralNode.jsonElementToLiteralData(jsonObject.get(XDIConstants.XDI_ARC_LITERAL.toString()));
	}

	@Override
	public void setLiteralData(Object literalData) {

		JSONContextNode jsonContextNode = (JSONContextNode) this.getContextNode();

		((JSONGraph) this.getGraph()).jsonSaveToObject(jsonContextNode.getXDIAddress().toString(), XDIConstants.XDI_ARC_LITERAL.toString(), AbstractLiteralNode.literalDataToJsonElement(literalData));
	}
}
