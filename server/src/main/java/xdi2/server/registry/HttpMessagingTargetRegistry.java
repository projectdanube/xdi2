package xdi2.server.registry;

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

import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.factory.MessagingTargetFactory;

/**
 * Registry to mount and unmount messaging targets.
 * 
 * @author markus
 */
public class HttpMessagingTargetRegistry implements MessagingTargetRegistry, MessagingTargetFactoryRegistry, ApplicationContextAware {

	private static final Logger log = LoggerFactory.getLogger(HttpMessagingTargetRegistry.class);

	private Map<String, MessagingTargetMount> messagingTargetMounts;
	private Map<String, MessagingTargetFactoryMount> messagingTargetFactoryMounts;

	private ApplicationContext applicationContext;

	public HttpMessagingTargetRegistry() {

		this.messagingTargetMounts = new HashMap<String, MessagingTargetMount> ();
		this.messagingTargetFactoryMounts = new HashMap<String, MessagingTargetFactoryMount> ();

		this.applicationContext = null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (log.isDebugEnabled()) log.debug("Setting application context.");

		this.applicationContext = applicationContext;
	}

	public synchronized void init() throws Xdi2ServerException {

		// no application context?

		if (this.applicationContext == null) {

			log.info("No application context. No messaging targets loaded.");
			return;
		}

		// look up and mount all messaging targets

		log.info("Mounting messaging targets...");

		Map<String, MessagingTarget> messagingTargets = this.applicationContext.getBeansOfType(MessagingTarget.class);

		for (Map.Entry<String, MessagingTarget> entry : messagingTargets.entrySet()) {

			String path = entry.getKey();
			MessagingTarget messagingTarget = entry.getValue();

			if (! path.startsWith("/")) continue;

			this.mountMessagingTarget(path, messagingTarget);
		}

		// look up and mount all messaging target factories

		log.info("Mounting messaging target factories...");

		Map<String, MessagingTargetFactory> messagingTargetFactorys = this.applicationContext.getBeansOfType(MessagingTargetFactory.class);

		for (Map.Entry<String, MessagingTargetFactory> entry : messagingTargetFactorys.entrySet()) {

			String path = entry.getKey();
			MessagingTargetFactory messagingTargetFactory = entry.getValue();

			if (! path.startsWith("/")) continue;
			if (! path.endsWith("/*")) continue;

			this.mountMessagingTargetFactory(path, messagingTargetFactory);
		}

		// done

		log.info("Done. " + this.messagingTargetMounts.size() + " messaging targets and " + this.messagingTargetFactoryMounts.size() + " messaging target factories mounted.");
	}

	public synchronized void shutdown() {

		int size = this.messagingTargetMounts.size();

		// unmount all our messaging targets

		List<MessagingTargetMount> tempList = this.getMessagingTargetMounts();

		for (MessagingTargetMount messagingTargetMount : tempList) { 

			this.unmountMessagingTarget(messagingTargetMount.getMessagingTarget());
		}

		tempList.clear();

		// done

		log.info(size + " messaging targets were shut down.");
	}

	public synchronized void reload() throws Xdi2ServerException {

		this.shutdown();
		this.init();
	}

	/*
	 * Mounting and unmounting
	 */

	/**
	 * Mount a messaging target in the registry.
	 * @param messagingTarget The messaging target to mount.
	 */
	public synchronized MessagingTargetMount mountMessagingTarget(String messagingTargetPath, MessagingTarget messagingTarget) throws Xdi2ServerException {

		if (messagingTargetPath == null) throw new NullPointerException("Cannot mount a messaging target without path.");

		if (log.isDebugEnabled()) log.debug("Mounting messaging target " + messagingTarget.getClass().getSimpleName() + " at path " + messagingTargetPath);

		// already mounted?

		if (this.messagingTargetMounts.containsKey(messagingTargetPath)) {

			throw new Xdi2ServerException("Messaging target " + this.messagingTargetMounts.get(messagingTargetPath).getMessagingTarget().getClass().getCanonicalName() + " already mounted at path " + messagingTargetPath + ".");
		}

		// init messaging target

		try {

			messagingTarget.init();
		} catch (Exception ex) {

			log.warn("Exception while initializing messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			throw new Xdi2ServerException("Exception while initializing messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// mount messaging target

		while (messagingTargetPath.startsWith("/")) messagingTargetPath = messagingTargetPath.substring(1);
		messagingTargetPath = "/" + messagingTargetPath;

		MessagingTargetMount messagingTargetMount = new MessagingTargetMount(messagingTargetPath, messagingTarget);

		this.messagingTargetMounts.put(messagingTargetPath, messagingTargetMount);

		// done

		log.info("Messaging target " + messagingTarget.getClass().getCanonicalName() + " mounted at path " + messagingTargetPath + ".");

		return messagingTargetMount;
	}

	/**
	 * Mount a messaging target factory in the registry.
	 * @param messagingTargetFactory The messaging target factory to mount.
	 */
	public synchronized MessagingTargetFactoryMount mountMessagingTargetFactory(String messagingTargetFactoryPath, MessagingTargetFactory messagingTargetFactory) throws Xdi2ServerException {

		if (messagingTargetFactoryPath == null) throw new NullPointerException("Cannot mount a messaging target factory without path.");

		if (log.isDebugEnabled()) log.debug("Mounting messaging target factory " + messagingTargetFactory.getClass().getSimpleName() + " at path " + messagingTargetFactoryPath);

		// already mounted?

		if (this.messagingTargetFactoryMounts.containsKey(messagingTargetFactoryPath)) {

			throw new Xdi2ServerException("Messaging target factory " + this.messagingTargetFactoryMounts.get(messagingTargetFactoryPath).getClass().getCanonicalName() + " already mounted at path " + messagingTargetFactoryPath + ".");
		}

		// mount messaging target factory

		while (messagingTargetFactoryPath.startsWith("/")) messagingTargetFactoryPath = messagingTargetFactoryPath.substring(1);
		if (messagingTargetFactoryPath.endsWith("/*")) messagingTargetFactoryPath = messagingTargetFactoryPath.substring(0, messagingTargetFactoryPath.length() - 2);
		messagingTargetFactoryPath = "/" + messagingTargetFactoryPath;

		MessagingTargetFactoryMount messagingTargetFactoryMount = new MessagingTargetFactoryMount(messagingTargetFactoryPath, messagingTargetFactory);

		this.messagingTargetFactoryMounts.put(messagingTargetFactoryPath, messagingTargetFactoryMount);

		// init messaging target factory

		try {

			messagingTargetFactory.init();
		} catch (Exception ex) {

			log.warn("Exception while initializing messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			throw new Xdi2ServerException("Exception while initializing messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// done

		log.info("Messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + " mounted at path " + messagingTargetFactoryPath + ".");

		return messagingTargetFactoryMount;
	}

	/**
	 * Unmounts a messaging target from the registry.
	 * @param messagingTarget The messaging target to unmount.
	 */
	public synchronized void unmountMessagingTarget(MessagingTarget messagingTarget) {

		if (log.isDebugEnabled()) log.debug("Unmounting messaging target " + messagingTarget.getClass().getSimpleName());

		// shutdown messaging target

		try {

			messagingTarget.shutdown();
		} catch (Exception ex) {

			log.warn("Exception while shutting down messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// unmount messaging target

		for (Iterator<Entry<String, MessagingTargetMount>> messagingTargetMounts = this.messagingTargetMounts.entrySet().iterator(); messagingTargetMounts.hasNext(); ) {

			MessagingTargetMount messagingTargetMount = messagingTargetMounts.next().getValue();

			if (messagingTargetMount.getMessagingTarget() == messagingTarget) messagingTargetMounts.remove();
		}

		// done

		log.info("Messaging target " + messagingTarget.getClass().getCanonicalName() + " unmounted.");
	}

	/**
	 * Unmounts a messaging target factory from the registry.
	 * @param messagingTargetFactory The messaging target factory to unmount.
	 */
	public synchronized void unmountMessagingTargetFactory(MessagingTargetFactory messagingTargetFactory) {

		if (log.isDebugEnabled()) log.debug("Unmounting messaging target factory " + messagingTargetFactory.getClass().getSimpleName());

		// shutdown messaging target factory

		try {

			messagingTargetFactory.shutdown();
		} catch (Exception ex) {

			log.warn("Exception while shutting down messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// unmount messaging target factory

		for (Iterator<Entry<String, MessagingTargetFactoryMount>> messagingTargetFactoryMounts = this.messagingTargetFactoryMounts.entrySet().iterator(); messagingTargetFactoryMounts.hasNext(); ) {

			MessagingTargetFactoryMount messagingTargetFactoryMount = messagingTargetFactoryMounts.next().getValue();

			if (messagingTargetFactoryMount.getMessagingTargetFactory() == messagingTargetFactory) messagingTargetFactoryMounts.remove();
		}

		// done

		log.info("Messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + " unmounted.");
	}

	/*
	 * Lookup
	 */

	public synchronized MessagingTargetMount lookup(String requestPath) throws Xdi2ServerException, Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Looking up messaging target for request path " + requestPath);

		// look at messaging targets

		String messagingTargetPath = this.findMessagingTargetPath(requestPath);
		MessagingTarget messagingTarget = messagingTargetPath == null ? null : this.getMessagingTarget(messagingTargetPath);

		if (log.isDebugEnabled()) log.debug("messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? null : messagingTarget.getClass().getSimpleName()));

		// look at messaging target factorys

		String messagingTargetFactoryPath = this.findMessagingTargetFactoryPath(requestPath);
		MessagingTargetFactory messagingTargetFactory = messagingTargetFactoryPath == null ? null : this.getMessagingTargetFactory(messagingTargetFactoryPath);

		if (log.isDebugEnabled()) log.debug("messagingTargetFactoryPath=" + messagingTargetFactoryPath + ", messagingTargetFactory=" + (messagingTargetFactory == null ? null : messagingTargetFactory.getClass().getSimpleName()));

		// what did we find?

		if (messagingTargetFactory != null) {

			if (messagingTarget == null) {

				// if we don't have a messaging target, see if the messaging target factory can create one

				messagingTargetFactory.mountMessagingTarget(this, messagingTargetFactoryPath, requestPath);
			} else {

				// if we do have a messaging target, see if the messaging target factory wants to modify or remove it

				messagingTargetFactory.updateMessagingTarget(this, messagingTargetFactoryPath, requestPath, messagingTarget);
			}

			// after the messaging target factory did its work, look for the messaging target again

			messagingTargetPath = this.findMessagingTargetPath(requestPath);
			messagingTarget = messagingTargetPath == null ? null : this.getMessagingTarget(messagingTargetPath);

			if (log.isDebugEnabled()) log.debug("messagingTargetPath=" + messagingTargetPath + ", messagingTarget=" + (messagingTarget == null ? null : messagingTarget.getClass().getSimpleName()));
		}

		// done

		return new MessagingTargetMount(messagingTargetPath, messagingTarget);
	}

	public synchronized MessagingTargetMount lookup(XDI3SubSegment ownerPeerRootXri) throws Xdi2ServerException, Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Looking up messaging target for owner peer root XRI " + ownerPeerRootXri);

		// look at messaging targets

		for (MessagingTargetMount messagingTargetMount : this.getMessagingTargetMounts()) {

			String requestPath = messagingTargetMount.getMessagingTargetPath();
			if (! ownerPeerRootXri.equals(messagingTargetMount.getMessagingTarget().getOwnerPeerRootXri())) continue;

			return this.lookup(requestPath);
		}

		// look at messaging target factorys

		for (MessagingTargetFactoryMount messagingTargetFactoryMount : this.getMessagingTargetFactoryMounts()) {

			String requestPath = messagingTargetFactoryMount.getMessagingTargetFactory().getRequestPath(messagingTargetFactoryMount.getMessagingTargetFactoryPath(), ownerPeerRootXri);
			if (requestPath == null) continue;

			return this.lookup(requestPath);
		}

		// done

		return null;
	}

	/*
	 * MessagingTargets
	 */

	@Override
	public synchronized List<MessagingTargetMount> getMessagingTargetMounts() {

		return new ArrayList<MessagingTargetMount> (this.messagingTargetMounts.values());
	}

	@Override
	public synchronized int getNumMessagingTargets() {

		return this.messagingTargetMounts.size();
	}

	public synchronized String findMessagingTargetPath(String requestPath) {

		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		log.info("Finding messaging target for path: " + requestPath);

		String longestMessagingTargetPath = null;

		for (Map.Entry<String, MessagingTargetMount> messagingTargetMount : this.messagingTargetMounts.entrySet()) {

			if (requestPath.startsWith(messagingTargetMount.getKey()) && (longestMessagingTargetPath == null || messagingTargetMount.getKey().length() > longestMessagingTargetPath.length())) {

				longestMessagingTargetPath = messagingTargetMount.getKey();
			}
		}

		log.info("Longest matching path of messaging target: " + longestMessagingTargetPath);

		return longestMessagingTargetPath;
	}

	public synchronized MessagingTarget getMessagingTarget(String messagingTargetPath) {

		if (! messagingTargetPath.startsWith("/")) messagingTargetPath = "/" + messagingTargetPath;

		MessagingTargetMount messagingTargetMount = this.messagingTargetMounts.get(messagingTargetPath);

		return messagingTargetMount == null ? null : messagingTargetMount.getMessagingTarget();
	}

	public synchronized String[] getMessagingTargetPaths() {

		return this.messagingTargetMounts.keySet().toArray(new String[this.messagingTargetMounts.size()]);
	}

	/*
	 * MessagingTargetFactorys
	 */

	@Override
	public synchronized List<MessagingTargetFactoryMount> getMessagingTargetFactoryMounts() {

		return new ArrayList<MessagingTargetFactoryMount> (this.messagingTargetFactoryMounts.values());
	}

	@Override
	public synchronized int getNumMessagingTargetFactorys() {

		return this.messagingTargetFactoryMounts.size();
	}

	public synchronized String findMessagingTargetFactoryPath(String requestPath) {

		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		log.info("Finding messaging target factory for path: " + requestPath);

		String longestMessagingTargetFactoryPath = null;

		for (Map.Entry<String, MessagingTargetFactoryMount> messagingTargetFactoryMount : this.messagingTargetFactoryMounts.entrySet()) {

			if (requestPath.startsWith(messagingTargetFactoryMount.getKey()) && (longestMessagingTargetFactoryPath == null || messagingTargetFactoryMount.getKey().length() > longestMessagingTargetFactoryPath.length())) {

				longestMessagingTargetFactoryPath = messagingTargetFactoryMount.getKey();
			}
		}

		log.info("Longest matching path of messaging target factory: " + longestMessagingTargetFactoryPath);

		return longestMessagingTargetFactoryPath;
	}

	public synchronized MessagingTargetFactory getMessagingTargetFactory(String messagingTargetFactoryPath) {

		if (! messagingTargetFactoryPath.startsWith("/")) messagingTargetFactoryPath = "/" + messagingTargetFactoryPath;

		MessagingTargetFactoryMount messagingTargetFactoryMount = this.messagingTargetFactoryMounts.get(messagingTargetFactoryPath);

		return messagingTargetFactoryMount == null ? null : messagingTargetFactoryMount.getMessagingTargetFactory();
	}

	public synchronized String[] getMessagingTargetFactoryPaths() {

		return this.messagingTargetFactoryMounts.keySet().toArray(new String[this.messagingTargetFactoryMounts.size()]);
	}
}
