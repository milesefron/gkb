package edu.gslis.gkb.queryrepresentation;

import java.util.ArrayList;
import java.util.Iterator;

import edu.gslis.gkb.utils.ListUtils;
import edu.gslis.searchhits.SearchHit;
import edu.gslis.searchhits.SearchHits;
import edu.gslis.textrepresentation.FeatureVector;

/**
 * Included for instruction and as a sanity check.  This class estimates the model by pulling
 * document contents out of an IndexWrapper (e.g. an indri index).  No entity linking stuff is
 * involved.
 * 
 * @author Miles Efron
 *
 */
public class RM1 extends FeedbackMethod
{
	
	
	@Override
	public void estimate()
	{
		// do our initial retrieval		
		SearchHits topk = index.runQuery(origQuery, fbDocs);
		fbDocVectors = new ArrayList<FeatureVector>(topk.size());
		fbDocScores  = new ArrayList<Double>(topk.size());
		Iterator<SearchHit> it = topk.iterator();
		while(it.hasNext()) {
			SearchHit hit = it.next();
			FeatureVector dv = index.getDocVector(hit.getDocID(), null);
			double score = Math.exp(hit.getScore());
			fbDocVectors.add(dv);
			fbDocScores.add(score);
		}
		fbDocScores = ListUtils.normalize(fbDocScores);
		
		// fit the model		
		fbModel = new FeatureVector(stopper);
		for(int i=0; i<fbDocVectors.size(); i++) {
			FeatureVector dv = fbDocVectors.get(i);
			double docScore  = fbDocScores.get(i);
			
			
			
			double docLength = dv.getLength();
			for(String term : dv.getFeatures()) {
				if(stopper.isStopWord(term))
					continue;
				double freqInDoc = dv.getFeatureWeight(term);
				double pr = freqInDoc / docLength;
				
				fbModel.addTerm(term, pr * docScore);
			}
		}

	}
}
