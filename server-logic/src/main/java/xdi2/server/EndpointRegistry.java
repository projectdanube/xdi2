package xdi2.server;

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

/**
 * Registry to mount and unmount messaging targets.
 * 
 * @author markus
 */
public class EndpointRegistry {

	private static final Logger log = LoggerFactory.getLogger(EndpointRegistry.class);

	private List<MessagingTarget> messagingTargets;
	private Map<String, MessagingTarget> messagingTargetsByPath;

	public EndpointRegistry() {

		this.messagingTargets = new ArrayList<MessagingTarget> ();
		this.messagingTargetsByPath = new HashMap<String, MessagingTarget>();
	}

	public synchronized void loadApplicationContext(ApplicationContext applicationContext) {

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

		// done

		log.info("Done. " + this.messagingTargets.size() + " messaging targets mounted.");
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
	public synchronized void mountMessagingTarget(String path, MessagingTarget messagingTarget) {

		if (path == null) throw new NullPointerException("Cannot mount a messaging target without path.");

		// mount messaging target

		while (path.startsWith("/")) path = path.substring(1);

		this.messagingTargets.add(messagingTarget);
		this.messagingTargetsByPath.put(path, messagingTarget);

		// init messaging target

		try {

			messagingTarget.init();
		} catch (Exception ex) {

			log.warn("Exception while initializing messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
		}

		// done

		log.info("Messaging target " + messagingTarget.getClass().getCanonicalName() + " mounted at path " + path + ".");
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
	}

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

		if (messagingTargetPath.startsWith("/")) messagingTargetPath = messagingTargetPath.substring(1);

		return this.messagingTargetsByPath.get(messagingTargetPath);
	}

	public synchronized String[] getMessagingTargetPaths(MessagingTarget messagingTarget) {

		List<String> messagingTargetPaths = new ArrayList<String> ();

		for (Entry<String, MessagingTarget> entry : this.messagingTargetsByPath.entrySet()) {

			if (messagingTarget == null || entry.getValue() == messagingTarget) messagingTargetPaths.add(entry.getKey());
		}

		return messagingTargetPaths.toArray(new String[messagingTargetPaths.size()]);
	}

	public synchronized String[] getMessagingTargetPaths() {

		return this.getMessagingTargetPaths(null);
	}

	public synchronized String findMessagingTargetPath(String path) {

		if (path.startsWith("/")) path = path.substring(1);

		log.info("Finding messaging target for path: " + path);

		String longestMessagingTargetPath = null;

		for (Map.Entry<String, MessagingTarget> messagingTargetByPath : this.messagingTargetsByPath.entrySet()) {

			if (path.startsWith(messagingTargetByPath.getKey()) && (longestMessagingTargetPath == null || messagingTargetByPath.getKey().length() > longestMessagingTargetPath.length())) {

				longestMessagingTargetPath = messagingTargetByPath.getKey();
			}
		}

		log.info("Longest matching path of messaging target: " + longestMessagingTargetPath);

		return longestMessagingTargetPath;
	}

	/*	public synchronized String getRequestPath(MessagingTarget messagingTarget, HttpServletRequest request) {

		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath(); 
		String path = requestUri.substring(contextPath.length() + 1);
		String messagingTargetPath = this.getMessagingTargetPath(messagingTarget);

		return(path.substring(messagingTargetPath.length()));
	}*/
}
