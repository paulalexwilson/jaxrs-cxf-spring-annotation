package org.apache.cxf.cfgproto.spring.server;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.FluentIterable.from;
import static org.apache.cxf.cfgproto.spring.JaxRsComponentConfigurationUtils.selectFirstAndOnlyItem;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.cfgproto.spring.FeatureRegistry;
import org.apache.cxf.cfgproto.spring.InterceptorRegistry;
import org.apache.cxf.cfgproto.spring.JaxRsServiceRegistry;
import org.apache.cxf.cfgproto.spring.ProviderRegistry;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

import com.google.common.base.Function;

/**
 * Holds a collection of {@link JaxRsServerConfigurer configurers} and applies 
 * each of them when a configuration method, e.g. 
 * {@link this#addServices(JaxRsServiceRegistry)}, is invoked.
 * <p>
 * If the configuration is over a <em>collection</em> of registered components
 * then this composite will typically aggregate the registrations from each
 * configurer. If the configuration is over a single configuration object, say
 * the JaxRs server <code>address</code>, then it will assert that only a single 
 * such registration has been made (i.e. conflict detection) or return 
 * <code>null</code>.
 * 
 * @author pwilson
 */
public class JaxRsServerConfigurationComposite {
	
	private List<JaxRsServerConfigurer> configurers = new ArrayList<JaxRsServerConfigurer>();

	public void addJaxRsServerConfigurers(List<JaxRsServerConfigurer> configurers) {
		this.configurers = configurers;
	}

	public String getAddress() {
		return selectFirstAndOnlyItem(
					from(configurers)
					.transform(getAddresses())
				    .filter(notNull())
				    .toSet(), 
				    "address");
	}

	public String getTransport() {
		return selectFirstAndOnlyItem(
					from(configurers)
					.transform(getTransports())
				    .filter(notNull())
				    .toSet(), 
				    "transport");
	}

	public Bus getBus() {
		return selectFirstAndOnlyItem(
					from(configurers)
					.transform(getBuses())
		   		 	.filter(notNull())
		   		 	.toSet(), 
		   		 	"bus");
	}
	
	public void addProviders(ProviderRegistry providerRegistry) {
		for (JaxRsServerConfigurer configurer: configurers) {
			configurer.addProviders(providerRegistry);
		}
	}
	
	public void addFeatures(FeatureRegistry featureRegistry) {
		for (JaxRsServerConfigurer configurer: configurers) {
			configurer.addFeatures(featureRegistry);
		}
	}

	public void addServices(JaxRsServiceRegistry serviceRegistry) {
		for (JaxRsServerConfigurer configurer: configurers) {
			configurer.addServices(serviceRegistry);
		}
	}

	public void addOutInterceptors(InterceptorRegistry outInterceptorRegistry) {
		for (JaxRsServerConfigurer configurer: configurers) {
			configurer.addOutInterceptors(outInterceptorRegistry);
		}
	}

	public void addInInterceptors(InterceptorRegistry inInterceptorRegistry) {
		for (JaxRsServerConfigurer configurer: configurers) {
			configurer.addInInterceptors(inInterceptorRegistry);
		}
	}
	
	public void configureFactoryBean(JAXRSServerFactoryBean factoryBean) {
		for (JaxRsServerConfigurer configurer: configurers) {
			configurer.configureFactoryBean(factoryBean);
		}
	}
	
	private Function<? super JaxRsServerConfigurer, String> getAddresses() {
		return new Function<JaxRsServerConfigurer, String>() {
			@Override
			public String apply(JaxRsServerConfigurer configurer) {
				return configurer.serverAddress();
			}
		};
	}
	
	private Function<? super JaxRsServerConfigurer, String> getTransports() {
		return new Function<JaxRsServerConfigurer, String>() {
			@Override
			public String apply(JaxRsServerConfigurer configurer) {
				return configurer.serverTransport();
			}
		};
	}
	
	private Function<? super JaxRsServerConfigurer, Bus> getBuses() {
		return new Function<JaxRsServerConfigurer, Bus>() {
			@Override
			public Bus apply(JaxRsServerConfigurer configurer) {
				return configurer.serverBus();
			}
		};
	}
	
}