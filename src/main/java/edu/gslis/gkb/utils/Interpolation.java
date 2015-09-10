package edu.gslis.gkb.utils;

import java.util.Iterator;
import java.util.List;

import edu.gslis.textrepresentation.FeatureVector;

public class Interpolation
{

	public static FeatureVector interpolate(List<FeatureVector> vectors, List<Double> weights) {
		if(vectors.size() != weights.size()) {
			System.err.println("unequal vectors/weights in vector interpolation.");
			System.exit(1);
		}
		FeatureVector combined = new FeatureVector(null);
		Iterator<FeatureVector> vit = vectors.iterator();
		Iterator<Double> wit = weights.iterator();
		while(vit.hasNext()) {
			FeatureVector v = vit.next();
			double w = wit.next();
			Iterator<String> fit = v.iterator();
			while(fit.hasNext()) {
				String feature = fit.next();
				double featureWeight = v.getFeatureWeight(feature);
				featureWeight *= w;
				combined.addTerm(feature, featureWeight);
			}
		}
		return combined;
	}
}
