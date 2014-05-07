package org.apache.cxf.cfgproto.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;


@Bean
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JaxRsFeature {

	String[] name() default {};

	Autowire autowire() default Autowire.NO;

	String initMethod() default "";

	String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;
	
}
