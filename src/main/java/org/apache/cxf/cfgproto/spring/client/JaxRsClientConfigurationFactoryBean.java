package org.apache.cxf.cfgproto.spring.client;

import javax.annotation.PostConstruct;

import org.apache.cxf.Bus;
import org.apache.cxf.cfgproto.spring.FeatureRegistry;
import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.ProviderRegistry;
import org.apache.cxf.cfgproto.spring.client.enable.JaxRsClientConfigurationComposite;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for the creation of JAX-RS {@link Client clients}. 
 * 
 * TODO: complete
 * 
 * @author pwilson
 */
public class JaxRsClientConfigurationFactoryBean implements FactoryBean<Client> {

	private Client client;
	private String address;
	private String transport;
	
	private final InterceptorRegistry outInterceptorRegistry = new InterceptorRegistry();
	private final InterceptorRegistry inInterceptorRegistry = new InterceptorRegistry();
	private final ProviderRegistry providerRegistry = new ProviderRegistry();
	private final FeatureRegistry featureRegistry = new FeatureRegistry();
	
	private final JaxRsClientConfigurationComposite configurers = new JaxRsClientConfigurationComposite();
	
	@Override
	public Client getObject() throws Exception {
		return client;
	}

	@Override
	public Class<?> getObjectType() {
		return Client.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@PostConstruct
	private void createClient() {
		client = createClientFromConfiguration();
	}

	protected Client createClientFromConfiguration() {
		JAXRSClientFactoryBean factoryBean = new JAXRSClientFactoryBean();
		factoryBean.setAddress(getAddress());
		factoryBean.setTransportId(getTransport());
		factoryBean.setBus(getBus());
		return factoryBean.create();
	}

	private Bus getBus() {
		return null;
	}

	private String getTransport() {
		return null;
	}

	private String getAddress() {
		return null;
	}

}
