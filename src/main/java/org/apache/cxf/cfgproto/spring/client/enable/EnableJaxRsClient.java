package org.apache.cxf.cfgproto.spring.client.enable;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.cxf.cfgproto.spring.CommonCxfConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author pwilson
 */
@Import({CommonCxfConfiguration.class, DelegatingJaxRsClientConfiguration.class})
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface EnableJaxRsClient {}
