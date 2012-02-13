package xdi2.core.xri3;

public interface XRISubSegment extends XRISyntaxComponent {

	public boolean hasGCS();
	public boolean hasLCS();
	public boolean hasLiteral();
	public boolean hasXRef();

	public Character getGCS();
	public Character getLCS();
	public XRILiteral getLiteral();
	public XRIXRef getXRef();

	public boolean isGlobal();
	public boolean isLocal();
	public boolean isPersistent();
	public boolean isReassignable();
}
