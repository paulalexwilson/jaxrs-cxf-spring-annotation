package org.apache.cxf.cfgproto.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;

/**
 * Marks a method as a factory for beans that are to be registered as a 
 * JAX-RS service.
 * 
 * @author pwilson
 */
@Bean
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JaxRsService {
	
	String[] name() default {};
	
	Autowire autowire() default Autowire.NO;

	String initMethod() default "";

	String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;
}
