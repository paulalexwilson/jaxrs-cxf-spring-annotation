package org.apache.cxf.cfgproto.spring;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public final class JaxRsServiceRegistry {
	
	private List<Object> jaxRsServices = new ArrayList<Object>();
	
	/**
	 * Add a collection of services to the service list of this JAX-RS
	 * Server.
	 * 
	 * @param services the services to add to the service list
	 */
	public void addServices(Object... services) {
		jaxRsServices.addAll(asList(services));
	}

	/**
	 * Get the collection of services registered to this registry.
	 * 
	 * @return an iterable collection of services
	 */
	public List<Object> getServices() {
		return jaxRsServices;
	}

}