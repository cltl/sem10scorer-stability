package semeval.idrepair;

import salsa.corpora.elements.Corpus;
import salsa.corpora.xmlparser.CorpusParser;
import salsa.util.MyFileWriter;

public class IdRepairerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		IdRepairerTest test = new IdRepairerTest();

		test.run();

	}

	private void run() {

		try {

			CorpusParser parser = new CorpusParser();

			Corpus corpus = parser
					.parseCorpusFromFile("files\\salsa\\eins_mdraeger.xml");

			IdRepairer repairer = new IdRepairer(corpus);
			
			repairer.repair();
			
			MyFileWriter filewriter = new MyFileWriter("deleteme.xml");
			
			filewriter.writeToFile(corpus.toString());
			
			System.out.println(repairer.getMappingOverview());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
