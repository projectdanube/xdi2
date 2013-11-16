package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;

public class StringGraphConverter implements Converter<String, Graph> {

	@Override
	public Graph convert(String string) {

		try {

			return MemoryGraphFactory.getInstance().parseGraph(string);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
