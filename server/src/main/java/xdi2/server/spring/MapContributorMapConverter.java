package xdi2.server.spring;

import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.contributor.ContributorMap;

public class MapContributorMapConverter implements Converter<Map<XDI3Segment, List<Contributor>>, ContributorMap> {

	@Override
	public ContributorMap convert(Map<XDI3Segment, List<Contributor>> source) {

		ContributorMap target = new ContributorMap();

		for (Map.Entry<XDI3Segment, List<Contributor>> item : source.entrySet()) 
			for (Contributor item2 : item.getValue())
				target.addContributor(item.getKey(), item2);

		return target;
	}
}
