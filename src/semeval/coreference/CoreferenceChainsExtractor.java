package semeval.coreference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import salsa.corpora.elements.Corpus;
import salsa.corpora.elements.Frame;
import salsa.corpora.processing.CorpusProcessor;

/**
 * Extracts all <code>CoreferenceChain</code> elements out of a SalsaXML
 * semeval corpus.
 * 
 * @author The SALSA Project team
 * 
 */
public class CoreferenceChainsExtractor {

	/**
	 * Contains the <code>Corpus</code> to be processed.
	 */
	private Corpus corpus;

	private CorpusProcessor corpusProcessor;

	/**
	 * Default constructor that takes the <code>Corpus</code> to be processed
	 * as an argument.
	 * 
	 * @param corpus
	 */
	public CoreferenceChainsExtractor(Corpus corpus) {

		super();

		this.corpus = corpus;

		corpusProcessor = new CorpusProcessor(corpus);

	}

	/**
	 * This method extracts the coreference chains out of a given
	 * <code>Corpus</code>.
	 * 
	 * @return coreferenceChains
	 */
	public Set<CoreferenceChain> extractCoreferenceChains() {

		System.out
				.println("method CoreferenceChainsExtractor.extractCoreferenceChains() has been called");

		// extract all SingleCoreferenceAnnotation elements out of the corpus
		Set<SingleCoreferenceAnnotation> singleAnnotations = extractSingleAnnotations();

		System.out.println("number of single annotations: "
				+ singleAnnotations.size());

		// create the CoreferenceChain elements out of the list of
		// SingleCoreferenceAnnotation elements.
		Set<CoreferenceChain> coreferenceChains = createCoreferenceChains(singleAnnotations);

		return coreferenceChains;
	}

	/**
	 * Extracts each SingleCorefernceAnnotation separately.
	 * 
	 * @return
	 */
	private Set<SingleCoreferenceAnnotation> extractSingleAnnotations() {

		System.out
				.println("method CoreferenceChainsExtractor.extractSingleAnnotations() has been called");

		Set<SingleCoreferenceAnnotation> allSingleAnnotations = new HashSet<SingleCoreferenceAnnotation>();

		// nimm alle Frames im Corpus auf
		ArrayList<Frame> allFrames = corpusProcessor.getAllAnnotatedFrames();

		// wandele alle Frames in SingleCoreferenceAnnotation um.
		for (Frame frame : allFrames) {
			if (frame.getName().equalsIgnoreCase("Coreference")) {
				allSingleAnnotations
						.add(new SingleCoreferenceAnnotation(frame));
			}
		}

		return allSingleAnnotations;
	}

	/**
	 * Macht aus einzelnen <code>SingleCoreferenceAnnotation</code>-Elementen
	 * ganze zusammengehoerende <code>CoreferenceChain</code>-Elemente.
	 * 
	 * @param singleAnnotations
	 * @return coreferenceChains
	 */
	private Set<CoreferenceChain> createCoreferenceChains(
			Set<SingleCoreferenceAnnotation> singleAnnotations) {

		System.out
				.println("method CoreferenceChainsExtractor.createCoreferenceChains() has been called");

		Set<CoreferenceChain> coreferenceChains = new HashSet<CoreferenceChain>();

		int counter = 0;

		// iteriere ueber alle SingleCoreferenceAnnotation Elemente.
		for (SingleCoreferenceAnnotation currentAnnotation : singleAnnotations) {

			CoreferenceChain currentCoreferenceChain = null;

			counter++;

			System.out.print("integrate chain " + counter + ": "
					+ currentAnnotation.getId().getId() + "...");

			// gehe durch alle bisherigen CoreferenceChain-Elemente und adde die
			// currentAnnotation falls sie reinpasst.
			// sollte es nirgendwo reinpassen, erstelle eine neue
			// CoreferenceChain.

			boolean hasMatched = false;

			for (CoreferenceChain currentChain : coreferenceChains) {

				if (currentChain.addCoreferenceAnnotation(currentAnnotation)) {
					hasMatched = true;
					currentCoreferenceChain = currentChain;
					break;
				}
			}

			if (!hasMatched) {
				System.err.println("NEW CHAIN");
				CoreferenceChain newChain = new CoreferenceChain(corpus);
				newChain.addCoreferenceAnnotation(currentAnnotation);
				coreferenceChains.add(newChain);
				currentCoreferenceChain = newChain;
			}

			/*
			 * Ueberpruefe, ob diejenige CoreferenceChain, in der
			 * currentAnnotation eingefuegt wurde (-> currentCoreferenceChain),
			 * mit einer anderen CoreferenceChain mergen kann. Falls ja, merge
			 * sie mit dieser anderen CoreferenceChain. Falls nein, tue nichts.
			 */

			HashSet<CoreferenceChain> chainsToBeRemoved = new HashSet<CoreferenceChain>();
			HashSet<CoreferenceChain> chainsToBeAdded = new HashSet<CoreferenceChain>();

			System.out.print("\tmerge coreference chains...");

			boolean hasMerged = false;

			for (CoreferenceChain someChain : coreferenceChains) {

				if (someChain != currentCoreferenceChain) {

					if (canBeMerged(someChain, currentCoreferenceChain)) {

						hasMerged = true;

						/*
						 * die beiden zu mergenden CoreferenceChains werden 
						 * geloescht, aber dafuer entsteht eine neue, gemergte 
						 * CoreferenceChain.
						 */
						chainsToBeRemoved.add(someChain);
						chainsToBeRemoved.add(currentCoreferenceChain);

						chainsToBeAdded.add(mergeCoreferenceChains(someChain,
								currentCoreferenceChain));
						break;
					}
				}
			}

			System.out.print("merged: " + hasMerged + "...");

			for (CoreferenceChain chain : chainsToBeRemoved) {

				coreferenceChains.remove(chain);
			}

			for (CoreferenceChain chain : chainsToBeAdded) {
				coreferenceChains.add(chain);
			}

			System.out.println("ok");

		}

		return coreferenceChains;

	}

	/**
	 * toString() method that prints each <code>CoreferenceChain</code> into a
	 * separate line.
	 */
	public String toString() {

		System.out
				.println("method CoreferenceChainsExtractor.toString() has been called");
		StringBuilder buffer = new StringBuilder();

		for (CoreferenceChain currentChain : extractCoreferenceChains()) {
			buffer.append(currentChain.toString());
		}

		return buffer.toString();
	}

	/**
	 * Returns <code>true</code> if some element of <code>firstChain</code> 
	 * matches to some element of <code>secondChain</code>.
	 * 
	 * @param firstChain
	 * @param secondChain
	 * @return
	 */
	private boolean canBeMerged(CoreferenceChain firstChain,
			CoreferenceChain secondChain) {

		for (SingleCoreferenceAnnotation sca1 : firstChain.getSingleCoreferences()){
			for (SingleCoreferenceAnnotation sca2 : secondChain.getSingleCoreferences()){
				if(firstChain.annotationsMatch(sca1, sca2))
					return true;
			}
		}

		return false;
	}

	
	/**
	 * Creates a new <code>CoreferenceChain</code> that contains the content of 
	 * both <code>firstChain</code> and <code>secondChain</code>.
	 * 
	 * @param firstChain
	 * @param secondChain
	 * @return
	 */
	private CoreferenceChain mergeCoreferenceChains(
			CoreferenceChain firstChain, CoreferenceChain secondChain) {

		CoreferenceChain newChain = new CoreferenceChain(corpus);

		for (SingleCoreferenceAnnotation annotation : firstChain
				.getSingleCoreferences()) {
			newChain.addCoreferenceAnnotationWithoutChecking(annotation);
		}

		for (SingleCoreferenceAnnotation secondAnnotation : secondChain
				.getSingleCoreferences()) {

			newChain.addCoreferenceAnnotationWithoutChecking(secondAnnotation);
		}

		return newChain;
	}
}
