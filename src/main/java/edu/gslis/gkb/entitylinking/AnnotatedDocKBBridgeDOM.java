package edu.gslis.gkb.entitylinking;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.gslis.gkb.xml.DOMUtil;
import edu.gslis.textrepresentation.FeatureVector;
import edu.gslis.utils.Stopper;

public class AnnotatedDocKBBridgeDOM extends AnnotatedDocument
{
	
	@Override
	public void read(String pathToFile)
	{
		try {
			File file = new File(pathToFile);
			if(!file.exists()) {
				System.err.println("can't find serialized file: " + pathToFile);
				return;
			}
			
			DOMParser parser = new DOMParser();
			parser.parse(pathToFile);
			Document doc = parser.getDocument();

			// Get the document's root XML node
			NodeList root = doc.getChildNodes();

			Node rootNode   = DOMUtil.getNode("root", root);
			Node docNode    = DOMUtil.getNode("document", rootNode.getChildNodes());
			Node nameNode   = DOMUtil.getNode("name", docNode.getChildNodes());
			docno           = DOMUtil.getNodeValue(nameNode);

			Node tokensNode = DOMUtil.getNode("tokens", docNode.getChildNodes());
			NodeList tokenList = tokensNode.getChildNodes();
			List<Node> toks  = DOMUtil.asList(tokenList);

			tokens = new HashMap<Integer,String>();
			for(Node tokenNode : toks) {
				if(tokenNode.hasAttributes()) {
					int tokNumber = Integer.parseInt(DOMUtil.getNodeAttr("id", tokenNode)) - 1;

					Node wordNode = DOMUtil.getNode("word", tokenNode.getChildNodes());
					String word   = DOMUtil.getNodeValue(wordNode);

					if(groomText) {
						Matcher matcher = NON_TEXT_PATTERN.matcher(word);
						word = matcher.replaceAll("").toLowerCase().trim();
					}

					tokens.put(tokNumber, word);
				}
			}


			Node mentionsNode      = DOMUtil.getNode("mentions", docNode.getChildNodes());
			List<Node> mentionList = DOMUtil.asList(mentionsNode.getChildNodes());
			mentions               = new ArrayList<Mention>();
			for(Node mentionNode : mentionList) {
				if(mentionNode.getLocalName() != null && mentionNode.getNodeName().equals("mention")) {
					Node stringNode = DOMUtil.getNode("string", mentionNode.getChildNodes());
					String mentionString = DOMUtil.getNodeValue(stringNode);
					
					Node typeNode     = DOMUtil.getNode("type", mentionNode.getChildNodes());
					String typeString = DOMUtil.getNodeValue(typeNode);
					
					Node tokenNode    = DOMUtil.getNode("TokenBegin", mentionNode.getChildNodes());
					int tokenBegin    = Integer.parseInt(DOMUtil.getNodeValue(tokenNode));
					
					tokenNode         = DOMUtil.getNode("TokenEnd", mentionNode.getChildNodes());
					int tokenEnd      = Integer.parseInt(DOMUtil.getNodeValue(tokenNode));
					
					
					if(groomText) {
						Matcher matcher = NON_TEXT_PATTERN.matcher(mentionString);
						mentionString = matcher.replaceAll("").toLowerCase().trim();
					}
					
					Mention mentionObj = new Mention();
					mentionObj.setMentionText(mentionString);
					mentionObj.setType(typeString);
					mentionObj.setTokenStart(tokenBegin);
					mentionObj.setTokenEnd(tokenEnd);
					
					mentions.add(mentionObj);
				}
			}
			
			Node kbLinksNode       = DOMUtil.getNode("kblinks", docNode.getChildNodes());
			List<Node> kbLinkList  = DOMUtil.asList(kbLinksNode.getChildNodes());
			links                  = new ArrayList<EntityLink>();
			for(Node kbLinkNode : kbLinkList) {
				if(kbLinkNode.getNodeName() != null && kbLinkNode.getNodeName().equals("entitylink")) {
					Node linkNameNode    = DOMUtil.getNode("name", kbLinkNode.getChildNodes());
					String linkName      = DOMUtil.getNodeValue(linkNameNode);

					Node tokenNode    = DOMUtil.getNode("TokenBegin", kbLinkNode.getChildNodes());
					int tokenBegin    = Integer.parseInt(DOMUtil.getNodeValue(tokenNode));
					
					tokenNode         = DOMUtil.getNode("TokenEnd", kbLinkNode.getChildNodes());
					int tokenEnd      = Integer.parseInt(DOMUtil.getNodeValue(tokenNode));
					
					EntityLink link = new EntityLink();
					link.setName(linkName);
					link.setTokenBegin(tokenBegin);
					link.setTokenEnd(tokenEnd);
					
					List<Node> temp   = DOMUtil.asList(kbLinkNode.getChildNodes());
					for(Node node : temp) {
						if(node.getNodeName() != null && node.getNodeName().equals("candidate")) {
							Node idNode      = DOMUtil.getNode("id", node.getChildNodes());
							String idString  = DOMUtil.getNodeValue(idNode);
							
							Node scoreNode   = DOMUtil.getNode("score", node.getChildNodes());
							double score     = Double.parseDouble(DOMUtil.getNodeValue(scoreNode));
							
							Candidate entry = new Candidate();
							entry.setID(idString);
							entry.setLinkProbability(score);
							link.addEntry(entry);
						}
							
					}
					links.add(link);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public FeatureVector getDocVector(Stopper stopper)
	{
		FeatureVector docVector = new FeatureVector(stopper);
		List<Integer> positions = new ArrayList<Integer>(tokens.keySet().size());
		positions.addAll(tokens.keySet());
		
		for(Integer position : positions)
			docVector.addTerm(tokens.get(position));
		
		return docVector;
	}

}
