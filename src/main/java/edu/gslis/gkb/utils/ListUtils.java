package edu.gslis.gkb.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils
{
	public static List<Double> normalize(List<Double> x) {
		List<Double> n = new ArrayList<Double>(x.size());
		double sum = 0.0;
		for(Double xx : x)
			sum += xx;
		
		for(Double xx : x)
			n.add(xx / sum);
		return n;
	}
}
