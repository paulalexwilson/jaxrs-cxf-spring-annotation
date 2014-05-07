package org.apache.cxf.cfgproto.spring.client;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.ws.rs.core.Feature;

import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.client.enable.EnableJaxRsClient;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.context.annotation.Bean;

/**
 * Supporting class for the creation of JAX-RS proxy clients.
 * <p>
 * Those wishing to create a JAX-RS Client should consider subclassing this
 * support class and overriding methods as desired.
 * <p>
 * For simpler cases, and for cases where configuration composition is required,
 * consider annotating configuration classes with {@link EnableJaxRsClient
 * @EnableJaxRsClient}.
 * 
 * @see EnableJaxRsClient
 * @author pwilson
 */
public abstract class JaxRsClientConfigurationSupport {

	private final InterceptorRegistry outInterceptors = new InterceptorRegistry();
	private final InterceptorRegistry inInterceptors = new InterceptorRegistry();
	private final List<Object> providers = newArrayList();
	private final List<Feature> features = newArrayList();

	@Bean
	public Object client() {
		addCollaborators();
		Object clientProxy = createClient();
		performConfiguration(clientProxy);
		return clientProxy;
	}

	protected void addCollaborators() {
		addOutInterceptors(outInterceptors);
		addInInterceptors(inInterceptors);
		addProviders(providers);
		addFeatures(features);
	}

	protected void performConfiguration(Object clientProxy) {
		ClientConfiguration clientConfiguration = WebClient	.getConfig(clientProxy);
		configureInterceptors(clientConfiguration);
		configureClient(clientConfiguration);
	}

	protected void configureInterceptors(ClientConfiguration clientConfiguration) {
		clientConfiguration.setOutInterceptors(outInterceptors.getInterceptors());
		clientConfiguration.setInInterceptors(inInterceptors.getInterceptors());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object createClient() {
		JAXRSClientFactoryBean clientFactoryBean = new JAXRSClientFactoryBean();
		clientFactoryBean.setAddress(address());
		clientFactoryBean.setProviders(providers);
		clientFactoryBean.setFeatures((List)features);
		clientFactoryBean.setTransportId(transportFactory());
		clientFactoryBean.setServiceClass(serviceType());
		return clientFactoryBean.create();
	}	

	protected List<Object> getProviders() {
		return providers;
	}

	protected List<Feature> getFeatures() {
		return features;
	}

	public abstract Class<?> serviceType();

	public abstract String address();

	public abstract void configureClient(ClientConfiguration clientConfiguration);

	public abstract void addOutInterceptors(InterceptorRegistry interceptorRegistry);

	public abstract void addInInterceptors(InterceptorRegistry interceptorRegistry);

	public abstract void addProviders(List<Object> providers);

	public abstract void addFeatures(List<Feature> features);

	public abstract String transportFactory();

}
