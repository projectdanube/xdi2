package xdi2.messaging.request;

import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.messaging.MessageCollection;

public class RequestMessageCollection extends MessageCollection<RequestMessageEnvelope, RequestMessageCollection, RequestMessage> {

	private static final long serialVersionUID = 819400692158298069L;

	protected RequestMessageCollection(RequestMessageEnvelope messageEnvelope, XdiEntityCollection xdiEntityCollection) {

		super(messageEnvelope, xdiEntityCollection);
	}

	/*
	 * Static methods
	 */

	/**
	 * Factory method that creates an XDI message collection bound to a given XDI entity collection.
	 * @param messageEnvelope The XDI message envelope to which this XDI message collection belongs.
	 * @param xdiEntityCollection The XDI entity collection that is an XDI message collection.
	 * @return The XDI message collection.
	 */
	public static RequestMessageCollection fromMessageEnvelopeAndXdiEntityCollection(RequestMessageEnvelope messageEnvelope, XdiEntityCollection xdiEntityCollection) {

		if (! isValid(xdiEntityCollection)) return null;

		return new RequestMessageCollection(messageEnvelope, xdiEntityCollection);
	}
}
