package org.apache.cxf.cfgproto.spring.server;

import org.apache.cxf.Bus;
import org.apache.cxf.cfgproto.spring.FeatureRegistry;
import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.JaxRsServiceRegistry;
import org.apache.cxf.cfgproto.spring.ProviderRegistry;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

/**
 * Convenience adapter to simplify the registration of {@link JaxRsServerConfigurer
 * JaxRsServerConfigurer}s, allowing subclasses to override only the methods they 
 * require to configure the desired behaviour.
 * 
 * @author pwilson
 */
public abstract class JaxRsServerConfigurerAdapter implements JaxRsServerConfigurer {
	
	@Override
	public String serverAddress() {
		return null;
	}

	@Override
	public String serverTransport() {
		return null;
	}

	@Override
	public Bus serverBus() {
		return null;
	}

	@Override
	public void addServices(JaxRsServiceRegistry serviceRegistry) {
	}

	@Override
	public void addOutInterceptors(InterceptorRegistry outInterceptorRegistry) {
	}

	@Override
	public void addInInterceptors(InterceptorRegistry inInterceptorRegistry) {
	}

	@Override
	public void addFaultInInterceptors(
			InterceptorRegistry faultInInterceptorRegistry) {
	}

	@Override
	public void addFaultOutInterceptors(
			InterceptorRegistry faultOutInterceptorRegistry) {
	}

	@Override
	public void addProviders(ProviderRegistry providerRegistry) {
	}

	@Override
	public void addFeatures(FeatureRegistry featureRegistry) {
	}

	@Override
	public void configureFactoryBean(JAXRSServerFactoryBean factoryBean) {
	}
	
}
