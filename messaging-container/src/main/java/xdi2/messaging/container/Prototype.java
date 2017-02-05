package xdi2.messaging.container;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;

/**
 * This interface is used to mark messaging containers, interceptors and contributors that
 * can create an automatically configured copy of themselves, which is useful for
 * messaging container factories.
 */
public interface Prototype<T extends Prototype<T>> extends Cloneable {

	T instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException;

	public static class PrototypingContext extends HashMap<Prototype<?>, Prototype<?>> {

		private static final long serialVersionUID = -2272269309376726298L;

		private static final Logger log = LoggerFactory.getLogger(PrototypingContext.class);

		private XDIAddress ownerXDIAddress;
		private XdiPeerRoot ownerXdiPeerRoot;
		private ContextNode ownerContextNode;

		public PrototypingContext(XDIAddress ownerXDIAddress, XdiPeerRoot ownerXdiPeerRoot, ContextNode ownerContextNode) {

			this.ownerXDIAddress = ownerXDIAddress;
			this.ownerXdiPeerRoot = ownerXdiPeerRoot;
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

				if (log.isDebugEnabled()) log.debug("Instantiated " + prototyped + " from prototype " + prototype);
			}

			return prototyped;
		}

		public XDIAddress getOwnerXDIAddress() {

			return this.ownerXDIAddress;
		}

		public XdiPeerRoot getOwnerXdiPeerRoot() {

			return this.ownerXdiPeerRoot;
		}

		public ContextNode getOwnerContextNode() {

			return this.ownerContextNode;
		}
	}
}
