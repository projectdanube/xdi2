package xdi2.transport.registry.impl.uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import xdi2.core.syntax.XDIArc;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.factory.impl.uri.UriMessagingContainerFactory;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.registry.MessagingContainerMount;
import xdi2.transport.registry.MessagingContainerRegistry;
import xdi2.transport.registry.impl.AbstractMessagingContainerRegistry;

/**
 * Registry to mount and unmount messaging targets.
 * 
 * @author markus
 */
public class UriMessagingContainerRegistry extends AbstractMessagingContainerRegistry implements MessagingContainerRegistry, ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(UriMessagingContainerRegistry.class);

	public static final boolean DEFAULT_CHECKDISABLED = true;
	public static final boolean DEFAULT_CHECKEXPIRED = true;

	private Map<String, UriMessagingContainerMount> messagingContainerMounts;
	private Map<String, UriMessagingContainerFactoryMount> messagingContainerFactoryMounts;

	private ApplicationContext applicationContext;

	private boolean checkDisabled;
	private boolean checkExpired;

	public UriMessagingContainerRegistry() {

		this.messagingContainerMounts = new HashMap<String, UriMessagingContainerMount> ();
		this.messagingContainerFactoryMounts = new HashMap<String, UriMessagingContainerFactoryMount> ();

		this.applicationContext = null;

		this.checkDisabled = DEFAULT_CHECKDISABLED;
		this.checkExpired = DEFAULT_CHECKEXPIRED;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (log.isDebugEnabled()) log.debug("Setting application context.");

		this.applicationContext = applicationContext;
	}

	public synchronized void init() throws Xdi2TransportException {

		// no application context?

		if (this.applicationContext == null) {

			log.info("No application context. No messaging targets loaded.");
			return;
		}

		// look up and mount all messaging targets

		log.info("Mounting messaging targets...");

		Map<String, MessagingContainer> messagingContainers = this.applicationContext.getBeansOfType(MessagingContainer.class);

		for (Map.Entry<String, MessagingContainer> entry : messagingContainers.entrySet()) {

			String path = entry.getKey();
			MessagingContainer messagingContainer = entry.getValue();

			if (! path.startsWith("/")) continue;

			this.mountMessagingContainer(path, messagingContainer);
		}

		// look up and mount all messaging target factories

		log.info("Mounting messaging target factories...");

		Map<String, UriMessagingContainerFactory> messagingContainerFactorys = this.applicationContext.getBeansOfType(UriMessagingContainerFactory.class);

		for (Map.Entry<String, UriMessagingContainerFactory> entry : messagingContainerFactorys.entrySet()) {

			String path = entry.getKey();
			UriMessagingContainerFactory messagingContainerFactory = entry.getValue();

			if (! path.startsWith("/")) continue;
			if (! path.endsWith("/*")) continue;

			this.mountMessagingContainerFactory(path, messagingContainerFactory);
		}

		// done

		log.info("Done. " + this.messagingContainerMounts.size() + " messaging targets and " + this.messagingContainerFactoryMounts.size() + " messaging target factories mounted.");
	}

	public synchronized void shutdown() {

		int size = this.messagingContainerMounts.size();

		// unmount all our messaging targets

		List<UriMessagingContainerMount> tempList = this.getMessagingContainerMounts();

		for (MessagingContainerMount messagingContainerMount : tempList) { 

			this.unmountMessagingContainer(messagingContainerMount.getMessagingContainer());
		}

		tempList.clear();

		// done

		log.info("Done. " + size + " messaging targets were shut down.");
	}

	/*
	 * Mounting and unmounting
	 */

	/**
	 * Mount a messaging target in the registry.
	 * @param messagingContainer The messaging target to mount.
	 */
	public synchronized MessagingContainerMount mountMessagingContainer(String messagingContainerPath, MessagingContainer messagingContainer) throws Xdi2TransportException {

		if (messagingContainerPath == null) throw new NullPointerException("Cannot mount a messaging target without path.");

		if (log.isDebugEnabled()) log.debug("Mounting messaging target " + messagingContainer.getClass().getSimpleName() + " at path " + messagingContainerPath);

		// already mounted?

		if (this.messagingContainerMounts.containsKey(messagingContainerPath)) {

			throw new Xdi2TransportException("Messaging target " + this.messagingContainerMounts.get(messagingContainerPath).getMessagingContainer().getClass().getCanonicalName() + " already mounted at path " + messagingContainerPath + ".");
		}

		// init messaging target

		try {

			messagingContainer.init();
		} catch (Exception ex) {

			log.warn("Exception while initializing messaging target " + messagingContainer.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			throw new Xdi2TransportException("Exception while initializing messaging target " + messagingContainer.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// mount messaging target

		while (messagingContainerPath.startsWith("/")) messagingContainerPath = messagingContainerPath.substring(1);
		messagingContainerPath = "/" + messagingContainerPath;

		UriMessagingContainerMount messagingContainerMount = new UriMessagingContainerMount(messagingContainerPath, messagingContainer);

		this.messagingContainerMounts.put(messagingContainerPath, messagingContainerMount);

		// done

		log.info("Messaging target " + messagingContainer.getClass().getCanonicalName() + " mounted at path " + messagingContainerPath + ".");

		return messagingContainerMount;
	}

	/**
	 * Mount a messaging target factory in the registry.
	 * @param messagingContainerFactory The messaging target factory to mount.
	 */
	public synchronized UriMessagingContainerFactoryMount mountMessagingContainerFactory(String messagingContainerFactoryPath, UriMessagingContainerFactory messagingContainerFactory) throws Xdi2TransportException {

		if (messagingContainerFactoryPath == null) throw new NullPointerException("Cannot mount a messaging target factory without path.");

		if (log.isDebugEnabled()) log.debug("Mounting messaging target factory " + messagingContainerFactory.getClass().getSimpleName() + " at path " + messagingContainerFactoryPath);

		// already mounted?

		if (this.messagingContainerFactoryMounts.containsKey(messagingContainerFactoryPath)) {

			throw new Xdi2TransportException("Messaging target factory " + this.messagingContainerFactoryMounts.get(messagingContainerFactoryPath).getClass().getCanonicalName() + " already mounted at path " + messagingContainerFactoryPath + ".");
		}

		// mount messaging target factory

		while (messagingContainerFactoryPath.startsWith("/")) messagingContainerFactoryPath = messagingContainerFactoryPath.substring(1);
		if (messagingContainerFactoryPath.endsWith("/*")) messagingContainerFactoryPath = messagingContainerFactoryPath.substring(0, messagingContainerFactoryPath.length() - 2);
		messagingContainerFactoryPath = "/" + messagingContainerFactoryPath;

		UriMessagingContainerFactoryMount messagingContainerFactoryMount = new UriMessagingContainerFactoryMount(messagingContainerFactoryPath, messagingContainerFactory);

		this.messagingContainerFactoryMounts.put(messagingContainerFactoryPath, messagingContainerFactoryMount);

		// init messaging target factory

		try {

			messagingContainerFactory.init();
		} catch (Exception ex) {

			log.warn("Exception while initializing messaging target factory " + messagingContainerFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			throw new Xdi2TransportException("Exception while initializing messaging target factory " + messagingContainerFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// done

		log.info("Messaging target factory " + messagingContainerFactory.getClass().getCanonicalName() + " mounted at path " + messagingContainerFactoryPath + ".");

		return messagingContainerFactoryMount;
	}

	/**
	 * Unmounts a messaging target from the registry.
	 * @param messagingContainer The messaging target to unmount.
	 */
	public synchronized void unmountMessagingContainer(MessagingContainer messagingContainer) {

		if (log.isDebugEnabled()) log.debug("Unmounting messaging target " + messagingContainer.getClass().getSimpleName());

		// shutdown messaging target

		try {

			messagingContainer.shutdown();
		} catch (Exception ex) {

			log.warn("Exception while shutting down messaging target " + messagingContainer.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// unmount messaging target

		for (Iterator<Entry<String, UriMessagingContainerMount>> messagingContainerMounts = this.messagingContainerMounts.entrySet().iterator(); messagingContainerMounts.hasNext(); ) {

			UriMessagingContainerMount messagingContainerMount = messagingContainerMounts.next().getValue();

			if (messagingContainerMount.getMessagingContainer() == messagingContainer) messagingContainerMounts.remove();
		}

		// done

		log.info("Messaging target " + messagingContainer.getClass().getCanonicalName() + " unmounted.");
	}

	/**
	 * Unmounts a messaging target factory from the registry.
	 * @param messagingContainerFactory The messaging target factory to unmount.
	 */
	public synchronized void unmountMessagingContainerFactory(UriMessagingContainerFactory messagingContainerFactory) {

		if (log.isDebugEnabled()) log.debug("Unmounting messaging target factory " + messagingContainerFactory.getClass().getSimpleName());

		// shutdown messaging target factory

		try {

			messagingContainerFactory.shutdown();
		} catch (Exception ex) {

			log.warn("Exception while shutting down messaging target factory " + messagingContainerFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// unmount messaging target factory

		for (Iterator<Entry<String, UriMessagingContainerFactoryMount>> messagingContainerFactoryMounts = this.messagingContainerFactoryMounts.entrySet().iterator(); messagingContainerFactoryMounts.hasNext(); ) {

			UriMessagingContainerFactoryMount messagingContainerFactoryMount = messagingContainerFactoryMounts.next().getValue();

			if (messagingContainerFactoryMount.getMessagingContainerFactory() == messagingContainerFactory) messagingContainerFactoryMounts.remove();
		}

		// done

		log.info("Messaging target factory " + messagingContainerFactory.getClass().getCanonicalName() + " unmounted.");
	}

	/*
	 * Lookup
	 */

	public synchronized UriMessagingContainerMount lookup(String requestPath) throws Xdi2TransportException, Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Looking up messaging target for request path " + requestPath);

		// look at messaging targets

		String messagingContainerPath = this.findMessagingContainerPath(requestPath);
		MessagingContainer messagingContainer = messagingContainerPath == null ? null : this.getMessagingContainer(messagingContainerPath);

		if (log.isDebugEnabled()) log.debug("messagingContainerPath=" + messagingContainerPath + ", messagingContainer=" + (messagingContainer == null ? null : messagingContainer.getClass().getSimpleName()));

		// look at messaging target factorys

		String messagingContainerFactoryPath = this.findMessagingContainerFactoryPath(requestPath);
		UriMessagingContainerFactory messagingContainerFactory = messagingContainerFactoryPath == null ? null : this.getMessagingContainerFactory(messagingContainerFactoryPath);

		if (log.isDebugEnabled()) log.debug("messagingContainerFactoryPath=" + messagingContainerFactoryPath + ", messagingContainerFactory=" + (messagingContainerFactory == null ? null : messagingContainerFactory.getClass().getSimpleName()));

		// what did we find?

		if (messagingContainerFactory != null) {

			if (messagingContainer == null) {

				// if we don't have a messaging target, see if the messaging target factory can create one

				messagingContainerFactory.mountMessagingContainer(this, messagingContainerFactoryPath, requestPath, this.isCheckDisabled(), this.isCheckExpired());
			} else {

				// if we do have a messaging target, see if the messaging target factory wants to modify or remove it

				messagingContainerFactory.updateMessagingContainer(this, messagingContainerFactoryPath, requestPath, this.isCheckDisabled(), this.isCheckExpired(), messagingContainer);
			}

			// after the messaging target factory did its work, look for the messaging target again

			messagingContainerPath = this.findMessagingContainerPath(requestPath);
			messagingContainer = messagingContainerPath == null ? null : this.getMessagingContainer(messagingContainerPath);

			if (log.isDebugEnabled()) log.debug("messagingContainerPath=" + messagingContainerPath + ", messagingContainer=" + (messagingContainer == null ? null : messagingContainer.getClass().getSimpleName()));
		}

		// done

		return new UriMessagingContainerMount(messagingContainerPath, messagingContainer);
	}

	@Override
	public synchronized UriMessagingContainerMount lookup(XDIArc ownerPeerRootXDIArc) throws Xdi2TransportException, Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Looking up messaging target for owner peer root " + ownerPeerRootXDIArc);

		// look at messaging targets

		for (UriMessagingContainerMount messagingContainerMount : this.getMessagingContainerMounts()) {

			String requestPath = messagingContainerMount.getMessagingContainerPath();
			if (! ownerPeerRootXDIArc.equals(messagingContainerMount.getMessagingContainer().getOwnerPeerRootXDIArc())) continue;

			return this.lookup(requestPath);
		}

		// look at messaging target factorys

		for (UriMessagingContainerFactoryMount messagingContainerFactoryMount : this.getMessagingContainerFactoryMounts()) {

			String requestPath = messagingContainerFactoryMount.getMessagingContainerFactory().getRequestPath(messagingContainerFactoryMount.getMessagingContainerFactoryPath(), ownerPeerRootXDIArc);
			if (requestPath == null) continue;

			return this.lookup(requestPath);
		}

		// done

		return null;
	}

	/*
	 * MessagingContainers
	 */

	@Override
	public synchronized List<UriMessagingContainerMount> getMessagingContainerMounts() {

		return new ArrayList<UriMessagingContainerMount> (this.messagingContainerMounts.values());
	}

	@Override
	public synchronized int getNumMessagingContainers() {

		return this.messagingContainerMounts.size();
	}

	public synchronized String findMessagingContainerPath(String requestPath) {

		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		if (log.isDebugEnabled()) log.debug("Finding messaging target for path: " + requestPath);

		String longestMessagingContainerPath = null;

		for (Map.Entry<String, UriMessagingContainerMount> messagingContainerMount : this.messagingContainerMounts.entrySet()) {

			if (requestPath.startsWith(messagingContainerMount.getKey()) && (longestMessagingContainerPath == null || messagingContainerMount.getKey().length() > longestMessagingContainerPath.length())) {

				longestMessagingContainerPath = messagingContainerMount.getKey();
			}
		}

		if (log.isDebugEnabled()) log.debug("Longest matching path of messaging target: " + longestMessagingContainerPath);

		return longestMessagingContainerPath;
	}

	public synchronized MessagingContainer getMessagingContainer(String messagingContainerPath) {

		if (! messagingContainerPath.startsWith("/")) messagingContainerPath = "/" + messagingContainerPath;

		MessagingContainerMount messagingContainerMount = this.messagingContainerMounts.get(messagingContainerPath);

		return messagingContainerMount == null ? null : messagingContainerMount.getMessagingContainer();
	}

	public synchronized String[] getMessagingContainerPaths() {

		return this.messagingContainerMounts.keySet().toArray(new String[this.messagingContainerMounts.size()]);
	}

	/*
	 * MessagingContainerFactorys
	 */

	@Override
	public synchronized List<UriMessagingContainerFactoryMount> getMessagingContainerFactoryMounts() {

		return new ArrayList<UriMessagingContainerFactoryMount> (this.messagingContainerFactoryMounts.values());
	}

	@Override
	public synchronized int getNumMessagingContainerFactorys() {

		return this.messagingContainerFactoryMounts.size();
	}

	public synchronized String findMessagingContainerFactoryPath(String requestPath) {

		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		if (log.isDebugEnabled()) log.debug("Finding messaging target factory for path: " + requestPath);

		String longestMessagingContainerFactoryPath = null;

		for (Map.Entry<String, UriMessagingContainerFactoryMount> messagingContainerFactoryMount : this.messagingContainerFactoryMounts.entrySet()) {

			if (requestPath.startsWith(messagingContainerFactoryMount.getKey()) && (longestMessagingContainerFactoryPath == null || messagingContainerFactoryMount.getKey().length() > longestMessagingContainerFactoryPath.length())) {

				longestMessagingContainerFactoryPath = messagingContainerFactoryMount.getKey();
			}
		}

		if (log.isDebugEnabled()) log.debug("Longest matching path of messaging target factory: " + longestMessagingContainerFactoryPath);

		return longestMessagingContainerFactoryPath;
	}

	public synchronized UriMessagingContainerFactory getMessagingContainerFactory(String messagingContainerFactoryPath) {

		if (! messagingContainerFactoryPath.startsWith("/")) messagingContainerFactoryPath = "/" + messagingContainerFactoryPath;

		UriMessagingContainerFactoryMount messagingContainerFactoryMount = this.messagingContainerFactoryMounts.get(messagingContainerFactoryPath);

		return messagingContainerFactoryMount == null ? null : messagingContainerFactoryMount.getMessagingContainerFactory();
	}

	public synchronized String[] getMessagingContainerFactoryPaths() {

		return this.messagingContainerFactoryMounts.keySet().toArray(new String[this.messagingContainerFactoryMounts.size()]);
	}

	/*
	 * Getters and setters
	 */

	public boolean isCheckDisabled() {

		return this.checkDisabled;
	}

	public void setCheckDisabled(boolean checkDisabled) {

		this.checkDisabled = checkDisabled;
	}

	public boolean isCheckExpired() {

		return this.checkExpired;
	}

	public void setCheckExpired(boolean checkExpired) {

		this.checkExpired = checkExpired;
	}
}

