package xdi2.core.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * Constants for XDI timestamps.
 * 
 * @author markus
 */
public final class XDITimestampsConstants {

	public static final XDI3Segment XRI_S_AS_CREATED = XDI3Segment.create("<#created>");
	public static final XDI3Segment XRI_S_AS_EXPIRES = XDI3Segment.create("<#expires>");
	public static final XDI3Segment XRI_S_AS_T = XDI3Segment.create("<$t>");

	public static final XDI3SubSegment XRI_SS_AS_CREATED = XDI3SubSegment.create("<#created>");
	public static final XDI3SubSegment XRI_SS_AS_EXPIRES = XDI3SubSegment.create("<#expires>");
	public static final XDI3SubSegment XRI_SS_AS_T = XDI3SubSegment.create("<$t>");

	public static final DateFormat[] FORMATS_TIMESTAMP = new SimpleDateFormat[] {

		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"),    // <-- default format
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	};

	private XDITimestampsConstants() { }
}
