package org.apache.cxf.cfgproto.spring;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.cxf.cfgproto.spring.server.JaxRsServer;

/**
 * Annotation to be placed on a server configuration class (annotated with
 * {@link JaxRsServer @EnableJaxRsServer}, declaring bean names of 
 * components to be supplied as providers.
 * 
 * @author pwilson
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface JaxRsProviders {

	String[] providerNames();

}
