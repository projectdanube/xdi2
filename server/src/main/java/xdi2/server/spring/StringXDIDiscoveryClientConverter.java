package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.client.http.XDIHttpClient;
import xdi2.discovery.XDIDiscoveryClient;

public class StringXDIDiscoveryClientConverter implements Converter<String, XDIDiscoveryClient> {

	@Override
	public XDIDiscoveryClient convert(String string) {

		return new XDIDiscoveryClient(new XDIHttpClient(string));
	}
}
