package xdi2.core.properties;

import java.io.IOException;
import java.util.Properties;

public class XDI2Properties extends Properties {

	private static final long serialVersionUID = -4409818670666316380L;

	public final static Properties properties;

	static {

		properties = new Properties();

		try {

			properties.load(XDI2Properties.class.getResourceAsStream("xdi2.properties"));
		} catch (IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
