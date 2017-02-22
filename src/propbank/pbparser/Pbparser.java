package propbank.pbparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import salsa.corpora.elements.*;
import salsa.corpora.noelement.Id;
import salsa.corpora.processing.CorpusProcessor;

/**
 * a parser to fit files in Propbank Format into our data-structures
 * 
 * @author philipg
 * 
 */
public class Pbparser {
	// we're building a Corpus
	private Corpus corpus;
	private Head head;
	private Body body;
	private Meta meta;
	private Author author;
	private CorpusId cid;
	private Date date;
	private Description desc;
	private Format format;
	private History hist;
	private Name name;

	// used to increment the IDs of Frames
	private int frameCount;
	// coref-frames occur only in the gold standard, so we count coref IDs
	// separately
	private int corefCount;

	private CorpusProcessor corpusProcessor;

	// map strings of sentence-IDs to actual sentences
	private HashMap<String, Sentence> sentenceMap;
	// map strings of frame-IDs to actual frames
	private HashMap<String, Frame> frameMap;

	/**
	 * parses a <code>Corpus</code> from a given file
	 * 
	 * @param aFile
	 *            the path to a txt-file in Propbank Format
	 * @return a Corpus that was parsed from the given file
	 * @throws IOException
	 */
	public Corpus parseCorpus(String aFile) throws IOException {
		System.out.println("Parsing PB-Corpus from " + aFile);

		// parsing a new Corpus
		corpus = new Corpus(aFile, "None");

		// needed to make the Corpus a valid FrameNet Corpus (those elements are
		// not needed in fact, but having them may avoid errors later on)
		author = new Author();
		author.setText("Automatically created from " + aFile);
		cid = new CorpusId();
		cid.setId(aFile);
		date = new Date();
		date.setText(new SimpleDateFormat("dd/MM/yyyy")
				.format(new java.util.Date()));
		desc = new Description();
		desc.setText("Autmatic conversion from PB to Negra Format");
		format = new Format();
		format.setFormat("Negra Format, Version 4");
		hist = new History();
		name = new Name();
		name.setText("Version 1");
		meta = new Meta();
		meta.setAuthor(author);
		meta.setCorpus_id(cid);
		meta.setDate(date);
		meta.setDescription(desc);
		meta.setFormat(format);
		meta.setHistory(hist);
		meta.setName(name);
		head = new Head();
		head.setMeta(meta);
		head.setFrames(new Frames());
		head.setWordtags(new Wordtags());
		corpus.setHead(head);

		body = new Body();
		corpus.setBody(body);

		// initialize everything
		corpusProcessor = new CorpusProcessor(corpus);
		sentenceMap = new HashMap<String, Sentence>();
		frameMap = new HashMap<String, Frame>();
		
		/* not used any more, kept them in however, as it might turn out we need them in the future
		frameCount = 0;
		coref IDs start at 500 to clearly separate them from normal frames
		corefCount = 500;
		*/

		// read the file
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				aFile)));

		System.out.println("Parsing Sentences");
		// multiple lines for a single sentence, seperated by tabs
		ArrayList<String[]> lines = new ArrayList<String[]>();
		while (reader.ready()) {
			String curLine = reader.readLine();
			// newlines seperate sentences
			if (curLine.isEmpty()) {
				if (!lines.isEmpty())
					// a new complete sentence is in lines, add it to the corpus
					this.addToCorpus(lines);
				lines.clear();
			} else
				// split the current line and add the array to lines
				lines.add(curLine.split("\t"));
		}
		reader.close();

		// read the file a second time, this time collecting the frames
		reader = new BufferedReader(new FileReader(new File(aFile)));
		// only needed for outpus
		int foo = 0;
		System.out.println("Parsing Frames");
		while (reader.ready()) {
			String curLine = reader.readLine();
			if (!curLine.isEmpty()) {
				// a new line possibly containing frames is found, clear the
				// frame map and generate frames from that line
				frameMap.clear();
				if (!(Integer.parseInt(curLine.split("\t")[0]) == foo)) {
					foo = Integer.parseInt(curLine.split("\t")[0]);
					if (foo % 30 == 0)
						System.out.println();
					System.out.print("s" + foo + ", ");
				}
				// frames can be found in rows 8 and 9
				this.generateFrames(curLine.split("\t"), 7);
				this.generateFrames(curLine.split("\t"), 8);
			}
			/* else {
				frameCount = 0;
				corefCount = 500;
			}
			*/
		}
		System.out.println();
		reader.close();
		return corpus;
	}

	/**
	 * extract the frames of a given line
	 * 
	 * @param line
	 *            the line possibly containing some frames
	 * @param position
	 *            the row of the line that is investigated (either 7 or 8)
	 */
	private void generateFrames(String[] line, int position) {
		// true if the row contains at least one frame
		if (!line[position].equals("_")) {
			// get several IDs
			String sentenceID = "s" + line[0];
			String terminalID = sentenceID + "_" + line[1];

			// ";" separates frames as well as frame elements, so "|" is added
			// as a unique identifier for frames
			String frameline = line[position].replace("};", "}|");

			// get the frames already generated for the current sentence
			Frames curFrames = sentenceMap.get(sentenceID).getSem().getFrames()
					.get(0);

			// iterate over all the frames of the current line
			for (String frameString : frameline.split("\\|")) {
				// get the name of the frame
				String frameName = frameString.split("\\{")[0];
				// content here means the set of IDs of Terminals the frame
				// contains
				String frameContent = null;

				// a frame may only be evoked, but not contain any Terminals
				if (frameString.split("\\{")[1].split("\\}").length > 0)
					frameContent = frameString.split("\\{")[1].split("\\}")[0];

				// see if we are adding new FrameElements to a Frame we have
				// already found
				// if not, create a new Frame
				if (!frameMap.containsKey(frameName)) {
					// coref-Frames and normal Frames are assigned different IDs
					if (!frameName.split("\\.")[0].equalsIgnoreCase("coref")) {
						frameMap.put(frameName, new Frame(frameName
								.split("\\.")[0], new Id(sentenceID + "_"
								+ frameName)));
						// frameCount++;
					} else {
						frameMap.put(frameName, new Frame(frameName
								.split("\\.")[0], new Id(sentenceID + "_"
								+ frameName)));
						// corefCount++;
					}
					// the target of the frame is the Terminal that has evoked
					// it
					Target target = new Target();
					target.addFenode(new Fenode(new Id(terminalID)));
					frameMap.get(frameName).setTarget(target);
					// add the new frame to the list of frames of the current
					// sentence
					curFrames.addFrame(frameMap.get(frameName));
				}

				// get the Frame mapped to the frameName
				Frame frame = frameMap.get(frameName);

				// add FrameElements to the Frame
				if (frameContent != null) {
					// FEs are separated by ";"
					for (String feString : frameContent.split(";")) {
						// get the name and the ID of the FrameElement
						String feName = feString.split("_")[0];
						// String feID = feName.split("A")[1];

						// coref-Elements get a distinct name (current or
						// coreferent)
						if (frame.getName().equalsIgnoreCase("coref")) {
							if (feName.equals("A0"))
								feName = "Current";
							if (feName.equals("A1"))
								feName = "Coreferent";
						}

						// the FrameElement currently investigated may only
						// extend an FE already created earlier
						boolean found = false;
						for (FrameElement fe : frame.getFes()) {
							if (found)
								break;
							// try to get a corresponding FE
							if (fe.getName().equalsIgnoreCase(feName)) {
								found = true;
								String ntID = null;
								// get the terminals spanned by the found FE
								ArrayList<String> terminals = new ArrayList<String>();
								for (String terminal : feString.split("\\(")[1]
										.split("\\)")[0].split(","))
									terminals.add(terminal);
								// get the Nonterminal spanning all these
								// terminals (if any)
								ntID = this.getMaxNT(terminals);

								// either add the Nonterminal to the FE or all
								// the terminals, if no Nonterminal could be
								// found
								if (ntID != null) {
									fe.addFenode(new Fenode(new Id(ntID)));
								} else {
									for (String terminal : terminals) {
										fe.addFenode(new Fenode(
												new Id(terminal)));
									}
								}
							}
						}
						// if we found a new FrameElement
						if (!found) {
							// create a new FE
							FrameElement ele = new FrameElement(new Id(frame
									.getId().getId()
									+ "_" + feName), feName);

							// set some Flags
							String interpretation = feString.split("_")[1]
									.split("=")[0];
							if (interpretation.equals("DNI"))
								ele
										.addFlag(new Flag(
												"Definite_Interpretation"));
							if (interpretation.equals("INI"))
								ele.addFlag(new Flag(
										"Indefinite_Interpretation"));
							if (position == 8) {
								ele
										.addFlag(new Flag(
												"Constructional_licensor"));
							}

							// add the new FE to the current Frame
							frame.addFe(ele);

							// same procedure as above, get Terminals,
							// corresponding NT and add them/it to the FE
							String ntID = null;
							ArrayList<String> terminals = new ArrayList<String>();
							for (String terminal : feString.split("\\(")[1]
									.split("\\)")[0].split(","))
								terminals.add(terminal);
							ntID = this.getMaxNT(terminals);
							if (ntID != null) {
								ele.addFenode(new Fenode(new Id(ntID)));
							} else {
								for (String terminal : terminals) {
									ele.addFenode(new Fenode(new Id(terminal)));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * adds the <code>Terminal</code>s and <code>Nonterminal</code>s of a given
	 * sentence to the corpus
	 * 
	 * @param lines
	 *            a String-Array representing a sentence in Propbank Format
	 */
	private void addToCorpus(ArrayList<String[]> lines) {
		// generate a new sentence, its ID can be found in the first row
		Sentence curSentence = new Sentence(new Id("s" + lines.get(0)[0]));
		// each sentence has a Graph, Terminals, Nonterminals, Semantics and
		// Frames
		Graph curGraph = new Graph(null);
		Terminals curTerminals = new Terminals();
		Nonterminals curNonterminals = new Nonterminals();
		Semantics curSem = new Semantics();
		Frames curFrames = new Frames();

		curSem.addFrames(curFrames);
		curGraph.setNonterminals(curNonterminals);
		curGraph.setTerminals(curTerminals);
		curSentence.setGraph(curGraph);
		curSentence.setSem(curSem);

		// a sentence belongs to a corpus' body
		body.addSentence(curSentence);

		// the sentence ID is needed several times
		String sentenceID = curSentence.getId().getId();
		// map the current sentence to its ID
		sentenceMap.put(sentenceID, curSentence);
		// Nonterminals start at count 500, to easily distinguish them from
		// Terminals (which start at 1)
		int ntCounter = 500;

		// stack for building Nonterminals
		ArrayList<Nonterminal> ntStack = new ArrayList<Nonterminal>();

		// iterate over the Terminals of the current sentence
		for (String[] line : lines) {
			// the Terminal's ID can be found in row 2
			String terminalID = sentenceID + "_" + line[1];

			// generate a new Terminal and add it to the sentence's terminals
			curTerminals.addTerminal(new Terminal(new Id(terminalID, true),
					line[3], null, line[4], line[2]));

			// row 7 of each line defines the syntax of the sentence
			String syntax = line[6];

			// parsing the sentence's syntax. Each Terminal either opens,
			// belongs to or closes a Nonterminal
			// "(" opens a new Nonterminal
			if (syntax.contains("(")) {
				// this is for sentences that have not been parsed correctly and
				// only consist of (NONE) as structure
				if (syntax.equals("(NONE")) {
					Nonterminal newNT = new Nonterminal("NONE", "--", new Id(
							sentenceID + "_" + ntCounter));
					ntStack.add(newNT);
					curNonterminals.addNonterminal(newNT);
					ntCounter++;
					// this is for sentences with a complete syntactic structure
				} else {
					for (String nt : syntax.split("\\(")) {
						if (!nt.isEmpty()) {
							// a Nonterminal has a category and a head
							String cat = nt.split(":")[0];
							String head;
							// either the current Terminal is the head
							// (catch-case) or it is defined right behind the
							// category
							try {
								head = lines.get(Integer
										.parseInt(nt.split(":")[1]) - 1)[2];
							} catch (ArrayIndexOutOfBoundsException e) {
								head = line[2];
							}
							// create and add a new Nonterminal to the stack,
							// increment the ntCounter
							Nonterminal newNT = new Nonterminal(cat, head,
									new Id(sentenceID + "_" + ntCounter, head, false));
							ntStack.add(newNT);
							curNonterminals.addNonterminal(newNT);
							ntCounter++;
						}
					}
				}
				// the Nonterminal created contains an edge to the current
				// Terminal
				ntStack.get(ntStack.size() - 1).addEdge(
						new Edge(new Id(terminalID), "-"));
			}
			// a "*" signals a Terminal belonging to the current Nonterminal
			if (syntax.contains("*")) {
				ntStack.get(ntStack.size() - 1).addEdge(
						new Edge(new Id(terminalID), "-"));
			}
			// closing Nonterminals
			while (syntax.contains(")")) {
				// add hierarchically dominated Nonterminals to the Nonterminals
				// dominating them
				if (ntStack.size() >= 2)
					ntStack.get(ntStack.size() - 2).addEdge(
							new Edge(new Id(ntStack.get(ntStack.size() - 1)
									.getId().getId()), "-"));
				else
					curGraph.setRoot(new Id(ntStack.get(0).getId().getId()));
				ntStack.remove(ntStack.size() - 1);
				syntax = syntax.replaceFirst("\\)", "");
			}
		}
	}

	/**
	 * returns the ID of the Nonterminal that spans a given list of Terminals
	 * (if any)
	 * 
	 * @param terminals
	 *            an ArrayList of Strings, the terminals to be spanned by the
	 *            Nonterminal
	 * @return a String, the ID of the Nonterminal spanning all the given
	 *         Terminals, or null if there is no such Nonterminal
	 */
	private String getMaxNT(ArrayList<String> terminals) {
		// only Nonterminals of the sentence containing the terminals can span
		// the terminals
		Sentence sentence = sentenceMap.get(terminals.get(0).split("_")[0]);

		// for each Nonterminal, get the Terminals it spans and compare to the
		// given ArrayList
		for (Nonterminal nt : sentence.getGraph().getNonterminals()
				.getNonterminals()) {
			if (corpusProcessor.getAllTerminals(sentence, nt).equals(terminals)) {
				// return the Nonterminal's ID if the lists match
				return nt.getId().getId();
			}
		}
		return null;
	}
}
