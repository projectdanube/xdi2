package xdi2.messaging;

import xdi2.xri3.impl.XRI3Segment;

/**
 * A class that provides various constants for XDI messaging.
 * 
 * @author markus
 */
public final class MessagingConstants {

	public static final XRI3Segment XRI_GET = new XRI3Segment("$get");
	public static final XRI3Segment XRI_ADD = new XRI3Segment("$add");
	public static final XRI3Segment XRI_MOD = new XRI3Segment("$mod");
	public static final XRI3Segment XRI_SET = new XRI3Segment("$set");
	public static final XRI3Segment XRI_DEL = new XRI3Segment("$del");
	public static final XRI3Segment XRI_DO = new XRI3Segment("$do");

	public static final XRI3Segment XRI_ANONYMOUS = new XRI3Segment("$");
	public static final XRI3Segment XRI_SELF = new XRI3Segment("$");

	private MessagingConstants() { }
}
