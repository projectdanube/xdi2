package xdi2.messaging.target.contributor.impl.digest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.DoOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.impl.authentication.secrettoken.DigestSecretTokenAuthenticator;

/**
 * This contributor can generate secret tokens in digest form in a target graph.
 */
@ContributorXri(addresses={"{{=@+*!}}$digest$secret<$token>", "{{(=@+*!)}}$digest$secret<$token>", "$digest$secret<$token>"})
public class GenerateDigestSecretTokenContributor extends AbstractContributor implements Prototype<GenerateDigestSecretTokenContributor> {

	private static final Logger log = LoggerFactory.getLogger(GenerateDigestSecretTokenContributor.class);

	public static final XDI3Segment XRI_S_DO_GENERATE = XDI3Segment.create("$do$digest$secret<$token>");

	private String globalSalt;
	private Graph targetGraph;

	public GenerateDigestSecretTokenContributor(String globalSalt, Graph targetGraph) {

		this.globalSalt = globalSalt;
		this.targetGraph = targetGraph;
	}

	public GenerateDigestSecretTokenContributor() {

		this.globalSalt = null;
		this.targetGraph = null;
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
	}

	/*
	 * Contributor methods
	 */

	@Override
	public boolean executeDoOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check operation

		if (! XRI_S_DO_GENERATE.equals(operation.getOperationXri())) return false;

		// check parameters

		Object literalData = relativeTargetStatement.getLiteralData();
		if (! (literalData instanceof String)) return false;

		String secretToken = (String) literalData;

		// generate digest

		String localSaltAndDigestSecretToken;

		try {

			localSaltAndDigestSecretToken = DigestSecretTokenAuthenticator.localSaltAndDigestSecretToken(secretToken, this.getGlobalSalt());
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Problem while creating digest secret token: " + ex.getMessage(), ex, executionContext);
		}

		if (log.isDebugEnabled()) log.debug("Created digest secret token: " + localSaltAndDigestSecretToken);
		
		// add it to the graph

		ContextNode contextNode = this.getTargetGraph().setDeepContextNode(contributorsXri);
		if (! XdiAbstractAttribute.isValid(contextNode)) throw new Xdi2MessagingException("Can only create a digest secret token on an attribute.", null, executionContext);
		XdiAttribute localSaltAndDigestSecretTokenXdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
		localSaltAndDigestSecretTokenXdiAttribute.getXdiValue(true).getContextNode().setLiteralString(localSaltAndDigestSecretToken);

		// done

		return false;
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
