package xdi2.core.impl.keyvalue.map;

import java.util.Map;
import java.util.Set;

public interface MapFactory {

	public Map<String, Set<String>> newMap();
}
