package xdi2.transport.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.messaging.response.ResponseMessageEnvelope;

public class StringResponseMessageEnvelopeConverter implements Converter<String, ResponseMessageEnvelope> {

	@Override
	public ResponseMessageEnvelope convert(String string) {

		try {

			return ResponseMessageEnvelope.fromGraph(MemoryGraphFactory.getInstance().parseGraph(string));
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
