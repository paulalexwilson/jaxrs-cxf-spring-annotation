package org.apache.cxf.cfgproto.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Configures the application context with various beans required for CXF.
 * 
 * @author pwilson
 */
@Configuration
@ImportResource(value="classpath:/META-INF/cxf/cxf.xml")
public class CommonCxfConfiguration {}
