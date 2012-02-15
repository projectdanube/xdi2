package xdi2.core.impl.keyvalue.map;

import java.util.HashSet;
import java.util.Set;

public class DefaultSetFactory implements SetFactory {

	@Override
	public Set<String> newSet(String key) {

		return new HashSet<String> ();
	}
}
