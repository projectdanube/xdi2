package xdi2.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * Registers all known messaging targets.
 * 
 * @author markus
 */
public class EndpointRegistry {

	private static final Log log = LogFactory.getLog(EndpointRegistry.class);

	private List<MessagingTarget> messagingTargets;
	private Map<String, MessagingTarget> messagingTargetsByPath;

	public EndpointRegistry() {

		this.messagingTargets = new ArrayList<MessagingTarget> ();
		this.messagingTargetsByPath = new HashMap<String, MessagingTarget>();
	}

	public synchronized void init(ApplicationContext applicationContext) {

		log.info("Registering messaging targets...");

		this.shutdown();

		// look up and register all messaging targets

		Map<String, MessagingTarget> messagingTargets = applicationContext.getBeansOfType(MessagingTarget.class);

		for (Map.Entry<String, MessagingTarget> entry : messagingTargets.entrySet()) {

			String path = entry.getKey();
			MessagingTarget messagingTarget = entry.getValue();

			if (! path.startsWith("/")) continue;

			try {

				messagingTarget.init(this);
			} catch (Exception ex) {

				log.warn("Exception while initializing messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			}

			this.registerMessagingTarget(path, messagingTarget);
		}

		// done

		log.info("Done. " + this.messagingTargets.size() + " messaging targets registered.");
	}

	public synchronized void shutdown() {

		int size = this.messagingTargets.size();

		// shutdown all our messaging targets

		List<MessagingTarget> tempList = new ArrayList<MessagingTarget> (this.messagingTargets);

		for (MessagingTarget messagingTarget : tempList) { 

			this.unregisterMessagingTarget(messagingTarget);

			try {

				messagingTarget.shutdown();
			} catch (Exception ex) {

				log.warn("Exception while shutting down messaging target " + messagingTarget.getClass().getCanonicalName() + ": " + ex.getMessage(), ex);
			}
		}

		tempList.clear();

		// done

		log.info(size + " messaging targets were shut down.");
	}

	public synchronized Iterator<MessagingTarget> getMessagingTargets() {

		return this.messagingTargets.iterator();
	}

	/**
	 * Registers a messaging target in the registry.
	 * @param messagingTarget The messaging target to register.
	 */
	public synchronized void registerMessagingTarget(String path, MessagingTarget messagingTarget) {

		if (path == null) throw new NullPointerException("Cannot register a messaging target without path.");

		// register messaging target

		while (path.startsWith("/")) path = path.substring(1);

		this.messagingTargets.add(messagingTarget);
		this.messagingTargetsByPath.put(path, messagingTarget);

		// done

		log.info("Messaging target " + messagingTarget.getClass().getCanonicalName() + " registered at path " + path + ".");
	}

	/**
	 * Unregisters a messaging target from the registry.
	 * @param messagingTarget The messaging target to unregister.
	 */
	public synchronized void unregisterMessagingTarget(MessagingTarget messagingTarget) {

		// unregister messaging target

		this.messagingTargets.remove(messagingTarget);

		for (Iterator<Entry<String, MessagingTarget>> messagingTargetsByPath = this.messagingTargetsByPath.entrySet().iterator(); messagingTargetsByPath.hasNext(); ) {

			Entry<String, MessagingTarget> messagingTargetByPath = messagingTargetsByPath.next();

			if (messagingTargetByPath.getValue() == messagingTarget) messagingTargetsByPath.remove();
		}
	}

	public synchronized MessagingTarget getMessagingTarget(String messagingTargetPath) {

		if (messagingTargetPath.startsWith("/")) messagingTargetPath = messagingTargetPath.substring(1);

		return this.messagingTargetsByPath.get(messagingTargetPath);
	}

	public synchronized String[] getMessagingTargetPaths(MessagingTarget messagingTarget) {

		List<String> messagingTargetPaths = new ArrayList<String> ();
		
		for (Entry<String, MessagingTarget> entry : this.messagingTargetsByPath.entrySet()) {

			if (entry.getValue() == messagingTarget) messagingTargetPaths.add(entry.getKey());
		}

		return(messagingTargetPaths.toArray(new String[messagingTargetPaths.size()]));
	}

	public synchronized String findMessagingTargetPath(String path) {

		if (path.startsWith("/")) path = path.substring(1);

		String longestMessagingTargetPath = null;

		for (Map.Entry<String, MessagingTarget> messagingTargetByPath : this.messagingTargetsByPath.entrySet()) {

			if (path.startsWith(messagingTargetByPath.getKey()) && (longestMessagingTargetPath == null || messagingTargetByPath.getKey().length() > longestMessagingTargetPath.length())) {

				longestMessagingTargetPath = messagingTargetByPath.getKey();
			}
		}

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
