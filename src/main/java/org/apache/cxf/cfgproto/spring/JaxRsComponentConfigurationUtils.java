package org.apache.cxf.cfgproto.spring;

import static com.google.common.collect.Iterables.getFirst;

import java.util.Set;

/**
 * General utilities for JAX-RS component configuration.
 * 
 * @author pwilson
 */
public final class JaxRsComponentConfigurationUtils {
	
	/**
	 * Selects the single item from the {@link Set} but allows <code>null</code>.
	 * 
	 * @param items a collection of items containing either one or zero items
	 * @param fieldName the field name used for error reporting
	 * @throws IllegalArgumentException when more than a single item exists in the Set
	 * @return the single item, or <code>null</code> if the Set is empty
	 */
	public static <T> T selectFirstAndOnlyItem(Set<T> items, String fieldName) {
		if (items.size() > 1) {
			throw new IllegalArgumentException("Found more than one value configured for [" + fieldName + "]");
		}
		return getFirst(items, null);
	}
	
	/**
	 * Selects the single item from the {@link Set} but throws an {@link IllegalArgumentException}
	 * if the set is empty or contains more than a single item.
	 * 
	 * @param items a collection of items containing either one or zero items
	 * @param fieldName the field name used for error reporting
	 * @throws IllegalArgumentException when more than a single item exists in the Set
	 * @return the single item, or <code>null</code> if the Set is empty
	 */
	public static <T> T selectFirstMandatory(Set<T> items, String fieldName) {
		if (items.size() != 1) {
			throw new IllegalArgumentException("Found [" + items.size() + "] values "
											  + "configured for [" + fieldName + "] "
											  + ": 1 required");
		}
		return getFirst(items, null);
	}
	
	private JaxRsComponentConfigurationUtils() {}
	
}
