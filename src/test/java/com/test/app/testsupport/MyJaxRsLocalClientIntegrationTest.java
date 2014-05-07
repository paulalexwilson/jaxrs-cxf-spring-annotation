package com.test.app.testsupport;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Composite 'stereotype' annotation 
 * 
 * @author pwilson
 */
@TestExecutionListeners(listeners={
		DependencyInjectionTestExecutionListener.class, 
		HeaderSettingJaxRsClientProxyTestExecutionListener.class})
@Retention(RUNTIME)
@Target(TYPE)
public @interface MyJaxRsLocalClientIntegrationTest {
	
}
