package org.apache.cxf.cfgproto.spring.client;

import org.apache.cxf.cfgproto.spring.JaxRsComponentRegistrar;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * A {@link ImportBeanDefinitionRegistrar registrar} that registers a 
 * {@link JaxRsClientConfigurationFactoryBean} into the application context.
 * 
 * @author pwilson
 */
class JaxRsClientBeanRegistrar extends JaxRsComponentRegistrar implements ImportBeanDefinitionRegistrar {
	
	private static final Class<?> TARGET_FACTORY_BEAN_CLASS = JaxRsClientConfigurationFactoryBean.class;
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
							BeanDefinitionRegistry registry) {
		// TODO Complete
	}

	

}
