package com.test.app.testsupport;

import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.cfgproto.spring.client.JaxRsClientConfigurationAdapter;
import org.apache.cxf.cfgproto.spring.client.JaxRsClientConfigurer;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

public class SpringBusJaxRsClientConfigurer implements ApplicationContextAware {
	
	private ApplicationContext applicationContext;

	@Bean
	public JaxRsClientConfigurer createConfigurer() {
		return new JaxRsClientConfigurationAdapter() {
			@Override
			public void configureClient(ClientConfiguration clientConfiguration) {
				clientConfiguration.setBus(new SpringBusFactory(applicationContext).createBus());
			}
		};
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
