package semeval.coreference;

import salsa.corpora.elements.Corpus;
import salsa.corpora.xmlparser.CorpusParser;
import salsa.util.MyFileWriter;

/**
 * Test class for <code>CoreferenceChainsExtractor</code>. It reads in a corpus, 
 * creates the <code>CoreferenceChain</code>s and the writes their representation
 * to a file.
 * @author The SALSA Project team.
 *
 */
public class CoreferenceChainsExtractorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		CoreferenceChainsExtractorTest test = new CoreferenceChainsExtractorTest();
		
		test.run();

	}
	
	
	private void run() {
		
		try {
		
		CorpusParser parser = new CorpusParser();
		
		Corpus corpus = parser.parseCorpusFromFile("files/anno.xml");
		CoreferenceChainsExtractor extractor = new CoreferenceChainsExtractor(corpus);
		
		MyFileWriter filewriter = new MyFileWriter("files/DELETEME.txt");
		
		filewriter.writeToFile(extractor.toString());
	
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	}

}
