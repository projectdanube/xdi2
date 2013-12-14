package xdi2.server.spring;

import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.contributor.ContributorMap;

public class MapContributorMapConverter implements Converter<Map<?, ?>, ContributorMap> {

	@Override
	public ContributorMap convert(Map<?, ?> source) {

		ContributorMap target = new ContributorMap();

		for (Map.Entry<?, ?> item : source.entrySet()) {

			Object key = item.getKey();

			if (key instanceof String) key = XDI3Segment.create((String) key);
			
			Object value = item.getValue();

			if (value instanceof Contributor) {

				target.addContributor((XDI3Segment) key, (Contributor) value);
			} else if (value instanceof List<?>) {

				for (Object item2 : (List<?>) value) {

					target.addContributor((XDI3Segment) key, (Contributor) item2);
				}
			}
		}

		return target;
	}
}
