package xdi2.messaging.response;

import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.messaging.Message;

public class ResponseMessage extends Message<ResponseMessageEnvelope, ResponseMessageCollection, ResponseMessage> {

	private static final long serialVersionUID = -4688032255316847537L;

	protected ResponseMessage(ResponseMessageCollection messageCollection, XdiEntity xdiEntity) {

		super(messageCollection, xdiEntity);
	}

	/**
	 * Factory method that creates an XDI message bound to a given XDI entity.
	 * @param messageCollection The XDI message collection to which this XDI message belongs.
	 * @param xdiEntity The XDI entity that is an XDI message.
	 * @return The XDI message.
	 */
	public static ResponseMessage fromMessageCollectionAndXdiEntity(ResponseMessageCollection messageCollection, XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new ResponseMessage(messageCollection, xdiEntity);
	}
}
