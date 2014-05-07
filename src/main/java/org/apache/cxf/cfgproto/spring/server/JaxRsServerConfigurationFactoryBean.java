package org.apache.cxf.cfgproto.spring.server;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.cfgproto.spring.FeatureRegistry;
import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.JaxRsServiceRegistry;
import org.apache.cxf.cfgproto.spring.ProviderRegistry;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Factory bean responsible for creating a {@link Server JAX-RS Server} and
 * making it available with the context.
 * <p>
 * Supports configuration via container-defined {@link JaxRsServerConfigurer 
 * configurers} and by configuration set directly on the factory itself.
 * Container registered server configurers provide lower-priority configuration than
 * configuration set directly on this factory bean. This allows general configuration
 * to be set, but for server-specific configuration to be overlayed.
 * <p>
 * Alternatively, if complete control over the described {@link JAXRSServerFactoryBean} 
 * is required, one can remove the {@link JaxRsServer @JaxRsServer} annotation and
 * directly subclass this, overriding methods such as {@link #getAddress()},
 * {@link #getServices()} or even {@link #configureFactoryBean(JAXRSServerFactoryBean)}.
 * 
 * @author pwilson
 */
public class JaxRsServerConfigurationFactoryBean implements FactoryBean<Server>, ApplicationContextAware {
	
	private Server server;
	private String address;
	private String transport;
	
	private final JaxRsServiceRegistry serviceRegistry = new JaxRsServiceRegistry();
	private Class<? extends Annotation>[] serviceAnnotationMarkerTypes;

	private final InterceptorRegistry outInterceptorRegistry = new InterceptorRegistry();
	private final InterceptorRegistry inInterceptorRegistry = new InterceptorRegistry();
	private final ProviderRegistry providerRegistry = new ProviderRegistry();
	private final FeatureRegistry featureRegistry = new FeatureRegistry();
	
	private ApplicationContext applicationContext;

	private final JaxRsServerConfigurationComposite configurers = new JaxRsServerConfigurationComposite();
	
	@Override
	public Server getObject() throws Exception {
		return server;
	}

	@Override
	public Class<Server> getObjectType() {
		return Server.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@PostConstruct
	private void createServer() {
		server = createServerFromConfiguration();
	}

	private Server createServerFromConfiguration() {
		JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
		addConfiguredInterceptors();
		globalFactoryBeanConfiguration(factoryBean);
		factoryBean = configureFactoryBean(factoryBean);
		return factoryBean.create();
	}

	/*
	 * Allow globally configurers configure the factory bean
	 */
	private void globalFactoryBeanConfiguration(JAXRSServerFactoryBean factoryBean) {
		configurers.configureFactoryBean(factoryBean);
	}

	/**
	 * Opportunity for subclasses to totally control the configuration of a JAX-RS 
	 * server. An override method could even create a new factory bean and return 
	 * that instead of the one provided.
	 * 
	 * @param factoryBean the created factory bean
	 */
	protected JAXRSServerFactoryBean configureFactoryBean(JAXRSServerFactoryBean factoryBean) {
		factoryBean.setAddress(getAddress());
		factoryBean.setTransportId(getTransport());
		factoryBean.setBus(getBus());
		factoryBean.setServiceBeans(getServices());
		factoryBean.setProviders(getProviders());
		factoryBean.setFeatures(getFeatures());
		factoryBean.setInInterceptors(inInterceptorRegistry.getInterceptors());
		factoryBean.setOutInterceptors(outInterceptorRegistry.getInterceptors());
		return factoryBean;
	}

	private List<?> getProviders() {
		configurers.addProviders(providerRegistry);
		addProviders(providerRegistry);
		return providerRegistry.getProviders();
	}
	
	protected void addProviders(ProviderRegistry providers) {}
	
	private List<Feature> getFeatures() {
		configurers.addFeatures(featureRegistry);
		addFeatures(featureRegistry);
		return featureRegistry.getFeatures();
	}
	
	protected void addFeatures(FeatureRegistry features) {}
	
	private List<Object> getServices() {
		configurers.addServices(serviceRegistry);
		addServices(serviceRegistry);
		addServicesFromMarkerAnnotations(serviceRegistry);
		return serviceRegistry.getServices();
	}

	private void addServicesFromMarkerAnnotations(JaxRsServiceRegistry serviceRegistry) {
		if (serviceAnnotationMarkerTypes == null) {
			return;
		}
		for (Class<? extends Annotation> annotationType: serviceAnnotationMarkerTypes) {
			Map<String, Object> beans = applicationContext.getBeansWithAnnotation(annotationType);
			for (Object annotatedBean: beans.values()) {
				serviceRegistry.addServices(annotatedBean);
			}
		}
	}

	protected void addServices(JaxRsServiceRegistry serviceRegistry) {}
	
	private void addConfiguredInterceptors() {
		configurers.addInInterceptors(inInterceptorRegistry);
		configurers.addOutInterceptors(outInterceptorRegistry);
	}
	
	protected void addOutInterceptors(InterceptorRegistry interceptorRegistry) {}

	protected String getTransport() {
		if (transport != null) {
			return transport;
		}
		return configurers.getTransport();
	}

	protected String getAddress() {
		if (address != null) {
			return address;
		}
		return configurers.getAddress();
	}

	protected Bus getBus() {
		Bus bus = configurers.getBus();
		if (bus == null) {
			return new SpringBusFactory(applicationContext).createBus();
		}
		return bus;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}
	
	public void setServiceBeans(List<Object> serviceBeans) {
		this.serviceRegistry.addServices(serviceBeans.toArray());
	}
	
	public void setProviders(List<Object> providers) {
		this.providerRegistry.addProvider(providers.toArray());
	}
	
	public void setFeatures(List<Feature> features) {
		this.featureRegistry.addFeature(features);
	}
	
	public void setServiceAnnotationMarkerTypes(Class<? extends Annotation>[] serviceAnnotationMarkerTypes) {
		this.serviceAnnotationMarkerTypes = serviceAnnotationMarkerTypes;
	}

	@Autowired(required = false)
	public void setContainerServerConfigurers(
			List<JaxRsServerConfigurer> containerServerConfigurers) {
		this.configurers.addJaxRsServerConfigurers(containerServerConfigurers);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
}