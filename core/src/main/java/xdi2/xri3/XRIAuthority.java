package xdi2.xri3;

import java.util.List;

public interface XRIAuthority extends XRISyntaxComponent {

	public List getSubSegments();
	public int getNumSubSegments();
	public XRISubSegment getSubSegment(int i);
	public XRISubSegment getFirstSubSegment();
	public XRISubSegment getLastSubSegment();
	public boolean startsWith(XRISubSegment[] subSegments);
}
