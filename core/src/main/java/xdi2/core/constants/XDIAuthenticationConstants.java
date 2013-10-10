package xdi2.core.constants;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI authentication.
 * 
 * @author markus
 */
public final class XDIAuthenticationConstants {

	public static final XDI3Segment XRI_S_SECRET_TOKEN = XDI3Segment.create("$secret<$token>");
	public static final XDI3Segment XRI_S_OAUTH_TOKEN = XDI3Segment.create("$oauth<$token>");
	public static final XDI3Segment XRI_S_SIGNATURE = XDI3Segment.create("<$sig>");

	public static final XDI3SubSegment XRI_SS_SIGNATURE = XDI3SubSegment.create("<$sig>");

	public static final XDI3Segment XRI_S_SECRET_TOKEN_VALID = XDI3Segment.create("$secret<$token><$valid>");
	public static final XDI3Segment XRI_S_OAUTH_TOKEN_VALID = XDI3Segment.create("$oauth<$token><$valid>");
	public static final XDI3Segment XRI_S_SIGNATURE_VALID = XDI3Segment.create("<$sig><$valid>");

	public static final XDI3Segment XRI_S_DIGEST_SECRET_TOKEN = XDI3Segment.create("$digest$secret<$token>");

	public static final XDI3Segment XRI_S_MSG_SIG_KEYPAIR_PUBLIC_KEY = XDI3Segment.create("$msg$sig$keypair$public<$key>");
	public static final XDI3Segment XRI_S_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY = XDI3Segment.create("$msg$encrypt$keypair$public<$key>");

	private XDIAuthenticationConstants() { }
}
