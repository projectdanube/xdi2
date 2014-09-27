package xdi2.core.features.nodetypes;

import xdi2.core.Literal;

public interface XdiAttribute extends XdiSubGraph<XdiAttribute> {

	public Literal setLiteral(Object literalData);
	public Literal setLiteralString(String literalData);
	public Literal setLiteralNumber(Double literalData);
	public Literal setLiteralBoolean(Boolean literalData);

	public Literal getLiteral();
	public Literal getLiteral(Object literalData);
	public Literal getLiteralString(String literalData);
	public Literal getLiteralNumber(Double literalData);
	public Literal getLiteralBoolean(Boolean literalData);
}
