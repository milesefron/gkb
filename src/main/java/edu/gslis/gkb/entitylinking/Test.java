package edu.gslis.gkb.entitylinking;

import java.util.List;
import java.util.Map;



public class Test
{
	public static void main(String[] args) {
		//AnnotatedDocument doc = new AnnotatedDocKBBridgeSAX();
		AnnotatedDocument doc = new AnnotatedDocKBBridgeDOM();
		doc.read("/Users/mefron/devel/workspace/gkb/data/robust-annotations-selected/LA020289-0103.xml");
		
		System.out.println("TOKENS");
		Map<Integer,String> toks = doc.getTokens();
		for(int i=0; i<toks.size(); i++)
			System.out.println(i + "\t\t" + toks.get(i));
		
		System.out.println();
		System.out.println("MENTIONS");
		List<Mention> mentions = doc.getMentions();
		for(Mention mention : mentions)
			System.out.println(mention.getTokenStart() + " " + mention.getTokenEnd() + ": " + mention.getType() + " :: " + mention.getMentionText());
		
		
		List<EntityLink> links = doc.getLinks();
		for(EntityLink link : links) {
			System.out.println(link.getTokenBegin() + "\t" + link.getTokenEnd() + "\t" + link.getName());
			for(Candidate entry : link.getCandidates()) 
				System.out.println("   " + entry.getLinkProbability() + "\t" + entry.getID());
		}
		
	}
}
