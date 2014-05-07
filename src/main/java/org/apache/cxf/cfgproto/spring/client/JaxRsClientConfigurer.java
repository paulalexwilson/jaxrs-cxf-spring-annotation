package org.apache.cxf.cfgproto.spring.client;

import java.util.List;

import javax.ws.rs.core.Feature;

import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.jaxrs.client.ClientConfiguration;

/**
 * A bean capable of configuring the client proxy within the application context. Implementations
 * may supply portions of the client proxy configuration and leave other implementations to
 * implement the remainder.
 * <p>
 * Implementations may provide <code>null</code> if they do not wish to provide a specific
 * configuration value.
 * 
 * @author pwilson
 */
public interface JaxRsClientConfigurer {

    /**
     * Provides the service type that the client proxy should generate or <code>null</code> if this
     * configurer wishes not to provide the service type.
     */
    Class<?> serviceType();

    /**
     * Provides the service address to send messages to or <code>null</code> if this configurer
     * wishes not to provide the address.
     */
    String address();

    /**
     * Provides a transport factory over which messages will be sent.
     */
    String transportFactory();

    /**
     * Add zero or more interceptors to the out-interceptor chain.
     */
    void addOutInterceptors(InterceptorRegistry interceptorRegistry);

    /**
     * Add zero or more interceptors to the in-interceptor chain.
     */
    void addInInterceptors(InterceptorRegistry interceptorRegistry);

    /**
     * Add zero or more providers to the client.
     */
    void addProviders(List<Object> providers);

    /**
     * Add zero or more features to the client.
     */
    void addFeatures(List<Feature> features);

    /**
     * Apply some configuration to the client's configuration instance.
     */
    void configureClient(ClientConfiguration clientConfiguration);
	
}
