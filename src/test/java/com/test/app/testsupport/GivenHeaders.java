package com.test.app.testsupport;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Given-annotation that specifies a collection of headers to be 
 * set on the client request before it is sent.
 * 
 * @author pwilson
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface GivenHeaders {

	/**
	 * A collection of headers to set on the request.
	 */
	Header[] value();

}
