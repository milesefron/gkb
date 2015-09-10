package edu.gslis.gkb.entitylinking;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import edu.gslis.textrepresentation.FeatureVector;
import edu.gslis.utils.Stopper;

/**
 * Represents a document that has been run through an entity linker.
 * 
 * @author Miles Efron
 *
 */
public abstract class AnnotatedDocument
{
	protected final static Pattern NON_TEXT_PATTERN = Pattern.compile("[,\\.\\?;:'\\\"!\\$%&\\(\\)-]", Pattern.DOTALL);
	protected boolean groomText = true;
	
	protected String docno;
	protected Map<Integer,String> tokens;
	protected List<Mention> mentions;
	protected List<EntityLink> links;
	
	/**
	 * The NIST-assigned docno field
	 * @return
	 */
	public String getDocno()
	{
		return docno;
	}
	public void setDocno(String docno)
	{
		this.docno = docno;
	}
	
	/**
	 * Represents the token sequence observed in the doc.  
	 * Uses a Map instead of a List or similar in case we've omitted some tokens upstream.  
	 * i.e. You can rebuild as much of the doc as we've stored by iterating from 0..tokens.size(), but
	 * you must check for "holes" by looking for key->null slots.
	 * @return
	 */
	public Map<Integer,String> getTokens()
	{
		return tokens;
	}
	public void setTokens(Map<Integer,String> tokens)
	{
		this.tokens = tokens;
	}
	public List<EntityLink> getLinks()
	{
		return links;
	}
	public void setMentions(List<Mention> mentions)
	{
		this.mentions = mentions;
	}
	public List<Mention> getMentions()
	{
		return mentions;
	}
	public void setLinks(List<EntityLink> links)
	{
		this.links = links;
	}	
	/**
	 * should we get rid of punctuation/capitalization?
	 * @param groomText
	 */
	public void setGroomText(boolean groomText)
	{
		this.groomText = groomText;
	}
	
	// read the serialized document data from disck
	public abstract void read(String pathToFile);
	
	/**
	 * get a vector representation of this document's text
	 * @param stopper if null, we don't do any stopping
	 * @return
	 */
	public abstract FeatureVector getDocVector(Stopper stopper);
}
