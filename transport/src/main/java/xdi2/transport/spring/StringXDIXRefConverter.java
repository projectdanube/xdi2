package xdi2.transport.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.syntax.XDIXRef;

public class StringXDIXRefConverter implements Converter<String, XDIXRef> {

	@Override
	public XDIXRef convert(String string) {

		return XDIXRef.create(string);
	}
}
