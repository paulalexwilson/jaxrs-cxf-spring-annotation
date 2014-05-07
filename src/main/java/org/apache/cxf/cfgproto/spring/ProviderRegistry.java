package org.apache.cxf.cfgproto.spring;

import static java.util.Arrays.asList;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * A registry of providers.
 * 
 * @author pwilson
 */
public class ProviderRegistry {

	private final List<Object> providers = Lists.newArrayList();
	
	public void addProvider(Object... providers) {
		this.providers.addAll(asList(providers));
	}
	
	public List<Object> getProviders() {
		return providers;
	}
	
}
