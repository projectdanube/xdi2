package xdi2.messaging.target.contributor.impl.digest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.secrettokens.SecretTokens;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * This contributor can generate secret tokens in digest form in a target graph.
 */
// TODO: fix variable syntax
@ContributorMount(
		contributorXDIAddresses={"{}<$digest><$secret><$token>", "<$digest><$secret><$token>"},
		operationXDIAddresses={"$do<$digest><$secret><$token>"}
)
public class GenerateDigestSecretTokenContributor extends AbstractContributor implements Prototype<GenerateDigestSecretTokenContributor> {

	private static final Logger log = LoggerFactory.getLogger(GenerateDigestSecretTokenContributor.class);

	public static final XDIAddress XDI_ADD_DIGEST_SECRET_TOKEN = XDIAddress.create("$do<$digest><$secret><$token>");

	private String globalSalt;
	private Graph targetGraph;

	public GenerateDigestSecretTokenContributor(String globalSalt, Graph targetGraph) {

		this.globalSalt = globalSalt;
		this.targetGraph = targetGraph;
	}

	public GenerateDigestSecretTokenContributor() {

		this(null, null);
	}

	/*
	 * Prototype
	 */

	@Override
	public GenerateDigestSecretTokenContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		GenerateDigestSecretTokenContributor contributor = new GenerateDigestSecretTokenContributor();

		// set the global salt

		contributor.setGlobalSalt(this.getGlobalSalt());

		// set the graph

		contributor.setTargetGraph(this.getTargetGraph());

		// done

		return contributor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (this.getTargetGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setTargetGraph(((GraphMessagingTarget) messagingTarget).getGraph()); 
		if (this.getTargetGraph() == null) throw new Xdi2MessagingException("No target graph.", null, null);

		if (this.getGlobalSalt() == null) throw new Xdi2MessagingException("No global salt.", null, null);
	}

	/*
	 * Contributor methods
	 */

	@Override
	public ContributorResult executeDoOnLiteralStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		Object literalData = relativeTargetStatement.getLiteralData();

		// check parameters

		if (! (literalData instanceof String)) return ContributorResult.SKIP_MESSAGING_TARGET;

		String secretToken = (String) literalData;

		// generate local salt and digest secret token

		String localSaltAndDigestSecretToken;

		try {

			localSaltAndDigestSecretToken = SecretTokens.localSaltAndDigestSecretToken(secretToken, this.getGlobalSalt());
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Problem while creating digest secret token: " + ex.getMessage(), ex, executionContext);
		}

		if (log.isDebugEnabled()) log.debug("Created digest secret token: " + localSaltAndDigestSecretToken);

		// add it to the graph

		ContextNode contextNode = this.getTargetGraph().setDeepContextNode(contributorsAddress);
		if (! XdiAbstractAttribute.isValid(contextNode)) throw new Xdi2MessagingException("Can only create a digest secret token on an attribute.", null, executionContext);
		XdiAttribute localSaltAndDigestSecretTokenXdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
		localSaltAndDigestSecretTokenXdiAttribute.setLiteralDataString(localSaltAndDigestSecretToken);

		// done

		return ContributorResult.SKIP_MESSAGING_TARGET;
	}

	/*
	 * Getters and setters
	 */

	public String getGlobalSalt() {

		return this.globalSalt;
	}

	public void setGlobalSalt(String globalSalt) {

		this.globalSalt = globalSalt;
	}

	public Graph getTargetGraph() {

		return this.targetGraph;
	}

	public void setTargetGraph(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}
}
