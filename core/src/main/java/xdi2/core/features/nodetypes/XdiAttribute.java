package xdi2.core.features.nodetypes;

import xdi2.core.LiteralNode;

public interface XdiAttribute extends XdiSubGraph<XdiAttribute> {

	public LiteralNode setLiteralData(Object literalData);
	public LiteralNode setLiteralDataString(String literalData);
	public LiteralNode setLiteralDataNumber(Double literalData);
	public LiteralNode setLiteralDataBoolean(Boolean literalData);

	public LiteralNode getLiteralNode();
	public LiteralNode getLiteralData(Object literalData);
	public LiteralNode getLiteralDataString(String literalData);
	public LiteralNode getLiteralDataNumber(Double literalData);
	public LiteralNode getLiteralDataBoolean(Boolean literalData);
}
