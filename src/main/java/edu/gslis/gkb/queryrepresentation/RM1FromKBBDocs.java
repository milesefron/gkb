package edu.gslis.gkb.queryrepresentation;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import edu.gslis.gkb.entitylinking.AnnotatedDocKBBridgeDOM;
import edu.gslis.gkb.entitylinking.AnnotatedDocument;
import edu.gslis.gkb.utils.ListUtils;
import edu.gslis.searchhits.SearchHit;
import edu.gslis.searchhits.SearchHits;
import edu.gslis.textrepresentation.FeatureVector;

/**
 * Should give results very similar to those from the RM1 class.  But this version demonstrates how to 
 * pull document text from files on disk prepared by an entity linker.
 * 
 * @author Miles Efron
 *
 */
public class RM1FromKBBDocs extends FeedbackMethod
{
	public static final String FILENAME_EXTENTION = ".xml";
	
	public enum EntityLinker {
		FACC,
		KBB
	}
	
	private String pathToLinkedDocs; // specifies the *directory* holding a bunch of annotated documents
	private EntityLinker linker;
	
	@Override
	public void estimate()
	{
		// should do better error checking, but...
		if(pathToLinkedDocs == null) {
			System.err.println("No path to annotated documents specified.  Can't estimate RM1!");
			System.exit(1);
		}
		
		if(origQuery == null) {
			System.err.println("No starting query specified.  Can't estimate RM1!");
			System.exit(1);
		}
		
		// do our initial retrieval		
		SearchHits topk = index.runQuery(origQuery, fbDocs);
		fbDocVectors = new ArrayList<FeatureVector>(topk.size());
		fbDocScores  = new ArrayList<Double>(topk.size());
		Iterator<SearchHit> it = topk.iterator();
		while(it.hasNext()) {
			SearchHit hit = it.next();
			
			String pathToFile = pathToLinkedDocs + File.separator + hit.getDocno() + FILENAME_EXTENTION;
			AnnotatedDocument doc = null;
			
			if(linker == EntityLinker.KBB)
				doc = new AnnotatedDocKBBridgeDOM();
			else if(linker == EntityLinker.FACC)
				; // TODO
			else {
				System.err.println("No viable entity linker specified.  Can't estimate RM1!");
				System.exit(1);
			}
			
			// get rid of punctuation and case-fold.
			doc.setGroomText(true);
			
			// read the serialized document from disk
			doc.read(pathToFile);
			
			// load our document with terms.  we iterate over integer-valued doc positions
			FeatureVector dv = new FeatureVector(stopper);
			Map<Integer,String> docTokens = doc.getTokens();
			for(Integer position : docTokens.keySet())
				dv.addTerm(docTokens.get(position));
				
			double score = Math.exp(hit.getScore());
			fbDocVectors.add(dv);
			fbDocScores.add(score);
		}
		fbDocScores = ListUtils.normalize(fbDocScores);
		
		// fit the model
		FeatureVector queryVector = new FeatureVector(origQuery, stopper);
		queryVector.normalize();
		
		fbModel = new FeatureVector(stopper);
		for(int i=0; i<fbDocVectors.size(); i++) {
			FeatureVector dv = fbDocVectors.get(i);
			double docScore  = fbDocScores.get(i);
			
			
			
			double docLength = dv.getLength();
			for(String term : queryVector.getFeatures()) {
				if(stopper.isStopWord(term))
					continue;
				double freqInDoc = dv.getFeatureWeight(term);
				double pr = freqInDoc / docLength;
				double probInQuery = queryVector.getFeatureWeight(term);
				fbModel.addTerm(term, probInQuery * pr * docScore);
			}
		}
	}
	
	public void setLinker(EntityLinker linker) 
	{
		this.linker = linker;
	}
}
