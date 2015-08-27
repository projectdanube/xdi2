package xdi2.messaging.constants;

import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;

/**
 * Constants for XDI messaging.
 * 
 * @author markus
 */
public final class XDIMessagingConstants {

	public static final XDIAddress XDI_ADD_ANONYMOUS = XDIAddress.create("$anon");

	public static final XDIArc XDI_ARC_MSG = XDIArc.create("$msg");
	public static final XDIArc XDI_ARC_GET = XDIArc.create("$get");
	public static final XDIArc XDI_ARC_SET = XDIArc.create("$set");
	public static final XDIArc XDI_ARC_DEL = XDIArc.create("$del");
	public static final XDIArc XDI_ARC_DO = XDIArc.create("$do");
	public static final XDIArc XDI_ARC_CONNECT = XDIArc.create("{$do}");
	public static final XDIArc XDI_ARC_SEND = XDIArc.create("$send");
	public static final XDIArc XDI_ARC_PUSH = XDIArc.create("$push");

	public static final XDIAddress XDI_ADD_MSG = XDIAddress.fromComponent(XDI_ARC_MSG);
	public static final XDIAddress XDI_ADD_GET = XDIAddress.fromComponent(XDI_ARC_GET);
	public static final XDIAddress XDI_ADD_GET_BOOLEAN = XDIAddressUtil.concatXDIAddresses(XDI_ADD_GET, XDIDictionaryConstants.XDI_ADD_BOOLEAN);
	public static final XDIAddress XDI_ADD_GET_NUMBER = XDIAddressUtil.concatXDIAddresses(XDI_ADD_GET, XDIDictionaryConstants.XDI_ADD_NUMBER);
	public static final XDIAddress XDI_ADD_SET = XDIAddress.fromComponent(XDI_ARC_SET);
	public static final XDIAddress XDI_ADD_DEL = XDIAddress.fromComponent(XDI_ARC_DEL);
	public static final XDIAddress XDI_ADD_DO = XDIAddress.fromComponent(XDI_ARC_DO);
	public static final XDIAddress XDI_ADD_CONNECT = XDIAddress.fromComponent(XDI_ARC_CONNECT);
	public static final XDIAddress XDI_ADD_SEND = XDIAddress.fromComponent(XDI_ARC_SEND);
	public static final XDIAddress XDI_ADD_PUSH = XDIAddress.fromComponent(XDI_ARC_PUSH);

	public static final XDIAddress XDI_ADD_FROM_PEER_ROOT_ARC = XDIAddress.create("$set");
	public static final XDIAddress XDI_ADD_TO_PEER_ROOT_ARC = XDIAddress.create("$is()");
	public static final XDIAddress XDI_ADD_CORRELATION = XDIAddress.create("$is$msg");

	public static final XDIAddress XDI_ADD_MESSAGE_PARAMETER_FULL = XDIAddress.create("<$full>");
	public static final XDIAddress XDI_ADD_OPERATION_PARAMETER_DEREF = XDIAddress.create("<$deref>");
	public static final XDIAddress XDI_ADD_OPERATION_PARAMETER_DEHAS = XDIAddress.create("<$dehas>");

	private XDIMessagingConstants() { }
}
