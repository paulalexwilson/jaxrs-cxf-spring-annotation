package org.apache.cxf.cfgproto.spring.client.enable;

import java.util.List;

import javax.ws.rs.core.Feature;

import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.client.JaxRsClientConfigurationSupport;
import org.apache.cxf.cfgproto.spring.client.JaxRsClientConfigurer;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration class capable of applying the configuration from a collection of
 * context-registered {@link JaxRsClientConfigurer client configurers} to a single
 * JAX-RS client.
 * 
 * @author pwilson
 */
@Configuration
public class DelegatingJaxRsClientConfiguration extends JaxRsClientConfigurationSupport {
	
	private final JaxRsClientConfigurationComposite configurers = new JaxRsClientConfigurationComposite();
	
	@Autowired(required = false)
	public void setJaxRsClientConfigurers(List<JaxRsClientConfigurer> configurers) {
		if (configurers == null || configurers.isEmpty()) {
			return;
		}
		this.configurers.setJaxRsClientConfigurers(configurers);
	}

	@Override
	public Class<?> serviceType() {
		return configurers.serviceType();
	}

	@Override
	public String address() {
		return configurers.address();
	}

	@Override
	public void configureClient(ClientConfiguration clientConfiguration) {
		configurers.configureClient(clientConfiguration);
	}

	@Override
	public void addOutInterceptors(InterceptorRegistry interceptorRegistry) {
		configurers.addOutInterceptors(interceptorRegistry);
	}

	@Override
	public void addInInterceptors(InterceptorRegistry interceptorRegistry) {
		configurers.addInInterceptors(interceptorRegistry);
	}

	@Override
	public void addProviders(List<Object> providers) {
		configurers.addProviders(providers);
	}

	@Override
	public void addFeatures(List<Feature> features) {
		configurers.addFeatures(features);
	}

	@Override
	public String transportFactory() {
		return configurers.transportFactory();
	}

}
