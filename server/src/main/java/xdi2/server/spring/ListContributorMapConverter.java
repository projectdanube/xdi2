package xdi2.server.spring;

import java.util.List;

import org.springframework.core.convert.converter.Converter;

import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.contributor.ContributorMap;

public class ListContributorMapConverter implements Converter<List<Contributor>, ContributorMap> {

	@Override
	public ContributorMap convert(List<Contributor> source) {

		ContributorMap target = new ContributorMap();

		for (Contributor item : source)
			target.addContributor(item);

		return target;
	}
}
