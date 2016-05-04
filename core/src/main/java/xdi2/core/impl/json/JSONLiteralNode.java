package xdi2.core.impl.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractLiteralNode;

public class JSONLiteralNode extends AbstractLiteralNode implements LiteralNode {

	private static final long serialVersionUID = 5656043671598618588L;

	private static final Logger log = LoggerFactory.getLogger(JSONLiteralNode.class);

	public JSONLiteralNode(ContextNode contextNode) {

		super(contextNode);
	}

	@Override
	public Object getLiteralData() {

		JSONContextNode jsonContextNode = (JSONContextNode) this.getContextNode();

		JsonObject jsonObject = ((JSONGraph) this.getGraph()).jsonLoad(jsonContextNode.getXDIAddress().toString());

		JsonElement jsonElement = jsonObject.get(XDIConstants.XDI_ARC_LITERAL.toString());

		if (jsonElement == null) {

			log.warn("In literal node " + this.getContextNode() + " found non-existent value.");
			return null;
		}

		return AbstractLiteralNode.jsonElementToLiteralData(jsonElement);
	}

	@Override
	public void setLiteralData(Object literalData) {

		JSONContextNode jsonContextNode = (JSONContextNode) this.getContextNode();

		((JSONGraph) this.getGraph()).jsonSaveToObject(jsonContextNode.getXDIAddress().toString(), XDIConstants.XDI_ARC_LITERAL.toString(), AbstractLiteralNode.literalDataToJsonElement(literalData));
	}
}
