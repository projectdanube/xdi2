/*******************************************************************************
 * Copyright (c) 2008 Parity Communications, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Sabadello - Initial API and implementation
 *******************************************************************************/
package xdi2.util;

import java.util.UUID;

import org.eclipse.higgins.xdi4j.impl.memory.MemoryGraphFactory;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3SubSegment;

/**
 * Various utility methods for cloning graph components.
 * 
 * @author msabadello at parityinc dot net
 */
public final class XriUtil {

	protected static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private XriUtil() { }


	public static XRI3SubSegment randomSubSegment() {

		return new XRI3SubSegment("$" + UUID.randomUUID().toString().replace("-", "."));
	}

	public static XRI3 extractParentXri(XRI3 xri) {

		StringBuffer buffer = new StringBuffer();

		if (xri.hasPath()) {

			buffer.append(xri.getAuthority());

			for (int i=0; i<xri.getPath().getNumSegments() - 1; i++) {

				buffer.append("/");
				buffer.append(xri.getPath().getSegment(i).toString());
			}
		} else {

			return null;
		}

		return new XRI3(buffer.toString());
	}

	public static XRI3Segment extractParentXriSegment(XRI3Segment xri) {

		StringBuffer buffer = new StringBuffer();

		if (xri.getNumSubSegments() > 1) {

			for (int i=0; i<xri.getNumSubSegments() - 1; i++) {

				buffer.append(xri.getSubSegment(i).toString());
			}
		} else {

			return null;
		}

		return new XRI3Segment(buffer.toString());
	}

	public static XRI3Segment extractLocalXriSegment(XRI3Segment xri) {

		if (xri.getNumSubSegments() > 0) {

			return new XRI3Segment("" + xri.getLastSubSegment());
		} else {

			return null;
		}
	}
}
