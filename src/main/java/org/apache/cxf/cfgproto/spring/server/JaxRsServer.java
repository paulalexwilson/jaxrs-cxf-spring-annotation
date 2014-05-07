package org.apache.cxf.cfgproto.spring.server;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.cxf.cfgproto.spring.CommonCxfConfiguration;
import org.apache.cxf.cfgproto.spring.JaxRsProvider;
import org.apache.cxf.cfgproto.spring.JaxRsProviders;
import org.apache.cxf.cfgproto.spring.JaxRsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Annotation to be added alongside {@link Configuration @Configuration}, 
 * which enables the target class to be used to configure a JAX-RS server
 * within the application context. 
 * <p>
 * <pre class="code">
 * &#064;Configuration
 * &#064;JaxRsServer
 * public class MyJaxRsServerConfiguration {
 *
 * }
 * </pre>
 * The above will create a new JAX-RS server but fail due to the absence of any 
 * registered services. Registering services is simple with the {@link JaxRsService}
 * annotation:
 * <pre class="code">
 * &#064;Configuration
 * &#064;JaxRsServer
 * public class MyJaxRsServerConfiguration {
 *     &#064;JaxRsService
 *     public Object myService() {
 *         return new MyService();
 *     }   
 * }
 * </pre>
 * This will register a Spring bean with the name 'myService', and will register
 * this bean as a JAX-RS service within the created server.
 * <p>
 * This configuration approach is compatible with Spring's XML configuration 
 * approach. That is, beans can be shared between contexted defined in both 
 * XML and with annotations.
 * 
 * @author pwilson
 * @see JaxRsServerBeanRegistrar
 * @see JaxRsServerConfigurationFactoryBean
 * @see JaxRsService
 * @see JaxRsServices
 * @see JaxRsProvider
 * @see JaxRsProviders
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({JaxRsServerBeanRegistrar.class, CommonCxfConfiguration.class})
@Configuration
public @interface JaxRsServer {

	/**
	 * The name of the server within the application context.
	 */
	String serverName() default "jaxRsServer";

	/**
	 * Configures the address of the server; either fully qualified or 
	 * relative. The default is the base address:
	 *          <code>'/'</code>
	 */
	String address() default "/";

	/**
	 * The transport identifier used for selecting the appropriate transport
	 * from those available from the classpath dependencies. The default is
	 * the HTTP transport: 
	 * 			<code>"http://cxf.apache.org/transports/http"</code>.
	 */
	String transport() default "http://cxf.apache.org/transports/http";
	
}
