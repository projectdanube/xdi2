package xdi2.messaging.instantiation;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.nodetypes.XdiAbstractVariable;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CompoundCopyStrategy;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceEscapedVariablesCopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceLiteralVariablesCopyStrategy;
import xdi2.core.util.CopyUtil.ReplaceXDIAddressCopyStrategy;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageTemplate;
import xdi2.messaging.constants.XDIMessagingConstants;

public class MessageInstantiation {

	private static final Logger log = LoggerFactory.getLogger(MessageInstantiation.class);

	public static final XDIArc XDI_ARC_V_SENDER = XDIMessagingConstants.XDI_ARC_V_FROM;
	public static final XDIArc XDI_ARC_V_FROM_PEER_ROOT = XDIMessagingConstants.XDI_ARC_V_FROM_ROOT;
	public static final XDIArc XDI_ARC_V_TO_PEER_ROOT = XDIMessagingConstants.XDI_ARC_V_TO_ROOT;

	public static final XDIArc XDI_ARC_V_INSTANCE = XDIArc.create("{*!:uuid:0}");

	private MessageTemplate messageTemplate;
	private Map<XDIArc, Object> variableValues;

	public MessageInstantiation(MessageTemplate messageTemplate, Map<XDIArc, Object> variableValues) {

		this.messageTemplate = messageTemplate;
		this.variableValues = variableValues;
	}

	public MessageInstantiation(MessageTemplate messageTemplate) {

		this(messageTemplate, new HashMap<XDIArc, Object> ());
	}

	public MessageInstantiation() {

		this(null, new HashMap<XDIArc, Object> ());
	}

	public Message execute() {

		// set up variable values including peer roots

		Map<XDIArc, Object> variableValues = new HashMap<XDIArc, Object> (this.getVariableValues());

		for (Map.Entry<XDIArc, Object> entry : this.getVariableValues().entrySet()) {

			XDIArc key = entry.getKey();
			Object value = entry.getValue();

			if ((! XdiPeerRoot.isValidXDIArc(key)) && value instanceof XDIAddress && ((XDIAddress) value).getNumXDIArcs() == 1) {

				XDIArc peerKey = XDIArc.create(key.toString().replace("{", "{(").replaceAll("}", ")}"));	// TODO: do this better
				if (variableValues.containsKey(peerKey)) continue;

				XDIArc peerValue = XdiPeerRoot.createPeerRootXDIArc((XDIAddress) value);

				variableValues.put(peerKey, peerValue);
			}
		}

		if (log.isDebugEnabled()) log.debug("Variable values: " + variableValues);

		// use variables

		Object instanceLiteralDataValue = XdiAbstractVariable.getVariableLiteralDataValue(variableValues, XDI_ARC_V_INSTANCE);
		Object instanceXDIArcValue = XdiAbstractVariable.getVariableXDIArcValue(variableValues, XDI_ARC_V_INSTANCE);
		XDIAddress senderValue = XdiAbstractVariable.getVariableXDIAddressValue(variableValues, XDI_ARC_V_SENDER);
		XDIArc fromPeerRootValue = XdiAbstractVariable.getVariableXDIArcValue(variableValues, XDI_ARC_V_FROM_PEER_ROOT);
		XDIArc toPeerRootValue = XdiAbstractVariable.getVariableXDIArcValue(variableValues, XDI_ARC_V_TO_PEER_ROOT);

		if (senderValue == null) throw new NullPointerException("No sender " + XDI_ARC_V_SENDER + "  value.");
		if (fromPeerRootValue == null) throw new NullPointerException("No FROM peer root " + XDI_ARC_V_FROM_PEER_ROOT + "  value.");
		if (toPeerRootValue == null) throw new NullPointerException("No TO peer root " + XDI_ARC_V_TO_PEER_ROOT + "  value.");

		// create message

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message;

		if (instanceLiteralDataValue instanceof Double) {

			message = messageEnvelope.createMessage(senderValue, ((Double) instanceLiteralDataValue).longValue());
		} else if (instanceXDIArcValue != null) {	// TODO here use the instanceXDIArcValue

			message = messageEnvelope.createMessage(senderValue);
		} else {

			message = messageEnvelope.createMessage(senderValue);
		}

		message.setFromPeerRootXDIArc(fromPeerRootValue);
		message.setToPeerRootXDIArc(toPeerRootValue);

		// TODO: make sure all variables in the link contract template have assigned values

		// instantiate

		CopyStrategy copyStrategy = new CompoundCopyStrategy(
				new ReplaceXDIAddressCopyStrategy(variableValues),
				new ReplaceLiteralVariablesCopyStrategy(variableValues),
				new ReplaceEscapedVariablesCopyStrategy());
		CopyUtil.copyContextNodeContents(this.getMessageTemplate().getContextNode(), message.getContextNode(), copyStrategy);

		if (log.isDebugEnabled()) log.debug("Instantiated message " + message + " from message template " + this.getMessageTemplate());

		// add type statement

		Dictionary.setContextNodeType(message.getContextNode(), this.getMessageTemplate().getContextNode().getXDIAddress());

		// done

		return message;
	}

	/*
	 * Getters and setters
	 */

	public MessageTemplate getMessageTemplate() {

		return this.messageTemplate;
	}

	public void setMessageTemplate(MessageTemplate messageTemplate) {

		this.messageTemplate = messageTemplate;
	}

	public Map<XDIArc, Object> getVariableValues() {

		return this.variableValues;
	}

	public void setVariableValues(Map<XDIArc, Object> variableValues) {

		this.variableValues = variableValues;
	}

	public void setVariableValue(XDIArc key, Object value) {

		if (this.variableValues == null) this.variableValues = new HashMap<XDIArc, Object> ();
		this.variableValues.put(key, value);
	}
}
