package xdi2.core.syntax;

public interface Parser {

	public XDIStatement parseXDIStatement(String string);
	public XDIAddress parseXDIAddress(String string);
	public XDIArc parseXDIArc(String string);
	public XDIXRef parseXDIXRef(String string);
}
