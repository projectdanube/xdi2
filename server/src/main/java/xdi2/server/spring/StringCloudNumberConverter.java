package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.CloudNumber;

public class StringCloudNumberConverter implements Converter<String, CloudNumber> {

	@Override
	public CloudNumber convert(String string) {

		return CloudNumber.create(string);
	}
}
