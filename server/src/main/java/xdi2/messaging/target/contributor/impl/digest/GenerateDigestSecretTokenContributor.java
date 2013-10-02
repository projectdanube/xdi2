package xdi2.messaging.target.contributor.impl.digest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.DoOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;
import xdi2.messaging.target.interceptor.impl.authentication.secrettoken.DigestSecretTokenAuthenticator;

@ContributorXri(addresses={"{{=@+*!}}$digest$secret<$token>", "{{(=@+*!)}}$digest$secret<$token>", "$digest$secret<$token>"})
public class GenerateDigestSecretTokenContributor extends AbstractContributor {

	private static final Logger log = LoggerFactory.getLogger(GenerateDigestSecretTokenContributor.class);

	public static final XDI3Segment XRI_S_DO_DIGEST_SECRET_TOKEN = XDI3Segment.create("$do$digest$secret<$token>");
	
	private String globalSalt;

	public GenerateDigestSecretTokenContributor(String globalSalt) {

		this.globalSalt = globalSalt;
	}

	public GenerateDigestSecretTokenContributor() {

	}

	@Override
	public boolean executeDoOnLiteralStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment digestSecretTokenXri = contributorXris[contributorXris.length - 1];

		log.debug("digestSecretTokenXri: " + digestSecretTokenXri);

		if (digestSecretTokenXri.equals("{{}}$digest$secret<$token>")) return false;

		// check operation
		
		if (! XRI_S_DO_DIGEST_SECRET_TOKEN.equals(operation.getOperationXri())) return false;
		
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

		// add it to the response

		XdiAttributeSingleton xdiAttribute = XdiAttributeSingleton.fromContextNode(messageResult.getGraph().setDeepContextNode(contributorsXri));
		xdiAttribute.getXdiValue(true).getContextNode().setLiteralString(localSaltAndDigestSecretToken);

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
}
