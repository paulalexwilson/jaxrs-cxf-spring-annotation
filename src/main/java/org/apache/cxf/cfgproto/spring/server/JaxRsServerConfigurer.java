package org.apache.cxf.cfgproto.spring.server;

import org.apache.cxf.Bus;
import org.apache.cxf.cfgproto.spring.FeatureRegistry;
import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.JaxRsServiceRegistry;
import org.apache.cxf.cfgproto.spring.ProviderRegistry;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

/**
 * Interface enabling context-registered components to participate in some or
 * all of the configuration of a JAX-RS server.
 * 
 * @author pwilson
 */
public interface JaxRsServerConfigurer {
	
	String serverAddress();
	
	String serverTransport();
	
	Bus serverBus();

	void addServices(JaxRsServiceRegistry serviceRegistry);
	
	void addOutInterceptors(InterceptorRegistry outInterceptorRegistry);
	
	void addInInterceptors(InterceptorRegistry inInterceptorRegistry);
	
	void addFaultInInterceptors(InterceptorRegistry faultInInterceptorRegistry);
	
	void addFaultOutInterceptors(InterceptorRegistry faultOutInterceptorRegistry);

	void addProviders(ProviderRegistry providerRegistry);
	
	void addFeatures(FeatureRegistry featureRegistry);
	
	void configureFactoryBean(JAXRSServerFactoryBean factoryBean);
	
}
