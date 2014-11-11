package xdi2.transport.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.messaging.request.RequestMessageEnvelope;

public class StringRequestMessageEnvelopeConverter implements Converter<String, RequestMessageEnvelope> {

	@Override
	public RequestMessageEnvelope convert(String string) {

		try {

			return RequestMessageEnvelope.fromGraph(MemoryGraphFactory.getInstance().parseGraph(string));
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
