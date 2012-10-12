package xdi2.messaging.target;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;

/**
 * This interface is used to mark messaging targets, interceptors and contributors that
 * can create an automatically configured copy of themselves, which is useful for
 * messaging target factories.
 */
public interface Prototype<T extends Prototype<T>> extends Cloneable {

	T instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException;

	public static class PrototypingContext extends HashMap<Prototype<?>, Prototype<?>> {

		private static final long serialVersionUID = -2272269309376726298L;

		private static final Logger log = LoggerFactory.getLogger(PrototypingContext.class);

		private MessagingTarget messagingTarget;
		private XRI3Segment owner;
		private ContextNode ownerRemoteRootContextNode;
		private ContextNode ownerContextNode;

		public PrototypingContext(XRI3Segment owner, ContextNode ownerRemoteRootContextNode, ContextNode ownerContextNode) {

			this.messagingTarget = null;
			this.owner = owner;
			this.ownerRemoteRootContextNode = ownerRemoteRootContextNode;
			this.ownerContextNode = ownerContextNode;
		}

		@SuppressWarnings("unchecked")
		public <T extends Prototype<T>> T instanceFor(Prototype<T> prototype) throws Xdi2MessagingException {

			T prototyped;

			if (this.containsKey(prototype)) {

				prototyped = (T) this.get(prototype); 

				if (log.isDebugEnabled()) log.debug("Already have instance " + prototyped + " from prototype " + prototype);
			} else {

				prototyped = prototype.instanceFor(this);
				this.put(prototype, prototyped);

				if (this.messagingTarget == null && (prototyped instanceof MessagingTarget)) this.messagingTarget = (MessagingTarget) prototyped;

				if (log.isDebugEnabled()) log.debug("Instantiated " + prototyped + " from prototype " + prototype);
			}

			return prototyped;
		}

		public MessagingTarget getMessagingTarget() {

			return this.messagingTarget;
		}

		public XRI3Segment getOwner() {

			return this.owner;
		}

		public ContextNode getOwnerRemoteRootContextNode() {

			return this.ownerRemoteRootContextNode;
		}

		public ContextNode getOwnerContextNode() {

			return this.ownerContextNode;
		}
	}
}
