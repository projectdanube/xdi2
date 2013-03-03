package xdi2.server;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.factory.MessagingTargetFactory;
import xdi2.server.registry.EndpointRegistry;

/**
 * This class encapsulates path information about a request to the server.
 * 
 * This is populated by the EndpointFilter and use by the EndpointServlet.
 * 
 * @author markus
 */
public class RequestInfo implements Serializable, Comparable<RequestInfo> {

	private static final long serialVersionUID = 5362137617270494532L;

	private String uri;
	private String requestPath;

	private String messagingTargetPath;
	private MessagingTarget messagingTarget;

	private static Logger log = LoggerFactory.getLogger(RequestInfo.class.getName());

	public RequestInfo(String uri, String requestPath) {

		this.uri = uri;
		this.requestPath = requestPath;
	}

	public void lookup(EndpointRegistry endpointRegistry) throws Xdi2ServerException, Xdi2MessagingException {

		// check which messaging target this request applies to

		String messagingTargetPath = endpointRegistry.findMessagingTargetPath(this.getRequestPath());
		MessagingTarget messagingTarget = messagingTargetPath == null ? null : endpointRegistry.getMessagingTarget(messagingTargetPath);

		log.debug("messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? null : messagingTarget.getClass().getSimpleName()));

		// check which messaging target factory this request applies to

		String messagingTargetFactoryPath = endpointRegistry.findMessagingTargetFactoryPath(this.getRequestPath());
		MessagingTargetFactory messagingTargetFactory = messagingTargetFactoryPath == null ? null : endpointRegistry.getMessagingTargetFactory(messagingTargetFactoryPath);

		log.debug("messagingTargetFactoryPath=" + messagingTargetFactoryPath + ", messagingTargetFactory=" + (messagingTargetFactory == null ? null : messagingTargetFactory.getClass().getSimpleName()));

		if (messagingTargetFactory != null) {

			if (messagingTarget == null) {

				// if we don't have a messaging target, see if the messaging target factory can create one

				messagingTargetFactory.mountMessagingTarget(endpointRegistry, messagingTargetFactoryPath, this.getRequestPath());
			} else {

				// if we do have a messaging target, see if the messaging target factory wants to modify or remove it

				messagingTargetFactory.updateMessagingTarget(endpointRegistry, messagingTargetFactoryPath, this.getRequestPath(), messagingTarget);
			}

			// after the messaging target factory did its work, look for the messaging target again

			messagingTargetPath = endpointRegistry.findMessagingTargetPath(this.getRequestPath());
			messagingTarget = messagingTargetPath == null ? null : endpointRegistry.getMessagingTarget(messagingTargetPath);

			log.debug("messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? null : messagingTarget.getClass().getSimpleName()));
		}

		// update request info

		this.setMessagingTargetPath(messagingTargetPath);
		this.setMessagingTarget(messagingTarget);
	}

	public String getUri() {

		return this.uri;
	}

	public void setUri(String uri) {

		this.uri = uri;
	}

	public String getRequestPath() {

		return this.requestPath;
	}

	public void setRequestPath(String requestPath) {

		this.requestPath = requestPath;
	}

	public String getMessagingTargetPath() {

		return this.messagingTargetPath;
	}

	public void setMessagingTargetPath(String messagingTargetPath) {

		this.messagingTargetPath = messagingTargetPath;
	}
	
	public MessagingTarget getMessagingTarget() {
	
		return this.messagingTarget;
	}

	public void setMessagingTarget(MessagingTarget messagingTarget) {
	
		this.messagingTarget = messagingTarget;
	}

	@Override
	public String toString() {

		return this.getUri();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof RequestInfo)) return false;
		if (object == this) return true;

		RequestInfo other = (RequestInfo) object;

		return this.getUri().equals(other.getUri());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getUri().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(RequestInfo other) {

		if (other == null || other == this) return 0;

		int compare;

		if ((compare = this.getUri().compareTo(other.getUri())) != 0) return compare;

		return 0;
	}
}
