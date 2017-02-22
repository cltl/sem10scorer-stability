package semeval.headMerge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import salsa.corpora.elements.Corpus;
import salsa.corpora.elements.Nonterminal;
import salsa.corpora.elements.Sentence;
import salsa.corpora.elements.Terminal;
import salsa.corpora.xmlparser.CorpusParser;

/**
 * this class merges into a given xml-file without head annotation the head-
 * information of another xml-file
 * 
 * @author Philip John Gorinski
 * 
 */
public class HeadMerger {
	private static String annoFile = null;
	private static String headFile = null;
	private static String destination = null;

	private static class Merger {

		/**
		 * this gets the number of the first sentence of the headCorpus that
		 * matches the given sentence
		 * 
		 * @param headCorpus
		 *            a <code>Corpus</code> containing syntactic information
		 * @param aSentence
		 *            <code>Sentence</code> to be found in the headCorpus
		 * @return the headCorpus' sentence's number or -1 if there is no
		 *         matching sentence
		 */
		private int getFirstSentence(Corpus headCorpus, Sentence aSentence) {
			ArrayList<String> sentenceTerminals = new ArrayList<String>();
			ArrayList<String> mapTerminals = new ArrayList<String>();

			for (Terminal t : aSentence.getGraph().getTerminals()
					.getTerminals()) {
				sentenceTerminals.add(t.getWord());
			}

			for (Sentence s : headCorpus.getBody().getSentences()) {
				mapTerminals.clear();
				for (Terminal t : s.getGraph().getTerminals().getTerminals()) {
					mapTerminals.add(t.getWord());
				}
				if (mapTerminals.equals(sentenceTerminals)){
					String[] id_array = s.getId().getId().split("_");
					return Integer.parseInt(id_array[id_array.length - 1]);
				}
			}
			return -1;
		}

		/**
		 * this copies head-information of a given headCorpus into a corpus that
		 * does not contain that information
		 * 
		 * @param headCorpus
		 *            a <code>Corpus</code> that contains head-information
		 * @param annoCorpus
		 *            a Corpus that does not contain head-information
		 * @return true if merging was successful
		 */
		private boolean merge(Corpus headCorpus, Corpus annoCorpus) {
			int firstSentence = this.getFirstSentence(headCorpus, annoCorpus
					.getBody().getSentences().get(0));

			if (firstSentence == -1) {
				System.err
						.println("Could not find a matching sentence, exiting.");
				System.exit(-2);
			}

			ArrayList<Sentence> headSentences = headCorpus.getBody()
					.getSentences();
			ArrayList<Sentence> annoSentences = annoCorpus.getBody()
					.getSentences();

			int next = 0;
			while (next < annoSentences.size()) {
				Sentence annoSent = annoSentences.get(next);
				Sentence headSent = headSentences.get(next + firstSentence);
				for (Nonterminal annoNt : annoSent.getGraph().getNonterminals()
						.getNonterminals()) {
					String[] annoNtID_array = annoNt.getId().getId().split("_");
					String annoNtID = annoNtID_array[annoNtID_array.length - 1];
					for (Nonterminal headNt : headSent.getGraph()
							.getNonterminals().getNonterminals()) {
						String[] headNtID_array = headNt.getId().getId().split("_");
						String headNtID = headNtID_array[headNtID_array.length - 1];
						if (annoNtID.equals(headNtID)) {
							annoNt.setHead(headNt.getHead());
							annoNt.getId().setHead(headNt.getHead());
						}
					}
				}
				next++;
			}
			for (Sentence s : annoCorpus.getBody().getSentences()) {
				for (Terminal t : s.getGraph().getTerminals().getTerminals()) {
					t.getId().setHead(t.getWord());
					t.getId().setIsTerminal(true);
				}
			}
			return true;
		}

		/**
		 * writes a Corpus to disk
		 * 
		 * @param aCorpus
		 *            a <code>Corpus</code> that is to be written to disk
		 * @param destination
		 *            a <code>String</code> giving the destination for the
		 *            output file
		 * @return true if writing was successful, false otherwise
		 */
		private boolean writeToFile(Corpus aCorpus, String destination) {
			File output = new File(destination);
			try {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(output)));
				out.write(aCorpus.toString());
				out.flush();
				out.close();
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		private boolean merge(String annoFile, String headFile,
				String destination) {
			try {
				CorpusParser parser = new CorpusParser();
				Corpus annoCorpus = parser.parseCorpusFromFile(annoFile);
				Corpus headCorpus = parser.parseCorpusFromFile(headFile);
				if (this.merge(headCorpus, annoCorpus)) {
					return this.writeToFile(annoCorpus, destination);
				}
				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * Needed just so one can use the merge()-Method
	 */
	public HeadMerger() {
	}

	/**
	 * merge the head-information stored in on <code>Corpus</code> into another
	 * Corpus
	 * 
	 * @param annoCorpus
	 *            a Corpus without head-information
	 * @param headCorpus
	 *            a Corpus containing head-information
	 */
	public void merge(Corpus annoCorpus, Corpus headCorpus) {
		System.out.println("Merging without saving to disk!");
		Merger merger = new Merger();
		merger.merge(headCorpus, annoCorpus);
	}

	/**
	 * this parses the commandline arguments. Order is not important.
	 * 
	 * @param args
	 *            the commandline arguments
	 * @return true if the arguments were correct, false otherwise
	 */
	private static boolean parseCommandLine(String[] args) {
		if (args.length != 3)
			return false;
		for (String arg : args) {
			String[] argVal = arg.split("=");
			if (argVal[0].equalsIgnoreCase("annoFile"))
				annoFile = argVal[1];
			if (argVal[0].equalsIgnoreCase("headFile"))
				headFile = argVal[1];
			if (argVal[0].equalsIgnoreCase("destination"))
				destination = argVal[1];
		}
		if (annoFile == null || headFile == null || destination == null)
			return false;
		return true;
	}

	/**
	 * merge head-information into a corpus from the command line
	 * 
	 * @param args
	 *            "annoFile=FILE1": the annotated File to be merged with
	 *            head-information "headFile=FILE2": the file containing the
	 *            head-information "destination=DEST": the location on the disk
	 *            where to save the merged file to
	 */
	public static void main(String[] args) {
		if (parseCommandLine(args)) {
			System.out.println("Trying to merge your specified files...");
			Merger merger = new Merger();
			if (merger.merge(annoFile, headFile, destination)) {
				System.out.println("Merging and writing successful!");
			} else {
				System.out.println("Merging and writing not successful!");
			}
		} else {
			System.err
					.println("Please use valid arguments!\nThose are 'annoFile=FILE1', 'headFile=FILE1' and 'destination=DEST'\nAll of those must be given!");
			System.exit(-1);
		}
	}
}