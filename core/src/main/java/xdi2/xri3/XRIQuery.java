package xdi2.xri3;

public interface XRIQuery extends XRISyntaxComponent {

	/**
	 * Returns the query. In XRI 3.0, this corresponds to the iquery rule.
	 * @return The query excluding the ? character.
	 */
	public String getValue();
}
