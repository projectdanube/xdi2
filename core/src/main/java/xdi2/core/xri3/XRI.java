package xdi2.core.xri3;

public interface XRI extends XRISyntaxComponent {

	public boolean hasAuthority();
	public boolean hasPath();
	public boolean hasQuery();
	public boolean hasFragment();

	public XRIAuthority getAuthority();
	public XRIPath getPath();
	public XRIQuery getQuery();
	public XRIFragment getFragment();

	public boolean isIName();
	public boolean isINumber();
	public boolean isReserved();

	public boolean isValidXRIReference();
	public XRIReference toXRIReference();

	public boolean startsWith(XRI xri);
}
