package edu.gslis.gkb.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.gslis.gkb.queryrepresentation.FeedbackMethod;
import edu.gslis.gkb.queryrepresentation.RM1;
import edu.gslis.gkb.queryrepresentation.RM1FromKBBDocs;
import edu.gslis.gkb.queryrepresentation.RM1FromKBBDocs.EntityLinker;
import edu.gslis.gkb.utils.Interpolation;
import edu.gslis.indexes.IndexWrapper;
import edu.gslis.indexes.IndexWrapperIndriImpl;
import edu.gslis.output.FormattedOutputTrecEval;
import edu.gslis.queries.GQueries;
import edu.gslis.queries.GQueriesJsonImpl;
import edu.gslis.queries.GQuery;
import edu.gslis.searchhits.SearchHit;
import edu.gslis.searchhits.SearchHits;
import edu.gslis.textrepresentation.FeatureVector;
import edu.gslis.utils.ParameterBroker;
import edu.gslis.utils.Stopper;


/**
 * Mimics the behavior of IndriRunQuery
 * 
 * @author mefron
 *
 */

public class FindMissingDocs {
	public static final String RUN_ID = "rm3";

	public static final String QUERY_PARAM = "queries";
	public static final String INDEX_PARAM = "index";
	public static final String STOPPER_PARAM = "stopper";
	public static final String COUNT_PARAM = "count";
	public static final String FB_DOCS_PARAM = "fbDocs";
	public static final String FB_TERMS_PARAM = "fbTerms";
	public static final String FB_ORIG_WEIGHT_PARAM = "fbOrigWeight";
	public static final String ANNOTATIONS_PARAM = "annotation-dir";

	/**
	 * imitates IndriRunQuery
	 * 
	 * @param args[0] /path/to/json/param/file
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		File paramFile = new File(args[0]);
		if(!paramFile.exists()) {
			System.err.println("you must specify a parameter file to run against.");
			System.exit(-1);
		}
		
		ParameterBroker params = new ParameterBroker(args[0]);

		
		GQueries        queries   = new GQueriesJsonImpl();
					    queries.read(params.getParamValue(QUERY_PARAM));
		IndexWrapper    index     = new IndexWrapperIndriImpl(params.getParamValue(INDEX_PARAM));
		
		
		String          runId     = RUN_ID;

		String      countString   = params.getParamValue(COUNT_PARAM);
		if(countString==null)
			countString="1000";
		int count = Integer.parseInt(countString);
		Stopper stopper = new Stopper(params.getParamValue(STOPPER_PARAM));
		
		int fbDocs = 20;
		if(params.getParamValue(FB_DOCS_PARAM) != null)
			fbDocs = Integer.parseInt(params.getParamValue(FB_DOCS_PARAM));
		
		int fbTerms = 20;
		if(params.getParamValue(FB_TERMS_PARAM) != null)
			fbTerms = Integer.parseInt(params.getParamValue(FB_TERMS_PARAM));
		
		double fbOrigWeight = 0.5;
		if(params.getParamValue(FB_ORIG_WEIGHT_PARAM) != null)
			fbOrigWeight = Double.parseDouble(params.getParamValue(FB_ORIG_WEIGHT_PARAM));
		
		
		
			
		
		Iterator<GQuery> queryIterator = queries.iterator();
		while(queryIterator.hasNext()) {
			GQuery query = queryIterator.next();
			
			System.err.println(query.getTitle());
			
			String qText = stopper.apply(query.getText());
			
			SearchHits results = index.runQuery(qText, fbDocs);
			Iterator<SearchHit> it = results.iterator();
			while(it.hasNext()) {
				String docno = it.next().getDocno();
				String fileName = "./robust04-ent-ann/" + docno + ".xml";
				System.out.println("cp " + fileName + " ./robust-ql-20/" + docno + ".xml");
				
			}
			
			
		}
	}

}
