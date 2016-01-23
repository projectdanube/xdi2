package xdi2.transport.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.syntax.XDIAddress;

public class StringXDIAddressConverter implements Converter<String, XDIAddress> {

	@Override
	public XDIAddress convert(String string) {

		return XDIAddress.create(string);
	}
}
