package xdi2.core.xri3;

import java.util.List;

public interface XRIPath extends XRISyntaxComponent {

	public List getSegments();
	public int getNumSegments();
	public XRISegment getSegment(int i);
	public XRISegment getFirstSegment();
	public XRISegment getLastSegment();

	public boolean startsWith(XRISegment[] segments);
}
