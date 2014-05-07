package org.apache.cxf.cfgproto.spring;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.apache.cxf.feature.Feature;

import com.google.common.collect.Lists;

public class FeatureRegistry {

	private final List<Feature> features = Lists.newArrayList();

	public void addFeature(Feature... features) {
		this.features.addAll(newArrayList(features));
	}
	
	public void addFeature(List<Feature> features) {
		this.features.addAll(features);
	}
	
	public void addFeature(Feature features) {
		this.features.add(features);
	}

	public List<Feature> getFeatures() {
		return features;
	}

}
