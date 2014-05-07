package org.apache.cxf.cfgproto.spring.client.enable;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.cxf.cfgproto.spring.JaxRsComponentConfigurationUtils.selectFirstAndOnlyItem;
import static org.apache.cxf.cfgproto.spring.JaxRsComponentConfigurationUtils.selectFirstMandatory;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Feature;

import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.client.JaxRsClientConfigurer;
import org.apache.cxf.jaxrs.client.ClientConfiguration;

import com.google.common.base.Function;

/**
 * Holds a collection of {@link JaxRsClientConfigurer configurers} and applies 
 * each of them when a configuration method, e.g. 
 * {@link this#addInInterceptor(InterceptorRegistry)}, is invoked.
 * <p>
 * If the configuration is over a <em>collection</em> of registered components
 * then this composite will typically aggregate the registrations from each
 * configurer. If the configuration is over a single configuration object, say
 * the JaxRs client <code>address</code>, then it will assert that only a single 
 * such registration has been made (i.e. conflict detection) or return 
 * <code>null</code>.
 * 
 * @author pwilson
 */
public class JaxRsClientConfigurationComposite {

	private List<JaxRsClientConfigurer> configurers = newArrayList();
	
	public void setJaxRsClientConfigurers(
			List<JaxRsClientConfigurer> clientConfigurers) {
		this.configurers = clientConfigurers;
	}

	public Class<?> serviceType() {
		return selectFirstMandatory(
				listOfValuesFor(getServiceType()), 
			    "serviceType");
	}

	public String address() {
		return selectFirstMandatory(
				listOfValuesFor(getAddresses()),
			    "address");
	}

	public void configureClient(ClientConfiguration clientConfiguration) {
		for (JaxRsClientConfigurer configurer: configurers) {
			configurer.configureClient(clientConfiguration);
		}
	}

	public void addOutInterceptors(InterceptorRegistry interceptorRegistry) {
		for (JaxRsClientConfigurer configurer: configurers) {
			configurer.addOutInterceptors(interceptorRegistry);
		}
	}

	public void addInInterceptors(InterceptorRegistry interceptorRegistry) {
		for (JaxRsClientConfigurer configurer: configurers) {
			configurer.addInInterceptors(interceptorRegistry);
		}
	}

	public void addProviders(List<Object> providers) {
		for (JaxRsClientConfigurer configurer: configurers) {
			configurer.addProviders(providers);
		}
	}

	public void addFeatures(List<Feature> features) {
		for (JaxRsClientConfigurer configurer: configurers) {
			configurer.addFeatures(features);
		}
	}

	public String transportFactory() {
		return selectFirstAndOnlyItem(
				listOfValuesFor(getTransportFactory()),
			    "transportFactory");
	}
	
	private <T> Set<T> listOfValuesFor(Function<JaxRsClientConfigurer, T> function) {
		return from(configurers)
					.transform(function)
					.filter(notNull())
					.toSet();
	}
	
	private Function<JaxRsClientConfigurer, String> getTransportFactory() {
		return new Function<JaxRsClientConfigurer, String>() {
			@Override
			public String apply(JaxRsClientConfigurer configurer) {
				return configurer.transportFactory();
			}
		};
	}

	private Function<JaxRsClientConfigurer, String> getAddresses() {
		return new Function<JaxRsClientConfigurer, String>() {
			@Override
			public String apply(JaxRsClientConfigurer configurer) {
				return configurer.address();
			}
		};
	}
	
	private Function<JaxRsClientConfigurer, Class<?>> getServiceType() {
		return new Function<JaxRsClientConfigurer, Class<?>>() {
			@Override
			public Class<?> apply(JaxRsClientConfigurer configurer) {
				return configurer.serviceType();
			}
		};
	}
	
}