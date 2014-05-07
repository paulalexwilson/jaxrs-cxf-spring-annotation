package com.test.app.testsupport;


import static java.util.Arrays.asList;

import javax.ws.rs.core.MultivaluedHashMap;

import org.apache.cxf.jaxrs.client.Client;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Test execution listener capable of configuring a client's headers based upon 
 * {@link GivenHeaders} annotations declared on the current test method.
 * 
 * @author pwilson
 */
public class HeaderSettingJaxRsClientProxyTestExecutionListener extends 
		AbstractTestExecutionListener {

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		if (hasNoHeaderDeclarations(testContext)) {
			return;
		}
		ApplicationContext context = getApplicationContext(testContext);
		Client client = getClient(context);
		MultivaluedHashMap<String, String> headers = getHeaders(testContext);
		client.headers(headers);
	}

	private boolean hasNoHeaderDeclarations(TestContext testContext) {
		return testContext.getTestMethod().getAnnotation(GivenHeaders.class) == null;
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		if (hasNoHeaderDeclarations(testContext)) {
			return;
		}
		ApplicationContext context = getApplicationContext(testContext);
		Client client = getClient(context);
		client.reset();
	}
	
	private Client getClient(ApplicationContext context) {
		try {
			return context.getBean(Client.class);
		} catch (BeansException e) {
			
			throw new IllegalStateException("No Client bean found registered with the application context."
										  + " Please configure your test set up register a single test client.", e);
		}
	}
	
	private ApplicationContext getApplicationContext(TestContext testContext) {
		return testContext.getApplicationContext();
	}

	private MultivaluedHashMap<String, String> getHeaders(TestContext testContext) {
		GivenHeaders headerAnnotation = testContext.getTestMethod().getAnnotation(GivenHeaders.class);
		Header[] headers = headerAnnotation.value();
		MultivaluedHashMap<String, String> headerMap = new MultivaluedHashMap<String, String>(headers.length);
		for (Header header: headers) {
			headerMap.put(header.name(), asList(header.value()));
		}
		return headerMap;
 	}

}