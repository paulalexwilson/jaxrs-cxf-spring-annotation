package org.apache.cxf.cfgproto.spring.server;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;

import org.apache.cxf.cfgproto.spring.JaxRsComponentRegistrar;
import org.apache.cxf.cfgproto.spring.JaxRsService;
import org.apache.cxf.cfgproto.spring.JaxRsServices;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 * A {@link ImportBeanDefinitionRegistrar registrar} that registers a 
 * {@link JaxRsServerConfigurationFactoryBean factory bean} configured 
 * to create a JAX-RS {@link Server} according to the annotations or 
 * meta-annotations supplied on the target class. 
 * <p>
 * Supported functionality includes:
 * <ul>
 *   <li>Various server attributes may be specified on the {@link JaxRsServer}
 *       annotation.
 *   <li>Factory methods can be annotated with {@link JaxRsService} to mark
 *       the bean as a service of the declared server.
 *   <li>Service references can be added via {@link JaxRsServices#serviceNames()}
 *       allowing bean references to be declaratively defined on a configuration
 *       class.
 *   <li>Groups of services can be added via {@link JaxRsServices#annotatedWith()}
 *       allowing developer-defined stereotype annotations to be applied to 
 *       service classes and registered with a server. This is a generalised form
 *       of the functionality to automatically add all {@link Path} annotated 
 *       beans to the current server.
 * <ul> 
 * <p>
 * All annotations described above can be included as <em>meta-annotations
 * <em>, allowing developers to abstract common configuration setup into
 * centralised annotations to improve code reuse and clarity. Such meta-
 * annotations may specify values that are to be applied to the target 
 * annotation, and the target annotation may provide attributes to allow
 * the further customisation of meta-attributes, by supplying an attribute
 * of the same name. 
 * 
 * @author pwilson
 * @see JAXRSServerFactoryBean
 */
class JaxRsServerBeanRegistrar extends JaxRsComponentRegistrar implements ImportBeanDefinitionRegistrar  {
	
	private static final Class<?> TARGET_FACTORY_BEAN_CLASS = JaxRsServerConfigurationFactoryBean.class;
	private static final String SERVER_ANNOTATION_CLASS_NAME = JaxRsServer.class.getName();
	private static final String JAXRS_SERVICE_ANNOTATION_NAME = JaxRsService.class.getName();
	private static final String JAXRS_SERVICES_ANNOTATION_NAME = JaxRsServices.class.getName();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		RootBeanDefinition beanDefinition = createBeanDefinitionBuilder();
		Map<String, Object> jaxRsServerAttributes = getJaxRsServerAttributes(importingClassMetadata);
		configureBeanProperties(beanDefinition, jaxRsServerAttributes);
		addCollaborators(importingClassMetadata, beanDefinition);
		String serverName = getServerName(importingClassMetadata);
		registerBeanDefinition(registry, serverName, beanDefinition);
	}

	private void addCollaborators(AnnotationMetadata importingClassMetadata,
			RootBeanDefinition beanDefinition) {
		addServiceBeans(beanDefinition, importingClassMetadata);
		addProviders(beanDefinition, importingClassMetadata);
		addFeatures(beanDefinition, importingClassMetadata);
	}

	private void registerBeanDefinition(BeanDefinitionRegistry registry,
			String serverName, RootBeanDefinition beanDefinition) {
		registry.registerBeanDefinition(serverName, beanDefinition);
	}

	private Map<String, Object> getJaxRsServerAttributes(AnnotationMetadata importingClassMetadata) {
		return importingClassMetadata.getAnnotationAttributes(SERVER_ANNOTATION_CLASS_NAME);
	}

	private RootBeanDefinition createBeanDefinitionBuilder() {
		return new RootBeanDefinition(TARGET_FACTORY_BEAN_CLASS);
	}

	private void configureBeanProperties(RootBeanDefinition beanDefinition, Map<String, Object> attributes) {
		beanDefinition.setLazyInit(false);
		beanDefinition.setSource(this);
		beanDefinition.getPropertyValues().add("address", attributes.get("address"));
		beanDefinition.getPropertyValues().add("transport", attributes.get("transport"));
	}

	private void addServiceBeans(BeanDefinition beanDefinition, AnnotationMetadata importingClassMetadata) {
		addServicesFromClass(beanDefinition, importingClassMetadata);
		addServicesFromMethods(beanDefinition, importingClassMetadata);
	}

	private void addServicesFromClass(BeanDefinition beanDefinition, AnnotationMetadata importingClassMetadata) {
		Map<String, Object> classLevelServiceAnnotationAttributes = importingClassMetadata.getAnnotationAttributes(JAXRS_SERVICES_ANNOTATION_NAME);
		if (classLevelServiceAnnotationAttributes == null) {
			return;
		}
		addTypeLevelServiceReferences(beanDefinition, classLevelServiceAnnotationAttributes);
		addTypeLevelServiceAnnotationMarkers(beanDefinition, classLevelServiceAnnotationAttributes);
	}

	private void addTypeLevelServiceAnnotationMarkers(BeanDefinition beanDefinition, Map<String, Object> classLevelServiceAnnotationAttributes) {
		Object annotationAttributes = classLevelServiceAnnotationAttributes.get("annotatedWith");
		if (annotationAttributes == null) {
			return;
		}
		if (annotationAttributes.getClass().isArray()) {
			for (Class<?> annotationRef: (Class<?>[]) annotationAttributes) {
				addServiceAnnotationReferenceToBeanDefinition(beanDefinition, (Class<?>) annotationRef);
			}
		} else {
			addServiceAnnotationReferenceToBeanDefinition(beanDefinition, (Class<?>) annotationAttributes);
		}
	}

	private void addServiceAnnotationReferenceToBeanDefinition(BeanDefinition beanDefinition, Class<?> annotationType) {
		beanDefinition.getPropertyValues().add("serviceAnnotationMarkerTypes", annotationType);
	}

	private void addTypeLevelServiceReferences(BeanDefinition beanDefinition,
			Map<String, Object> classLevelServiceAnnotationAttributes) {
		Object nameAttribute = classLevelServiceAnnotationAttributes.get("serviceNames");
		if (nameAttribute == null) {
			return;
		}
		if (nameAttribute.getClass().isArray()) {
			for (String name: (String[]) nameAttribute) {
				addServiceToBeanDefinition(beanDefinition, name);
			}
		} else {
			addServiceToBeanDefinition(beanDefinition, (String)nameAttribute);
		}
	}

	private void addServiceToBeanDefinition(BeanDefinition beanDefinition, String name) {
		beanDefinition.getPropertyValues().add("serviceBeans", new RuntimeBeanReference(name));
	}

	private void addServicesFromMethods(BeanDefinition beanDefinition,
			AnnotationMetadata importingClassMetadata) {
		Set<MethodMetadata> serviceFactoryMethods = getAnnotatedMethods(JAXRS_SERVICE_ANNOTATION_NAME, importingClassMetadata);
		for (MethodMetadata methodMetadata : serviceFactoryMethods) {
			String[] beanNames = getBeanNames(methodMetadata, JAXRS_SERVICE_ANNOTATION_NAME);
			addServiceToBeanDefinition(beanDefinition, beanNames[0]);
		}
	}
	
	private String getServerName(AnnotationMetadata importingClassMetadata) {
		Map<String, Object> serverAttributes = importingClassMetadata.getAnnotationAttributes(SERVER_ANNOTATION_CLASS_NAME);
		Object nameAttribute = serverAttributes.get("serverName");
		if (nameAttribute.getClass().isArray()) {
			return ((String[]) nameAttribute)[0];
		}
		return (String) nameAttribute;
	}

}
