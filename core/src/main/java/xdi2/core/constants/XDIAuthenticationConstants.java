package xdi2.core.constants;

import xdi2.core.xri3.XDI3Segment;

/**
 * Constants for XDI authentication.
 * 
 * @author markus
 */
public final class XDIAuthenticationConstants {

	public static final XDI3Segment XRI_S_SECRET_TOKEN = XDI3Segment.create("$secret<$token>&");
	public static final XDI3Segment XRI_S_DIGEST_SECRET_TOKEN = XDI3Segment.create("$digest$secret<$token>&");
	public static final XDI3Segment XRI_S_OAUTH_TOKEN = XDI3Segment.create("$oauth<$token>&");

	public static final XDI3Segment XRI_S_SECRET_TOKEN_VALID = XDI3Segment.create("$secret<$token><$valid>&");

	private XDIAuthenticationConstants() { }
}
