package org.apache.cxf.cfgproto.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.interceptor.Interceptor;

/**
 * Holds a collection of interceptors.
 * 
 * @author pwilson
 */
public class InterceptorRegistry {

	private List<Interceptor<?>> interceptors = new ArrayList<Interceptor<?>>();
	
	public void addInterceptors(Interceptor<?> interceptor) {
		interceptors.add(interceptor);
	}
	
	public List<Interceptor<?>> getInterceptors() {
		return interceptors;
	}
	
}
