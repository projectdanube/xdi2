package xdi2.transport.spring;

import java.util.List;

import org.springframework.core.convert.converter.Converter;

import xdi2.client.impl.ManipulatorList;
import xdi2.client.manipulator.Manipulator;

public class ListManipulatorListConverter implements Converter<List<Manipulator>, ManipulatorList> {

	@Override
	public ManipulatorList convert(List<Manipulator> source) {

		ManipulatorList target = new ManipulatorList();

		for (Manipulator item : source)
			target.addManipulator(item);

		return target;
	}
}
