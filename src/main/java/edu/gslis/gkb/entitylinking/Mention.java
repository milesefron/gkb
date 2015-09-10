package edu.gslis.gkb.entitylinking;

public class Mention implements Comparable<Mention>
{
	private String mentionText;
	private String type;
	private int tokenStart;
	private int tokenEnd;
	
	public String getMentionText()
	{
		return mentionText;
	}
	public void setMentionText(String mentionText)
	{
		this.mentionText = mentionText;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public int getTokenStart()
	{
		return tokenStart;
	}
	public void setTokenStart(int tokenStart)
	{
		this.tokenStart = tokenStart;
	}
	public int getTokenEnd()
	{
		return tokenEnd;
	}
	public void setTokenEnd(int tokenEnd)
	{
		this.tokenEnd = tokenEnd;
	}
	public int compareTo(Mention o)
	{
		if(this.getTokenStart() > o.getTokenStart())
			return 1;
		return 0;
	}
	
	@Override
	public String toString() {
		return(mentionText + "\n" + type + "\t" + tokenStart + "-" + tokenEnd + "\n\n");
	}

	
	
}
