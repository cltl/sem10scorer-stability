package semeval.idrepair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import salsa.corpora.elements.Corpus;
import salsa.corpora.noelement.Id;
import salsa.corpora.processing.CorpusProcessor;
import salsa.corpora.xmlparser.CorpusParser;
import salsa.util.MyFileWriter;

/**
 * IdRepairer can 'repair' the values of the 'id' and 'idref' attribute in a
 * SalsaXML <code>Corpus</code>, so that the salto tool will be able to merge
 * them.
 * 
 * @author Fabian Shirokov
 * 
 */
public class IdRepairer {

	private Corpus corpus;

	private String mappingOverview;

	private HashMap<String, String> idMapping;

	private String _inputfile;

	private String _outputfile;

	private String _mappingfile;

	static private String newline = System.getProperty("line.separator");

	/**
	 * Default constructor that takes the SalsaXML <code>Corpus</code> as an
	 * argument.
	 * 
	 * @param corpus
	 */
	public IdRepairer(Corpus corpus) {
		super();
		this.corpus = corpus;
		this.mappingOverview = "";
		idMapping = new HashMap<String, String>();
	}

	private IdRepairer(String[] args) throws SAXException,
			ParserConfigurationException, IOException {
		super();
		assignParameters(args);
		this.mappingOverview = "";
		idMapping = new HashMap<String, String>();
		initializeCorpus();
	}

	/**
	 * Main class that repairs a corpus.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			IdRepairer rep = new IdRepairer(args);

			rep.repair();

			MyFileWriter filewriter = new MyFileWriter(rep._outputfile);

			filewriter.writeToFile(rep.corpus.toString());

			MyFileWriter filewriterMapping = new MyFileWriter(rep._mappingfile);

			filewriterMapping.writeToFile(rep.getMappingOverview());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method repairs the 'ids' in a corpus.
	 */
	public void repair() {

		CorpusProcessor processor = new CorpusProcessor(corpus);

		ArrayList<Id> idlist = processor.getAllIds();

		HashMap<String, String> sentenceIdMapping = processor
				.getSentenceIdMapping();

		for (Id id : idlist) {

			if (null != id) {

				// If the id is from a Sentence ('s') element
				if (sentenceIdMapping.containsKey(id.getId())) {

					id.setId(sentenceIdMapping.get(id.getId()));

				}

				// If the id is not from a Sentence ('s') element.
				else {

					for (String oldSentenceId : sentenceIdMapping.keySet()) {
						
						if (id.getId().indexOf(oldSentenceId) == 0) {

							String firstPart = oldSentenceId;

							String secondPart = id.getId().substring(
									firstPart.length());

							// neu
							if (secondPart.startsWith("_")) {

								String newName = sentenceIdMapping
										.get(firstPart)
										+ secondPart;

								idMapping.put(id.getId(), newName);

								id.setId(newName);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Returns an overview of which old Ids have matched to which new Ids. This
	 * method returns an empty String if you don't apply the
	 * <code>repair()</code> method first.
	 * 
	 * @return
	 */
	public String getMappingOverview() {

		StringBuilder buffer = new StringBuilder();

		for (String oldId : idMapping.keySet()) {

			buffer.append(oldId + "\t" + idMapping.get(oldId) + newline);

		}

		return buffer.toString();
	}

	/**
	 * Writes the result of <code>getMappingOverview()</code> into a file with
	 * the specified name.
	 */
	public void writeMappingToFile(String fileName) throws IOException {

		MyFileWriter filewriter = new MyFileWriter(fileName);

		filewriter.writeToFile(getMappingOverview());
	}

	/**
	 * Assigns the input parameters to the variables.
	 * 
	 * @param args
	 */
	private void assignParameters(String[] args) {

		for (int i = 0; i < args.length; i++) {

			if (args[i].equalsIgnoreCase("-in")) {
				i++;
				_inputfile = args[i];
			} else if (args[i].equalsIgnoreCase("-out")) {
				i++;
				_outputfile = args[i];
			} else if (args[i].equalsIgnoreCase("-mapping")) {
				i++;
				_mappingfile = args[i];
			}

		}

		if (null == _outputfile || null == _inputfile) {
			usage();
		}

	}

	/**
	 * Initializes the corpus according to the _inputfile.
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	private void initializeCorpus() throws SAXException,
			ParserConfigurationException, IOException {

		CorpusParser parser = new CorpusParser();

		corpus = parser.parseCorpusFromFile(_inputfile);

	}

	/**
	 * Prints to System.out how to invoke the main method of this program.
	 */
	private void usage() {

		System.out.println("Welcome to the IdRepairer.");
		System.out.println();

		System.out
				.println("IdRepairer can 'repair' the values of the 'id' and "
						+ "'idref' attribute in a SalsaXML <code>Corpus</code>, "
						+ "so that the salto tool will be able to merge them.");

		System.out.println("usage:");
		System.out
				.println("java -jar idrepairer.jar -Xmx512M -in INPUTFILEXML "
						+ "-out OUTPUTFILEXML" + " -mapping MAPPINGOUTPUTFILE");

		System.out.println();

		System.out
				.println("INPUTFILEXML is the existing corpus that you want to "
						+ "be repaired");

		System.out
				.println("OUTPUTFILEXML is the name of the new file that will "
						+ "contain the repaired corpus");

		System.out
				.println("MAPPINGOUTPUTFILE is the name of the new file that "
						+ "will contain the mapping from each old 'id' and 'idref' "
						+ "value to the new ones.");

		System.exit(0);
	}
}
