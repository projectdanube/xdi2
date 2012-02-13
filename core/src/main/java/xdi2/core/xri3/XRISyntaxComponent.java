package xdi2.core.xri3;

import java.io.Serializable;

public interface XRISyntaxComponent extends Serializable, Cloneable, Comparable {

	public String toURINormalForm();
	public String toIRINormalForm();
}
