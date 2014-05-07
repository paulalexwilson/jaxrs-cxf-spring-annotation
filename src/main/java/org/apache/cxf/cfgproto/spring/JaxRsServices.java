package org.apache.cxf.cfgproto.spring;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Declares a collection of services to be wired into the 
 * server.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface JaxRsServices {

	/**
	 * A collection of service bean names to add to the current server.
	 */
	String[] serviceNames() default {};
	
	/**
	 * Zero or more annotation types which, if declared on a context-
	 * registered bean, will be used a marker to declare the bean as a
	 * service to be registered with the current server. 
	 */
	Class<?>[] annotatedWith() default {};
	
}
