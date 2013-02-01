package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.XDI3InnerGraph;

public class StringXDI3InnerGraphConverter implements Converter<String, XDI3InnerGraph> {

	@Override
	public XDI3InnerGraph convert(String string) {

		return XDI3InnerGraph.create(string);
	}
}
