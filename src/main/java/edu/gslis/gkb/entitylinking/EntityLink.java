package edu.gslis.gkb.entitylinking;

import java.util.ArrayList;
import java.util.List;

public class EntityLink implements Comparable<EntityLink>
{
	private String name; // the in-line text (i.e. the mention)
	private int tokenBegin = 0;
	private int tokenEnd   = 0;
	private List<Candidate> candidates;
	
	public EntityLink() 
	{
		candidates = new ArrayList<Candidate>();
	}
	public void addEntry(Candidate entry)
	{
		candidates.add(entry);
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public int getTokenBegin()
	{
		return tokenBegin;
	}
	public void setTokenBegin(int tokenBegin)
	{
		this.tokenBegin = tokenBegin;
	}
	public int getTokenEnd()
	{
		return tokenEnd;
	}
	public void setTokenEnd(int tokenEnd)
	{
		this.tokenEnd = tokenEnd;
	}
	public List<Candidate> getCandidates()
	{
		return candidates;
	}
	public void setCandidates(List<Candidate> candidates)
	{
		this.candidates = candidates;
	}
	public int compareTo(EntityLink o)
	{
		if(this.getTokenBegin() > o.getTokenBegin())
			return 1;
		return 0;
	}
	
	
}
