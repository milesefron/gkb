package edu.gslis.gkb.queryrepresentation;

import java.util.List;

import edu.gslis.indexes.IndexWrapper;
import edu.gslis.textrepresentation.FeatureVector;
import edu.gslis.utils.Stopper;

/**
 * Abstract class for handling basic setup for estimating a pseudo-relevance feedback model.  Handles setting
 * of high-level params and retrieving the final model.  Does make the assumption that the final model can
 * be represented as a FeatureVector.
 * 
 * To create a new feedback method, clients need to subclass this and implement the <pre>estimate()</pre> method.
 * 
 * @author Miles Efron
 *
 */
public abstract class FeedbackMethod
{
	protected String origQuery;
	protected FeatureVector fbModel;
	protected Stopper stopper;
	protected IndexWrapper index;
	protected List<FeatureVector> fbDocVectors;
	protected List<Double> fbDocScores;
	
	protected int fbDocs  = 20;
	protected int fbTerms = 20;
	
	
	/**
	 * This is where the action happens
	 */
	public abstract void estimate();
	
	public void setOrigQuery(String origQuery)
	{
		this.origQuery = origQuery;
	}
	public void setStopper(Stopper stopper)
	{
		this.stopper = stopper;
	}
	public void setFbDocs(int fbDocs)
	{
		this.fbDocs = fbDocs;
	}
	public void setFbTerms(int fbTerms)
	{
		this.fbTerms = fbTerms;
	}
	public void setIndex(IndexWrapper index)
	{
		this.index = index;
	}
	
	public FeatureVector getModel() 
	{
		fbModel.clip(fbTerms);
		return fbModel;
	}
	
}
