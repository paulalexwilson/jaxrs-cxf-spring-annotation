package com.test.app.testsupport.registrar;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static org.apache.cxf.jaxrs.client.JAXRSClientFactory.create;
import static org.apache.cxf.message.Message.PROTOCOL_HEADERS;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.cxf.cfgproto.spring.FeatureRegistry;
import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.JaxRsProviders;
import org.apache.cxf.cfgproto.spring.JaxRsService;
import org.apache.cxf.cfgproto.spring.JaxRsServices;
import org.apache.cxf.cfgproto.spring.ProviderRegistry;
import org.apache.cxf.cfgproto.spring.server.JaxRsServer;
import org.apache.cxf.cfgproto.spring.server.JaxRsServerConfigurer;
import org.apache.cxf.cfgproto.spring.server.JaxRsServerConfigurerAdapter;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.local.LocalConduit;
import org.apache.cxf.transport.local.LocalTransportFactory;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.test.app.ProtoEchoService;
import com.test.app.ProtoSimpleEchoService;

/**
 * Tests showcasing the @Enable-annotation driven approach whereby annotations
 * are used to describe the server and its configuration. 
 * <p>
 * The tests show that this configuration style also supports 
 * the configuration style found in Spring WebMVC via the @EnableWebMvc 
 * annotation. In this style, an interface (in our case {@link JaxRsServerConfigurer})
 * declares beans that may be used to configure the server. In our case, 
 * since multiple servers can be declared, the configuration interface is 
 * used to configure <em>all</em> servers within the application context. 
 * <p>
 * Some other interesting capabilities of this framework:
 * <li>
 * 	 * Inheritance can be used to acquire server configuration from a parent class
 *   * Server configuration can additionally acquire server configuration via 
 *     composition (i.e. referencing externally defined interceptors/providers 
 *     etc.).
 *   * Many annotations can be grouped into a single annotation using meta-
 *     annotations to keep configuration clean.
 * </li>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JaxRsServerAnnotationTest {
	
	@Rule
	public ExpectedException thrown = none();
	
	/*
	 * Illustrates the basic configuration of a JAX-RS server with a single 
	 * service registered. The @EnableJaxRsServer annotation defines various
	 * server attributes 
	 */
	@Configuration
	@JaxRsServer(serverName="testServer", address="local://local-address/v1", 
				       transport=LocalTransportFactory.TRANSPORT_ID)
	static class JaxRsServerConfiguration {
		@JaxRsService
		public Object myService() {
			return new ProtoSimpleEchoService();
		}
	}
	
	/*
	 * Illustrates that two servers can be configured within the same context.
	 * Note that the address and server name must be different.
	 * Note also that the annotated factory method can control the name of 
	 * the service bean via the 'name' attribute, which takes on the same
	 * behaviour as @Bean, and that the annotation is compatible with other
	 * Spring factory annotations such as @Scope and @Lazy.
	 */
	@Configuration
	@JaxRsServer(serverName="testServer2", address="local://local-address/v2", 
				       transport=LocalTransportFactory.TRANSPORT_ID)
	static class JaxRsServerConfiguration2 {
		@JaxRsService(name = "myOtherService")
		@Scope(SCOPE_SINGLETON)
		@Lazy(false)
		public Object myService() {
			return new ProtoSimpleEchoService();
		}
	}
	
	/*
	 * Shows that a provider can be referenced through names
	 */
	@Configuration
	@JaxRsServer(serverName="testServer3", address="local://local-address/v3", 
				       transport=LocalTransportFactory.TRANSPORT_ID)
	@JaxRsProviders(providerNames = {"testServer3Provider"})
	static class JaxRsServerConfigurationWithProvider extends JaxRsServerConfiguration {
		public class DummyProvider implements ExceptionMapper<RuntimeException> {
			@Override
			public Response toResponse(RuntimeException exception) {
				return Response.status(PRECONDITION_FAILED).build();
			}
		}
		@Bean
		public Object testServer3Provider() {
			return new DummyProvider();
		}
	}
	
	/*
	 * Illustrates registering globally configured components such as interceptors
	 * that apply to all servers registered in the container.
	 */
	@Configuration
	public static class AddConfigurersInterceptors {
		@Bean
		public JaxRsServerConfigurer createInterceptor() {
			return headerAddingSendOutInterceptor();
		}
		@Bean
		public JaxRsServerConfigurer createGlobalLoggingFeature() {
			return globalLoggingFeature();
		}
		@Bean
		public JaxRsServerConfigurer createJacksonJsonProvider() {
			return jacksonJsonProvider();
		}
	}
	
	/*
	 * Illustrates the composition of meta-annotations on an annotation.
	 * Attributes on a meta-annotation are 'inherited' onto the target
	 * annotation. 
	 */
	@JaxRsServer(transport=LocalTransportFactory.TRANSPORT_ID)
	@JaxRsServices(serviceNames = { "myService" })
	@Target(TYPE)
	@Retention(RUNTIME)
	public @interface TypicalServerConfig {
		String[] serverName() default {};
		String address() default "/";
	}
	
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface MyServiceAnnotation {}
	
	/**
	 * Illustrates broader service registration patterns via a class-level service
	 * annotation.
	 */
	@Configuration
	@TypicalServerConfig(serverName = "testServer4", address="local://local-address/v4")
	@JaxRsServices(annotatedWith=MyServiceAnnotation.class)
	static class JaxRsServerConfigurationServices {
		@MyServiceAnnotation 
		public class AnnotatedService extends ProtoSimpleEchoService {}
		@Bean
		public Object aService() {
			return new AnnotatedService();
		}
	}
	
	@Test
	public void shouldProduceACallableServer() throws Exception {
		assertThat(responseFrom("v1"), is(expectedResponse()));
	}
	
	@Test
	public void shouldSupportMultipleServersConfigured() throws Exception {
		assertThat(responseFrom("v2"), is(expectedResponse()));
	}
	
	@Test
	public void shouldHaveHeaderSetByGlobalRegisteredOutInterceptor() {
		ProtoEchoService service = createClient("v1");
		service.echo(expectedResponse());
		MultivaluedMap<String, Object> headers = getHeaders(service);
		assertThat(headers.get("x-custom-header"), hasItem("expected response value"));
	}

	@Test
	public void shouldUseProviders() throws Exception {
		thrown.expect(ClientErrorException.class);
		thrown.expect(statusCode(412));
		ProtoEchoService client = createClient("v3");
		client.echo("throw exception");
	}
	
	@Test
	public void shouldBeAbleToCallServicesRegisteredViaAnnotations() throws Exception {
		assertThat(responseFrom("v4"), is(expectedResponse()));
	}
	
	private Matcher<?> statusCode(final int status) {
		return new TypeSafeMatcher<ClientErrorException>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Expected: " + status);
			}

			@Override
			protected boolean matchesSafely(ClientErrorException item) {
				return item.getResponse().getStatus() == status;
			}
			
		};
	}

	private static JaxRsServerConfigurer jacksonJsonProvider() {
		return new JaxRsServerConfigurerAdapter() {
			@Override
			public void addProviders(ProviderRegistry providerRegistry) {
				providerRegistry.addProvider(new JacksonJsonProvider());
			}
		};
	}
	
	private static JaxRsServerConfigurer globalLoggingFeature() {
		return new JaxRsServerConfigurerAdapter() {
			@Override
			public void addFeatures(FeatureRegistry featureRegistry) {
				featureRegistry.addFeature(new LoggingFeature());
			}
		};
	}

	private String expectedResponse() {
		return "hello";
	}

	private String responseFrom(String version) {
		return createClient(version).echo(expectedResponse()).echoResponse;
	}
	
	private void configureForLocalTransport(ClientConfiguration config) {
		config.getRequestContext().put(LocalConduit.DIRECT_DISPATCH, true);
	}

	private ClientConfiguration getConfig(ProtoEchoService service) {
		return WebClient.getConfig(service);
	}

	private MultivaluedMap<String, Object> getHeaders(Object service) {
		return WebClient.client(service).getResponse().getHeaders();
	}

	private ProtoEchoService createClient(String addressPath) {
		ProtoEchoService service = createLocalJsonClient(addressPath);
		ClientConfiguration config = getConfig(service);
		configureForLocalTransport(config);
		return service;
	}

	private ProtoEchoService createLocalJsonClient(String addressPath) {
		return create("local://local-address/" + addressPath,
				ProtoEchoService.class, newArrayList(new JacksonJaxbJsonProvider()));
	}
	
	private static JaxRsServerConfigurerAdapter headerAddingSendOutInterceptor() {
		return new JaxRsServerConfigurerAdapter() {
			@Override
			public void addOutInterceptors(InterceptorRegistry outInterceptorRegistry) {
				outInterceptorRegistry.addInterceptors(new AbstractPhaseInterceptor<Message>(Phase.SEND) {
					@Override
					public void handleMessage(Message message) throws Fault {
						@SuppressWarnings("unchecked")
						MetadataMap<String, Object> headers = (MetadataMap<String, Object>) message.get(PROTOCOL_HEADERS);
						headers.add("x-custom-header", "expected response value");
					}
				});
			}
		};
	}

}
