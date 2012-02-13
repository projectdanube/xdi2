package xdi2.core.xri3;

public interface XRIReference extends XRISyntaxComponent {

	public boolean hasAuthority();
	public boolean hasPath();
	public boolean hasQuery();
	public boolean hasFragment();

	public XRIAuthority getAuthority();
	public XRIPath getPath();
	public XRIQuery getQuery();
	public XRIFragment getFragment();

	public boolean isValidXRI();
	public XRI toXRI();
}
