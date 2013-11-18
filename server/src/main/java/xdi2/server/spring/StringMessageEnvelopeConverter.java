package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.messaging.MessageEnvelope;

public class StringMessageEnvelopeConverter implements Converter<String, MessageEnvelope> {

	@Override
	public MessageEnvelope convert(String string) {

		try {

			return MessageEnvelope.fromGraph(MemoryGraphFactory.getInstance().parseGraph(string));
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
