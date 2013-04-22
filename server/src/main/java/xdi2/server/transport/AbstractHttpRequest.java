package xdi2.server.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.factory.MessagingTargetFactory;
import xdi2.server.registry.HttpEndpointRegistry;

/**
 * This class encapsulates path information about a request to the server.
 * 
 * This is populated by the EndpointFilter and use by the EndpointServlet.
 * 
 * @author markus
 */
public abstract class AbstractHttpRequest implements HttpRequest {

	private String messagingTargetPath;
	private MessagingTarget messagingTarget;

	private static Logger log = LoggerFactory.getLogger(AbstractHttpRequest.class.getName());

	public AbstractHttpRequest() {

	}

	@Override
	public void lookup(HttpEndpointRegistry httpEndpointRegistry) throws Xdi2ServerException, Xdi2MessagingException {

		log.debug("Looking up messaging target for request path " + this.getRequestPath());

		// check which messaging target this request applies to

		String messagingTargetPath = httpEndpointRegistry.findMessagingTargetPath(this.getRequestPath());
		MessagingTarget messagingTarget = messagingTargetPath == null ? null : httpEndpointRegistry.getMessagingTarget(messagingTargetPath);

		log.debug("messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? null : messagingTarget.getClass().getSimpleName()));

		// check which messaging target factory this request applies to

		String messagingTargetFactoryPath = httpEndpointRegistry.findMessagingTargetFactoryPath(this.getRequestPath());
		MessagingTargetFactory messagingTargetFactory = messagingTargetFactoryPath == null ? null : httpEndpointRegistry.getMessagingTargetFactory(messagingTargetFactoryPath);

		log.debug("messagingTargetFactoryPath=" + messagingTargetFactoryPath + ", messagingTargetFactory=" + (messagingTargetFactory == null ? null : messagingTargetFactory.getClass().getSimpleName()));

		if (messagingTargetFactory != null) {

			if (messagingTarget == null) {

				// if we don't have a messaging target, see if the messaging target factory can create one

				messagingTargetFactory.mountMessagingTarget(httpEndpointRegistry, messagingTargetFactoryPath, this.getRequestPath());
			} else {

				// if we do have a messaging target, see if the messaging target factory wants to modify or remove it

				messagingTargetFactory.updateMessagingTarget(httpEndpointRegistry, messagingTargetFactoryPath, this.getRequestPath(), messagingTarget);
			}

			// after the messaging target factory did its work, look for the messaging target again

			messagingTargetPath = httpEndpointRegistry.findMessagingTargetPath(this.getRequestPath());
			messagingTarget = messagingTargetPath == null ? null : httpEndpointRegistry.getMessagingTarget(messagingTargetPath);

			log.debug("messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? null : messagingTarget.getClass().getSimpleName()));
		}

		// update request info

		this.setMessagingTargetPath(messagingTargetPath);
		this.setMessagingTarget(messagingTarget);
	}

	@Override
	public String getMessagingTargetPath() {

		return this.messagingTargetPath;
	}

	protected void setMessagingTargetPath(String messagingTargetPath) {

		this.messagingTargetPath = messagingTargetPath;
	}

	@Override
	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}

	protected void setMessagingTarget(MessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
	}
}
