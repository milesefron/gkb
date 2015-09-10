package edu.gslis.gkb.entitylinking;

public class Candidate implements Comparable<Candidate>
{
	private String id;
	private double linkProbability;
	
	
	
	public String getID()
	{
		return id;
	}



	public void setID(String id)
	{
		this.id = id;
	}



	public double getLinkProbability()
	{
		return linkProbability;
	}



	public void setLinkProbability(double linkProbability)
	{
		this.linkProbability = linkProbability;
	}



	public int compareTo(Candidate o)
	{
		if(this.getLinkProbability() > o.getLinkProbability())
			return -1;
		return 1;
	}
}
