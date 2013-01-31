package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.XDI3XRef;

public class StringXDI3XRefConverter implements Converter<String, XDI3XRef> {

	@Override
	public XDI3XRef convert(String string) {

		return XDI3XRef.create(string);
	}
}
