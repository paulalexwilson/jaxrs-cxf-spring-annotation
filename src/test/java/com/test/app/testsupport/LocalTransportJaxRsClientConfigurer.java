package com.test.app.testsupport;

import org.apache.cxf.cfgproto.spring.client.JaxRsClientConfigurationAdapter;
import org.apache.cxf.cfgproto.spring.client.JaxRsClientConfigurer;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.transport.local.LocalConduit;
import org.apache.cxf.transport.local.LocalTransportFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link JaxRsClientConfigurer configurer} setting up the client proxy to 
 * communicate over a local address and using the local transport. Usage of
 * this configurer requires that the local transport dependency is available
 * on the classpath.
 * <p>
 * Enables integration testing outside of the container. Uses the direct dispatch 
 * message mode to prevent the client from timing out in the absence of an 
 * acknowledgement via the local transport mechanism.
 *  
 * @author pwilson
 */
@Configuration
public class LocalTransportJaxRsClientConfigurer {

    @Bean
    public JaxRsClientConfigurer createConfigurer() {
        return new JaxRsClientConfigurationAdapter() {
            private static final String LOCAL_ADDRESS = "local://local-address/";

            @Override
            public String transportFactory() {
                return LocalTransportFactory.TRANSPORT_ID;
            }

            @Override
            public void configureClient(ClientConfiguration clientConfiguration) {
                clientConfiguration.getRequestContext().put(LocalConduit.DIRECT_DISPATCH, Boolean.TRUE);
            }

            @Override
            public String address() {
                return LOCAL_ADDRESS;
            }
        };
    }
	
}
