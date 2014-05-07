package org.apache.cxf.cfgproto.spring;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;

/**
 * Baseclass for registrars wishing to create JAX-RS components. 
 * <p>
 * Provides registration mechanisms for the common types of collaborators such as
 * <ul>
 * 	<li>Providers
 *  <li>Features
 *  <li>Out Interceptors
 *  <li>In Interceptors
 *  <li>Out Fault Interceptors
 *  <li>In Fault Interceptors
 * </ul>
 * ... and so on.
 * 
 * @author pwilson
 */
public abstract class JaxRsComponentRegistrar {

	private static final String JAXRS_PROVIDER_ANNOTATION_NAME = JaxRsProvider.class.getName();
	private static final String JAXRS_PROVIDERS_ANNOTATION_NAME = JaxRsProviders.class.getName();
	private static final String JAXRS_FEATURE_ANNOTATION_NAME = JaxRsFeature.class.getName();
	private static final String JAXRS_FEATURES_ANNOTATION_NAME = JaxRsFeatures.class.getName();

	
	public JaxRsComponentRegistrar() {
		super();
	}

	protected void addProviders(RootBeanDefinition beanDefinition, AnnotationMetadata importingClassMetadata) {
		addProvidersFromMethods(beanDefinition, importingClassMetadata);
		addProvidersFromClass(beanDefinition, importingClassMetadata);
	}
	
	protected void addFeatures(RootBeanDefinition beanDefinition, AnnotationMetadata importingClassMetadata) {
		addFeaturesFromMethods(beanDefinition, importingClassMetadata);
		addFeaturesFromClass(beanDefinition, importingClassMetadata);
	}

	private void addProvidersFromClass(RootBeanDefinition beanDefinition, AnnotationMetadata importingClassMetadata) {
		Map<String, Object> classLevelServiceAnnotationAttributes = importingClassMetadata.getAnnotationAttributes(JAXRS_PROVIDERS_ANNOTATION_NAME);
		if (classLevelServiceAnnotationAttributes == null) {
			return;
		}
		Object nameAttribute = classLevelServiceAnnotationAttributes.get("providerNames");
		if (nameAttribute == null) {
			return;
		}
		if (nameAttribute.getClass().isArray()) {
			for (String name : (String[]) nameAttribute) {
				addProviderToBeanDefinition(beanDefinition, name);
			}
		} else {
			addProviderToBeanDefinition(beanDefinition, (String) nameAttribute);
		}
	}

	private void addProviderToBeanDefinition(RootBeanDefinition beanDefinition, String nameAttribute) {
		beanDefinition.getPropertyValues().add("providers", new RuntimeBeanReference(nameAttribute));
	}

	private void addProvidersFromMethods(RootBeanDefinition beanDefinition, AnnotationMetadata importingClassMetadata) {
		Set<MethodMetadata> providerFactoryMethods = getAnnotatedMethods(JAXRS_PROVIDER_ANNOTATION_NAME, importingClassMetadata);
		for (MethodMetadata methodMetadata : providerFactoryMethods) {
			String[] beanNames = getBeanNames(methodMetadata, JAXRS_PROVIDER_ANNOTATION_NAME);
			addProviderToBeanDefinition(beanDefinition, beanNames[0]);
		}
	}
	
	private void addFeaturesFromClass(RootBeanDefinition beanDefinition, AnnotationMetadata importingClassMetadata) {
		Map<String, Object> classLevelProviderAnnotationAttributes = importingClassMetadata.getAnnotationAttributes(JAXRS_FEATURES_ANNOTATION_NAME);
		if (classLevelProviderAnnotationAttributes == null) {
			return;
		}
		Object nameAttribute = classLevelProviderAnnotationAttributes.get("featureNames");
		if (nameAttribute == null) {
			return;
		}
		if (nameAttribute.getClass().isArray()) {
			for (String name : (String[]) nameAttribute) {
				addFeatureToBeanDefinition(beanDefinition, name);
			}
		} else {
			addFeatureToBeanDefinition(beanDefinition, (String) nameAttribute);
		}
	}

	private void addFeatureToBeanDefinition(RootBeanDefinition beanDefinition, String nameAttribute) {
		beanDefinition.getPropertyValues().add("features", new RuntimeBeanReference(nameAttribute));
	}

	private void addFeaturesFromMethods(RootBeanDefinition beanDefinition, AnnotationMetadata importingClassMetadata) {
		Set<MethodMetadata> featureFactoryMethods = getAnnotatedMethods(JAXRS_FEATURE_ANNOTATION_NAME, importingClassMetadata);
		for (MethodMetadata methodMetadata : featureFactoryMethods) {
			String[] beanNames = getBeanNames(methodMetadata, JAXRS_FEATURE_ANNOTATION_NAME);
			addFeatureToBeanDefinition(beanDefinition, beanNames[0]);
		}
	}

	protected Set<MethodMetadata> getAnnotatedMethods(String annotationType, AnnotationMetadata importingClassMetadata) {
		Class<?> clazz = getClassObject(importingClassMetadata);
		Method[] methods = clazz.getMethods();
		Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>();
		for (Method method : methods) {
			if (AnnotatedElementUtils.isAnnotated(method, annotationType)) {
				annotatedMethods.add(new StandardMethodMetadata(method, false));
			}
		}
		return annotatedMethods;
	}

	protected Class<?> getClassObject(AnnotationMetadata importingClassMetadata) {
		try {
			return Class.forName(importingClassMetadata.getClassName());
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Unable to load class object: " + importingClassMetadata.getClassName());
		}
	}
	
	/*
	 * Use the 'name' annotation attribute or fall back to the method name.
	 */
	protected String[] getBeanNames(MethodMetadata methodMetadata, String annotationName) {
		String[] names = (String[]) methodMetadata.getAnnotationAttributes(annotationName).get("name");
		if (names.length == 0) {
			names = new String[]{methodMetadata.getMethodName()};
		}
		return names;
	}

}