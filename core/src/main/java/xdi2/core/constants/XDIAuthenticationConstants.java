package xdi2.core.constants;

import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI authentication.
 * 
 * @author markus
 */
public final class XDIAuthenticationConstants {

	public static final XDI3Segment XRI_S_ANONYMOUS = XDI3Segment.create("$anon");

	public static final XDI3SubSegment XRI_SS_SIGNATURE = XDI3SubSegment.create("$sig");

	public static final XDI3Segment XRI_S_SECRET_TOKEN = XDI3Segment.create("<$secret><$token>");
	public static final XDI3Segment XRI_S_OAUTH_TOKEN = XDI3Segment.create("<$oauth><$token>");

	public static final XDI3Segment XRI_S_SECRET_TOKEN_VALID = XDI3Segment.create("<$secret><$token><$valid>");
	public static final XDI3Segment XRI_S_OAUTH_TOKEN_VALID = XDI3Segment.create("<$oauth><$token><$valid>");
	public static final XDI3Segment XRI_S_SIGNATURE_VALID = XDI3Segment.create("<$sig><$valid>");

	public static final XDI3Segment XRI_S_DIGEST_SECRET_TOKEN = XDI3Segment.create("<$digest><$secret><$token>");

	public static final XDI3Segment XRI_S_PUBLIC_KEY = XDI3Segment.create("<$public><$key>");
	public static final XDI3Segment XRI_S_PRIVATE_KEY = XDI3Segment.create("<$private><$key>");
	public static final XDI3Segment XRI_S_SECRET_KEY = XDI3Segment.create("<$secret><$key>");

	public static final XDI3Segment XRI_S_MSG_SIG_KEYPAIR = XDI3Segment.create("$msg$sig$keypair");
	public static final XDI3Segment XRI_S_MSG_SIG_KEYPAIR_PUBLIC_KEY = XDI3Util.concatXris(XRI_S_MSG_SIG_KEYPAIR, XRI_S_PUBLIC_KEY);
	public static final XDI3Segment XRI_S_MSG_SIG_KEYPAIR_PRIVATE_KEY = XDI3Util.concatXris(XRI_S_MSG_SIG_KEYPAIR, XRI_S_PRIVATE_KEY);
	public static final XDI3Segment XRI_S_MSG_ENCRYPT_KEYPAIR = XDI3Segment.create("$msg$encrypt$keypair");
	public static final XDI3Segment XRI_S_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY = XDI3Util.concatXris(XRI_S_MSG_ENCRYPT_KEYPAIR, XRI_S_PUBLIC_KEY);
	public static final XDI3Segment XRI_S_MSG_ENCRYPT_KEYPAIR_PRIVATE_KEY = XDI3Util.concatXris(XRI_S_MSG_ENCRYPT_KEYPAIR, XRI_S_PRIVATE_KEY);

	private XDIAuthenticationConstants() { }
}
