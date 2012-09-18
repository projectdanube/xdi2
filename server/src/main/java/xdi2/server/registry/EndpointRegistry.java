package xdi2.server.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.factory.MessagingTargetFactory;

/**
 * Registry to mount and unmount messaging targets.
 * 
 * @author markus
 */
public class EndpointRegistry {

	private static final Logger log = LoggerFactory.getLogger(EndpointRegistry.class);

	private List<MessagingTarget> messagingTargets;
	private Map<String, MessagingTarget> messagingTargetsByPath;
	private List<MessagingTargetFactory> messagingTargetFactorys;
	private Map<String, MessagingTargetFactory> messagingTargetFactorysByPath;

	public EndpointRegistry() {

		this.messagingTargets = new ArrayList<MessagingTarget> ();
		this.messagingTargetsByPath = new HashMap<String, MessagingTarget> ();
		this.messagingTargetFactorys = new ArrayList<MessagingTargetFactory> ();
		this.messagingTargetFactorysByPath = new HashMap<String, MessagingTargetFactory> ();
	}

	public synchronized void init(ApplicationContext applicationContext) throws Xdi2ServerException {

		// no application context?

		if (applicationContext == null) {

			log.info("No application context. No messaging targets loaded.");
			return;
		}

		// look up and mount all messaging targets

		log.info("Mounting messaging targets...");

		Map<String, MessagingTarget> messagingTargets = applicationContext.getBeansOfType(MessagingTarget.class);

		for (Map.Entry<String, MessagingTarget> entry : messagingTargets.entrySet()) {

			String path = entry.getKey();
			MessagingTarget messagingTarget = entry.getValue();

			if (! path.startsWith("/")) continue;

			this.mountMessagingTarget(path, messagingTarget);
		}

		// look up and mount all messaging target factories

		log.info("Mounting messaging target factories...");

		Map<String, MessagingTargetFactory> messagingTargetFactorys = applicationContext.getBeansOfType(MessagingTargetFactory.class);

		for (Map.Entry<String, MessagingTargetFactory> entry : messagingTargetFactorys.entrySet()) {

			String path = entry.getKey();
			MessagingTargetFactory messagingTargetFactory = entry.getValue();

			if (! path.startsWith("/")) continue;
			if (! path.endsWith("/*")) continue;

			this.mountMessagingTargetFactory(path, messagingTargetFactory);
		}

		// done

		log.info("Done. " + this.messagingTargets.size() + " messaging targets and " + this.messagingTargetFactorys.size() + " messaging target factories mounted.");
	}

	public synchronized void shutdown() {

		int size = this.messagingTargets.size();

		// unmount all our messaging targets

		List<MessagingTarget> tempList = new ArrayList<MessagingTarget> (this.messagingTargets);

		for (MessagingTarget messagingTarget : tempList) { 

			this.unmountMessagingTarget(messagingTarget);
		}

		tempList.clear();

		// done

		log.info(size + " messaging targets were shut down.");
	}

	/**
	 * Mount a messaging target in the registry.
	 * @param messagingTarget The messaging target to mount.
	 */
	public synchronized void mountMessagingTarget(String messagingTargetPath, MessagingTarget messagingTarget) throws Xdi2ServerException {

		if (messagingTargetPath == null) throw new NullPointerException("Cannot mount a messaging target without path.");

		// already mounted?

		if (this.messagingTargetsByPath.containsKey(messagingTargetPath)) {

			throw new Xdi2ServerException("Messaging target " + this.messagingTargetsByPath.get(messagingTargetPath).getClass().getCanonicalName() + " already mounted at path " + messagingTargetPath + ".");
		}

		// mount messaging target

		while (messagingTargetPath.startsWith("/")) messagingTargetPath = messagingTargetPath.substring(1);
		messagingTargetPath = "/" + messagingTargetPath;

		this.messagingTargets.add(messagingTarget);
		this.messagingTargetsByPath.put(messagingTargetPath, messagingTarget);

		// init messaging target

		try {

			messagingTarget.init();
		} catch (Exception ex) {

			log.warn("Exception while initializing messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			throw new Xdi2ServerException("Exception while initializing messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// done

		log.info("Messaging target " + messagingTarget.getClass().getCanonicalName() + " mounted at path " + messagingTargetPath + ".");
	}

	/**
	 * Mount a messaging target factory in the registry.
	 * @param messagingTargetFactory The messaging target factory to mount.
	 */
	public synchronized void mountMessagingTargetFactory(String messagingTargetFactoryPath, MessagingTargetFactory messagingTargetFactory) throws Xdi2ServerException {

		if (messagingTargetFactoryPath == null) throw new NullPointerException("Cannot mount a messaging target factory without path.");

		// already mounted?

		if (this.messagingTargetFactorysByPath.containsKey(messagingTargetFactoryPath)) {

			throw new Xdi2ServerException("Messaging target factory " + this.messagingTargetFactorysByPath.get(messagingTargetFactoryPath).getClass().getCanonicalName() + " already mounted at path " + messagingTargetFactoryPath + ".");
		}

		// mount messaging target

		while (messagingTargetFactoryPath.startsWith("/")) messagingTargetFactoryPath = messagingTargetFactoryPath.substring(1);
		if (messagingTargetFactoryPath.endsWith("/*")) messagingTargetFactoryPath = messagingTargetFactoryPath.substring(0, messagingTargetFactoryPath.length() - 2);
		messagingTargetFactoryPath = "/" + messagingTargetFactoryPath;

		this.messagingTargetFactorys.add(messagingTargetFactory);
		this.messagingTargetFactorysByPath.put(messagingTargetFactoryPath, messagingTargetFactory);

		// init messaging target factory

		try {

			messagingTargetFactory.init();
		} catch (Exception ex) {

			log.warn("Exception while initializing messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			throw new Xdi2ServerException("Exception while initializing messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// done

		log.info("Messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + " mounted at path " + messagingTargetFactoryPath + ".");
	}

	/**
	 * Unmounts a messaging target from the registry.
	 * @param messagingTarget The messaging target to unmount.
	 */
	public synchronized void unmountMessagingTarget(MessagingTarget messagingTarget) {

		// shutdown messaging target

		try {

			messagingTarget.shutdown();
		} catch (Exception ex) {

			log.warn("Exception while shutting down messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// unmount messaging target

		this.messagingTargets.remove(messagingTarget);

		for (Iterator<Entry<String, MessagingTarget>> messagingTargetsByPath = this.messagingTargetsByPath.entrySet().iterator(); messagingTargetsByPath.hasNext(); ) {

			Entry<String, MessagingTarget> messagingTargetByPath = messagingTargetsByPath.next();

			if (messagingTargetByPath.getValue() == messagingTarget) messagingTargetsByPath.remove();
		}

		// done

		log.info("Messaging target " + messagingTarget.getClass().getCanonicalName() + " unmounted.");
	}

	/**
	 * Unmounts a messaging target factory from the registry.
	 * @param messagingTargetFactory The messaging target factory to unmount.
	 */
	public synchronized void unmountMessagingTargetFactory(MessagingTargetFactory messagingTargetFactory) {

		// shutdown messaging target factory

		try {

			messagingTargetFactory.shutdown();
		} catch (Exception ex) {

			log.warn("Exception while shutting down messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// unmount messaging target factory

		this.messagingTargetFactorys.remove(messagingTargetFactory);

		for (Iterator<Entry<String, MessagingTargetFactory>> messagingTargetFactorysByPath = this.messagingTargetFactorysByPath.entrySet().iterator(); messagingTargetFactorysByPath.hasNext(); ) {

			Entry<String, MessagingTargetFactory> messagingTargetFactoryByPath = messagingTargetFactorysByPath.next();

			if (messagingTargetFactoryByPath.getValue() == messagingTargetFactory) messagingTargetFactorysByPath.remove();
		}

		// done

		log.info("Messaging target factory " + messagingTargetFactory.getClass().getCanonicalName() + " unmounted.");
	}

	/*
	 * MessagingTargets
	 */

	@SuppressWarnings("unchecked")
	public synchronized List<MessagingTarget> getMessagingTargets() {

		return (List<MessagingTarget>) ((ArrayList<MessagingTarget>) this.messagingTargets).clone();
	}

	@SuppressWarnings("unchecked")
	public synchronized Map<String, MessagingTarget> getMessagingTargetsByPath() {

		return (Map<String, MessagingTarget>) ((HashMap<String, MessagingTarget>) this.messagingTargetsByPath).clone();
	}

	public synchronized int getNumMessagingTargets() {

		return this.messagingTargets.size();
	}

	public synchronized MessagingTarget getMessagingTarget(String messagingTargetPath) {

		if (! messagingTargetPath.startsWith("/")) messagingTargetPath = "/" + messagingTargetPath;

		return this.messagingTargetsByPath.get(messagingTargetPath);
	}

	public synchronized String[] getMessagingTargetPaths() {

		return this.messagingTargetsByPath.keySet().toArray(new String[this.messagingTargetsByPath.size()]);
	}

	public synchronized String findMessagingTargetPath(String requestPath) {

		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		log.info("Finding messaging target for path: " + requestPath);

		String longestMessagingTargetPath = null;

		for (Map.Entry<String, MessagingTarget> messagingTargetByPath : this.messagingTargetsByPath.entrySet()) {

			if (requestPath.startsWith(messagingTargetByPath.getKey()) && (longestMessagingTargetPath == null || messagingTargetByPath.getKey().length() > longestMessagingTargetPath.length())) {

				longestMessagingTargetPath = messagingTargetByPath.getKey();
			}
		}

		log.info("Longest matching path of messaging target: " + longestMessagingTargetPath);

		return longestMessagingTargetPath;
	}

	/*
	 * MessagingTargetFactorys
	 */

	@SuppressWarnings("unchecked")
	public synchronized List<MessagingTargetFactory> getMessagingTargetFactorys() {

		return (List<MessagingTargetFactory>) ((ArrayList<MessagingTargetFactory>) this.messagingTargetFactorys).clone();
	}

	@SuppressWarnings("unchecked")
	public synchronized Map<String, MessagingTargetFactory> getMessagingTargetFactorysByPath() {

		return (Map<String, MessagingTargetFactory>) ((HashMap<String, MessagingTargetFactory>) this.messagingTargetFactorysByPath).clone();
	}

	public synchronized int getNumMessagingTargetFactorys() {

		return this.messagingTargetFactorys.size();
	}

	public synchronized MessagingTargetFactory getMessagingTargetFactory(String messagingTargetFactoryPath) {

		if (! messagingTargetFactoryPath.startsWith("/")) messagingTargetFactoryPath = "/" + messagingTargetFactoryPath;

		return this.messagingTargetFactorysByPath.get(messagingTargetFactoryPath);
	}

	public synchronized String[] getMessagingTargetFactoryPaths() {

		return this.messagingTargetFactorysByPath.keySet().toArray(new String[this.messagingTargetFactorysByPath.size()]);
	}

	public synchronized String findMessagingTargetFactoryPath(String requestPath) {

		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		log.info("Finding messaging target factory for path: " + requestPath);

		String longestMessagingTargetFactoryPath = null;

		for (Map.Entry<String, MessagingTargetFactory> messagingTargetFactoryByPath : this.messagingTargetFactorysByPath.entrySet()) {

			if (requestPath.startsWith(messagingTargetFactoryByPath.getKey()) && (longestMessagingTargetFactoryPath == null || messagingTargetFactoryByPath.getKey().length() > longestMessagingTargetFactoryPath.length())) {

				longestMessagingTargetFactoryPath = messagingTargetFactoryByPath.getKey();
			}
		}

		log.info("Longest matching path of messaging target factory: " + longestMessagingTargetFactoryPath);

		return longestMessagingTargetFactoryPath;
	}
}
