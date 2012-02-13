package xdi2.core.xri3;

import java.util.List;

public interface XRISegment extends XRISyntaxComponent {

	public boolean hasLiteral();

	public XRILiteral getLiteral();
	public List getSubSegments();
	public int getNumSubSegments();
	public XRISubSegment getSubSegment(int i);
	public XRISubSegment getFirstSubSegment();
	public XRISubSegment getLastSubSegment();

	public boolean startsWith(XRISubSegment[] subSegments);
}
