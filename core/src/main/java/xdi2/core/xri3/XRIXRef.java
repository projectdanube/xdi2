package xdi2.core.xri3;

public interface XRIXRef extends XRISyntaxComponent {

	public boolean hasXRIReference();
	public boolean hasIRI();

	public XRIReference getXRIReference();
	public String getIRI();
}
