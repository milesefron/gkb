package edu.gslis.gkb.entitylinking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.gslis.textrepresentation.FeatureVector;
import edu.gslis.utils.Stopper;

/**
 * Something's wrong with this class... <b>DO NOT USE.  USE THE DOM VERSION!!</b>
 * @author Miles Efron
 *
 */
public class AnnotatedDocKBBridgeSAX extends AnnotatedDocument
{
	public static final int OFFSET_ATT_ID = 0;

	@Override
	public void read(String pathToFile)
	{
		tokens = new HashMap<Integer,String>();
		mentions = new ArrayList<Mention>();
		links  = new ArrayList<EntityLink>();

		// XML parsing stuff
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				boolean wordFlag = false;

				boolean mentionStringFlag = false;
				boolean mentionTypeFlag = false;

				boolean tokenBeginFlag = false;
				boolean tokenEndFlag = false;
				
				boolean linkIDFlag = false;
				boolean linkScoreFlag = false;
				boolean linkNameFlag = false;
				
				int wordOffset = 0;

				String mentionText;
				String mentionType;
				
				String linkName;
				String linkID;
				double linkScore = 0.0;
				double previousLinkScore = Double.POSITIVE_INFINITY;
				
				EntityLink link = new EntityLink();

				int spanStart = 0;
				int spanEnd = 0;

				// SAX callbacks
				public void startElement(String uri, String localName,String qName, 
						Attributes attributes) throws SAXException {


					if (qName.equalsIgnoreCase("TOKEN")) {
						wordOffset = Integer.parseInt(attributes.getValue(OFFSET_ATT_ID)) - 1; // -1 to reconcile array indexing
					}

					if (qName.equalsIgnoreCase("WORD")) {
						wordFlag = true;
					}

					// mentions
					if (qName.equalsIgnoreCase("STRING")) {
						mentionStringFlag = true;
					}

					if (qName.equalsIgnoreCase("TYPE")) {
						mentionTypeFlag = true;
					}

					if (qName.equalsIgnoreCase("TOKENBEGIN")) {
						tokenBeginFlag = true;
					}

					if (qName.equalsIgnoreCase("TOKENEND")) {
						tokenEndFlag = true;
					}


					// links
					if (qName.equalsIgnoreCase("ENTITYLINK")) {
						link = new EntityLink();
					}

					if (qName.equalsIgnoreCase("NAME")) {
						linkNameFlag = true;
					}
					
					if (qName.equalsIgnoreCase("ID")) {
						linkIDFlag = true;
					}
					
					if (qName.equalsIgnoreCase("SCORE")) {
						linkScoreFlag = true;
					}
					
				}



				public void endElement(String uri, String localName,
						String qName) throws SAXException {

					if(qName.equalsIgnoreCase("MENTION")) {
						Mention mention = new Mention();
						mention.setMentionText(mentionText);
						mention.setTokenStart(spanStart);
						mention.setTokenEnd(spanEnd);
						mention.setType(mentionType);
						mentions.add(mention);
					}
					
					
					if(qName.equalsIgnoreCase("ENTITYLINK")) {
						link.setName(linkName);
						link.setTokenBegin(spanStart);
						link.setTokenEnd(spanEnd);
						links.add(link);
						
						previousLinkScore = Double.POSITIVE_INFINITY;
					}

					if(qName.equalsIgnoreCase("CANDIDATE")) {
						Candidate entry = new Candidate();
						entry.setID(linkID);
						entry.setLinkProbability(linkScore);
						
						if(linkScore > previousLinkScore) {
							System.err.println(linkName);
							System.err.println(linkID);
							System.err.println(linkScore + " :: " + previousLinkScore);
							linkScore = previousLinkScore;
							entry.setLinkProbability(linkScore);
							//System.exit(-1);
						}
						previousLinkScore = linkScore;
						link.addEntry(entry);
					}
				
				}



				public void characters(char ch[], int start, int length) throws SAXException {
					String content = new String(ch, start, length);
					content = content.trim();

					if (wordFlag) {	
						tokens.put(wordOffset, content);
						wordFlag = false;
						return;
					}

					if(mentionStringFlag) {
						mentionText = content;
						mentionStringFlag = false;
					}

					if(mentionTypeFlag) {
						mentionType = content;
						mentionTypeFlag = false;
					}

					if(tokenBeginFlag) {
						try {
							spanStart = Integer.parseInt(content);
						} catch(Exception e) {
							;
						}
						tokenBeginFlag = false;
					}

					if(tokenEndFlag) {
						try {
							spanEnd = Integer.parseInt(content);
						} catch(Exception e) {
							;
						}
						tokenEndFlag = false;
					}

					if(linkNameFlag) {
						linkName = content;
						linkNameFlag = false;
					}
					if(linkIDFlag) {
						linkID = content;
						linkIDFlag = false;
					}
					if(linkScoreFlag) {
						try {
							linkScore = Double.parseDouble(content);
						} catch(Exception e) {
							;
						}
						linkScoreFlag = false;
					}

				}

			};


			// do the xml parsing
			saxParser.parse(pathToFile, handler);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public FeatureVector getDocVector(Stopper stopper)
	{
		// TODO Auto-generated method stub
		return null;
	}
	

}
