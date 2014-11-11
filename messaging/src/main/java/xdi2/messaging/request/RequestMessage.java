package xdi2.messaging.request;

import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.messaging.Message;

public class RequestMessage extends Message<RequestMessageEnvelope, RequestMessageCollection, RequestMessage> {

	private static final long serialVersionUID = 2958882899209008483L;

	protected RequestMessage(RequestMessageCollection messageCollection, XdiEntity xdiEntity) {

		super(messageCollection, xdiEntity);
	}

	/**
	 * Factory method that creates an XDI message bound to a given XDI entity.
	 * @param messageCollection The XDI message collection to which this XDI message belongs.
	 * @param xdiEntity The XDI entity that is an XDI message.
	 * @return The XDI message.
	 */
	public static RequestMessage fromMessageCollectionAndXdiEntity(RequestMessageCollection messageCollection, XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new RequestMessage(messageCollection, xdiEntity);
	}
}
