package xdi2.core.features.nodetypes;

import xdi2.core.LiteralNode;

public interface XdiAttribute extends XdiSubGraph<XdiAttribute> {

	public LiteralNode setLiteralNode(Object literalData);
	public LiteralNode setLiteralString(String literalData);
	public LiteralNode setLiteralNumber(Double literalData);
	public LiteralNode setLiteralBoolean(Boolean literalData);

	public LiteralNode getLiteralNode();
	public LiteralNode getLiteralNode(Object literalData);
	public LiteralNode getLiteralString(String literalData);
	public LiteralNode getLiteralNumber(Double literalData);
	public LiteralNode getLiteralBoolean(Boolean literalData);
}
