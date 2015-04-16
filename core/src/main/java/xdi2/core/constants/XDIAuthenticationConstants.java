package xdi2.core.constants;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;

/**
 * Constants for XDI authentication.
 * 
 * @author markus
 */
public final class XDIAuthenticationConstants {

	public static final XDIArc XDI_ARC_SIGNATURE = XDIArc.create("$sig");
	public static final XDIArc XDI_ARC_ENCRYPTION = XDIArc.create("$encrypt");

	public static final XDIAddress XDI_ADD_SECRET_TOKEN = XDIAddress.create("<$secret><$token>");
	public static final XDIAddress XDI_ADD_OAUTH_TOKEN = XDIAddress.create("<$oauth><$token>");

	public static final XDIAddress XDI_ADD_SECRET_TOKEN_VALID = XDIAddress.create("<$secret><$token><$valid>");
	public static final XDIAddress XDI_ADD_OAUTH_TOKEN_VALID = XDIAddress.create("<$oauth><$token><$valid>");
	public static final XDIAddress XDI_ADD_SIGNATURE_VALID = XDIAddress.create("<$sig><$valid>");

	public static final XDIAddress XDI_ADD_DIGEST_SECRET_TOKEN = XDIAddress.create("<$digest><$secret><$token>");

	public static final XDIAddress XDI_ADD_PUBLIC_KEY = XDIAddress.create("<$public><$key>");
	public static final XDIAddress XDI_ADD_PRIVATE_KEY = XDIAddress.create("<$private><$key>");
	public static final XDIAddress XDI_ADD_SECRET_KEY = XDIAddress.create("<$secret><$key>");

	public static final XDIAddress XDI_ADD_MSG_SIG_KEYPAIR = XDIAddress.create("$msg$sig$keypair");
	public static final XDIAddress XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY = XDIAddressUtil.concatXDIAddresses(XDI_ADD_MSG_SIG_KEYPAIR, XDI_ADD_PUBLIC_KEY);
	public static final XDIAddress XDI_ADD_MSG_SIG_KEYPAIR_PRIVATE_KEY = XDIAddressUtil.concatXDIAddresses(XDI_ADD_MSG_SIG_KEYPAIR, XDI_ADD_PRIVATE_KEY);
	public static final XDIAddress XDI_ADD_MSG_ENCRYPT_KEYPAIR = XDIAddress.create("$msg$encrypt$keypair");
	public static final XDIAddress XDI_ADD_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY = XDIAddressUtil.concatXDIAddresses(XDI_ADD_MSG_ENCRYPT_KEYPAIR, XDI_ADD_PUBLIC_KEY);
	public static final XDIAddress XDI_ADD_MSG_ENCRYPT_KEYPAIR_PRIVATE_KEY = XDIAddressUtil.concatXDIAddresses(XDI_ADD_MSG_ENCRYPT_KEYPAIR, XDI_ADD_PRIVATE_KEY);

	private XDIAuthenticationConstants() { }
}
