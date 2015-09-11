package xdi2.discovery.cache;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import xdi2.client.XDIClient;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;

public class DiscoveryCacheKey implements Serializable {

	private static final long serialVersionUID = -2109761083423630152L;

	private XDIAddress query;
	private URI xdiEndpointUri;
	private Set<XDIAddress> endpointUriTypes;

	public DiscoveryCacheKey(XDIAddress query, URI xdiEndpointUri, Set<XDIAddress> endpointUriTypes) {

		this.query = query;
		this.xdiEndpointUri = xdiEndpointUri;
		this.endpointUriTypes = endpointUriTypes;
	}

	public static DiscoveryCacheKey build(XDIAddress query, XDIClient<?> registryXdiClient, XDIAddress[] endpointUriTypes) {

		if (! (registryXdiClient instanceof XDIHttpClient)) return null;

		return new DiscoveryCacheKey(query, ((XDIHttpClient) registryXdiClient).getXdiEndpointUri(), endpointUriTypes == null ? null : new HashSet<XDIAddress> (Arrays.asList(endpointUriTypes)));
	}

	public static DiscoveryCacheKey build(CloudNumber cloudNumber, URI xdiEndpointUri, XDIAddress[] endpointUriTypes) {

		return new DiscoveryCacheKey(cloudNumber.getXDIAddress(), xdiEndpointUri, endpointUriTypes == null ? null : new HashSet<XDIAddress> (Arrays.asList(endpointUriTypes)));
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + ((this.query == null) ? 0 : this.query.hashCode());
		result = prime * result + ((this.xdiEndpointUri == null) ? 0 : this.xdiEndpointUri.hashCode());
		result = prime * result + ((this.endpointUriTypes == null) ? 0 : this.endpointUriTypes.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		DiscoveryCacheKey other = (DiscoveryCacheKey) obj;

		if (this.query == null) {

			if (other.query != null) return false;
		} else if (! this.query.equals(other.query)) return false;

		if (this.xdiEndpointUri == null) {

			if (other.xdiEndpointUri != null) return false;
		} else if (! this.xdiEndpointUri.equals(other.xdiEndpointUri)) return false;

		if (this.endpointUriTypes == null) {

			if (other.endpointUriTypes != null) return false;
		} else if (! this.endpointUriTypes.equals(other.endpointUriTypes)) return false;

		return true;
	}

	@Override
	public String toString() {

		return "DiscoveryCacheKey [query=" + this.query + ", xdiEndpointUri=" + this.xdiEndpointUri + ", endpointUriTypes=" + this.endpointUriTypes + "]";
	}
}
