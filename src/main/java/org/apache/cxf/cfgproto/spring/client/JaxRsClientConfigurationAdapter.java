package org.apache.cxf.cfgproto.spring.client;

import java.util.List;

import javax.ws.rs.core.Feature;

import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.jaxrs.client.ClientConfiguration;

public class JaxRsClientConfigurationAdapter implements JaxRsClientConfigurer {
	
	@Override
	public Class<?> serviceType() {
		return null;
	}

	@Override
	public String address() {
		return null;
	}

	@Override
	public String transportFactory() {
		return null;
	}

	@Override
	public void addOutInterceptors(InterceptorRegistry interceptorRegistry) {
	}

	@Override
	public void addInInterceptors(InterceptorRegistry interceptorRegistry) {
	}

	@Override
	public void addProviders(List<Object> providers) {
	}

	@Override
	public void addFeatures(List<Feature> features) {
	}

	@Override
	public void configureClient(ClientConfiguration clientConfiguration) {
	}

}
