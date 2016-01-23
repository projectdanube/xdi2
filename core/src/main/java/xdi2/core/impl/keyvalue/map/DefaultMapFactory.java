package xdi2.core.impl.keyvalue.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultMapFactory implements MapFactory {

	@Override
	public Map<String, Set<String>> newMap() {

		return new HashMap<String, Set<String>> ();
	}
}
