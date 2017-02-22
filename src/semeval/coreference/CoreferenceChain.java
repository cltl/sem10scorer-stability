package semeval.coreference;

import java.util.ArrayList;
import java.util.Set;

import salsa.corpora.elements.Corpus;
import salsa.corpora.elements.Fenode;
import salsa.corpora.elements.FrameElement;
import salsa.corpora.elements.Nonterminal;
import salsa.corpora.elements.Terminal;
import salsa.corpora.noelement.Id;
import salsa.corpora.processing.CorpusProcessor;

/**
 * Represents a coreference chain in the semeval-SalsaXML corpora.
 * 
 * @author The SALSA Project team
 * 
 */
public class CoreferenceChain {

	private static String newline = System.getProperty("line.separator");

	/**
	 * Contains the <code>CorpusProcessor</code> that can process the given
	 * <code>Corpus</code>.
	 */
	private CorpusProcessor corpusProcessor;
	
	private ArrayList<Nonterminal> allNT;
	private ArrayList<Terminal> allTerminals;

	/**
	 * Contains the list of <code>SingleCoreferenceAnnotation</code> elements
	 * that represents this coreference chain.
	 */
	private ArrayList<SingleCoreferenceAnnotation> singleCoreferences;

	/**
	 * Default constructor that takes the <code>Corpus</code> as an argument.
	 * 
	 * @param corpus
	 */
	public CoreferenceChain(Corpus corpus) {

		corpusProcessor = new CorpusProcessor(corpus);

		allNT = corpusProcessor.getAllNonterminalsInCorpus();
		allTerminals = corpusProcessor.getAllTerminalsInCorpus();

		singleCoreferences = new ArrayList<SingleCoreferenceAnnotation>();

	}

	/**
	 * Returns <code>true</code> if the annotation matches one of the single
	 * coreferences that are already contained in singleCoreferences. Returns
	 * also true if no annotation has been set yet.
	 * 
	 * @param newAnnotation
	 * @return
	 */
	public boolean addCoreferenceAnnotation(
			SingleCoreferenceAnnotation newAnnotation) {

		if (singleCoreferences.isEmpty()) {

			singleCoreferences.add(newAnnotation);

			return true;
		}

		for (SingleCoreferenceAnnotation currentAnnotation : singleCoreferences) {

			if (annotationsMatch(currentAnnotation, newAnnotation)) {

				this.singleCoreferences.add(newAnnotation);

				return true;
			}
		}
		return false;

	}

	/**
	 * Adds 'newAnnotation' to this CoreferenceChain without checking if it
	 * really suits to this chain. Please use this method only if you are sure
	 * that in the end this <code>CoreferenceChain</code> will still be a
	 * connected chain.
	 * 
	 * 
	 * @param newAnnotation
	 */
	public void addCoreferenceAnnotationWithoutChecking(
			SingleCoreferenceAnnotation newAnnotation) {

		if (!singleCoreferences.contains(newAnnotation)) {
			this.singleCoreferences.add(newAnnotation);
		}
	}

	/**
	 * Returns the list of <code>SingleCoreferenceAnnotation</code> elements. 
	 * @return the singleCoreferences
	 */
	public ArrayList<SingleCoreferenceAnnotation> getSingleCoreferences() {
		return singleCoreferences;
	}

	/**
	 * Two references of the 'Coreference' frame match if: (1) The elements
	 * denoted by the 'Coreferent' FE of firstAnnotation is the same as the
	 * element denoted by the 'Current' FE of secondAnnotation or
	 * (2) the Coreferent FE of firstAnnotation is covered by the head of the
	 * Current FE of secondAnnotation or (3) vice versa
	 * 
	 * @param firstAnnotation
	 * @param secondAnnotation
	 * @return true iff the two annotations match in any way
	 */
	public boolean annotationsMatch(
			SingleCoreferenceAnnotation firstAnnotation,
			SingleCoreferenceAnnotation secondAnnotation) {
		
		Object firstCurrent = null;
		ArrayList<Fenode> firstCurrentFeNodes = null;
		Object firstCoreferent = null;
		ArrayList<Fenode> firstCoreferentFeNodes = null;
		Object secondCurrent = null;
		ArrayList<Fenode> secondCurrentFeNodes = null;
		Object secondCoreferent = null;
		ArrayList<Fenode> secondCoreferentFeNodes = null;
		
		for(FrameElement fe : firstAnnotation.getFes()){
			if(fe.getName().equalsIgnoreCase("Current")){
				firstCurrent = this.getCorrespondentObject(fe.getFenodes().get(0));
				firstCurrentFeNodes = fe.getFenodes();
			}
			if(fe.getName().equalsIgnoreCase("Coreferent")){
				firstCoreferent = this.getCorrespondentObject(fe.getFenodes().get(0));
				firstCoreferentFeNodes = fe.getFenodes();
			}
		}
		for(FrameElement fe : secondAnnotation.getFes()){
			if(fe.getName().equalsIgnoreCase("Current")){
				secondCurrent = this.getCorrespondentObject(fe.getFenodes().get(0));
				secondCurrentFeNodes = fe.getFenodes();
			}
			if(fe.getName().equalsIgnoreCase("Coreferent")){
				secondCoreferent = this.getCorrespondentObject(fe.getFenodes().get(0));
				secondCoreferentFeNodes = fe.getFenodes();
			}
		}
		
		// match in die eine Richtung?
		if (null != firstCurrent && null != secondCoreferent)
			if (this.contains(firstCurrent, firstCurrentFeNodes,
					secondCoreferent, secondCoreferentFeNodes) ||
					this.contains(secondCoreferent, secondCoreferentFeNodes,
							firstCurrent, firstCurrentFeNodes))
				return true;

		// match in die andere Richtung?
		if (null != firstCoreferent && null != secondCurrent)
			if (this.contains(firstCoreferent, firstCoreferentFeNodes,
					secondCurrent, secondCurrentFeNodes) ||
					this.contains(secondCurrent, secondCurrentFeNodes,
							firstCoreferent, firstCoreferentFeNodes))
				return true;
		return false;
	}

	/**
	 * returns the Object (<code>Nonterminal</code> or <code>Terminal</code>)
	 * that is refered to by a <code>Fenode</code> in a <code>Corpus</code>
	 * 
	 * @param anFeNode a <code>Fenode</code> found in an annotated <code>Frame
	 * </code>
	 * @return an <code>Object</code>, either a Terminal or a Nonterminal, that
	 * is, in the Corpus, the one refered to by anFenode
	 */
	private Object getCorrespondentObject(Fenode anFeNode){
		for(Nonterminal nt : allNT){
			if (nt.getId().getId().equals(anFeNode.getIdref().getId()))
				return nt;
		}
		for(Terminal t : allTerminals){
			if (t.getId().getId().equals(anFeNode.getIdref().getId()))
				return t;
		}
		return null;
	}
	
	/**
	 * check whether one Object (either a <code>Terminal</code> or a <code>
	 * Nonterminal</code> hierarchically contains another Object
	 * @param first the first Object
	 * @param firstFeNodes used to get all the Terminals spanned by the first
	 * Object
	 * @param second the second Object
	 * @param secondFeNodes used to get all the Terminals spanned by the second
	 * Object
	 * @return true iff the first Object is equal to or contains the second Object
	 */
	private boolean contains(Object first, ArrayList<Fenode> firstFeNodes,
			Object second, ArrayList<Fenode> secondFeNodes){
		if(first instanceof Terminal && second instanceof Terminal){
			return ((Terminal)first).getId().equals(((Terminal)second).getId());
		}
		if(first instanceof Nonterminal && second instanceof Terminal){
			if(corpusProcessor.getAllTerminals(firstFeNodes).contains(((Terminal)second)))
					return ((Nonterminal)first).getHead().equalsIgnoreCase(((Terminal)second).getLemma());
		}
		if(first instanceof Terminal && second instanceof Nonterminal){
			if(corpusProcessor.getAllTerminals(secondFeNodes).contains(((Terminal)first)))
				return ((Terminal)first).getLemma().equalsIgnoreCase(((Nonterminal)second).getHead());
		}
		if(first instanceof Nonterminal && second instanceof Nonterminal){
			Set<Terminal> firstTerminals = corpusProcessor.getAllTerminals(firstFeNodes);
			Set<Terminal> secondTerminals = corpusProcessor.getAllTerminals(secondFeNodes);
			if(firstTerminals.containsAll(secondTerminals) || secondTerminals.containsAll(firstTerminals))
				return ((Nonterminal)first).getHead().equalsIgnoreCase(((Nonterminal)second).getHead());
		}
		return false;
	}
	/**
	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("start new CoreferenceChain:" + newline);

		for (SingleCoreferenceAnnotation singleAnnotation : singleCoreferences) {

			buffer.append("\t" + singleAnnotation.toString() + newline);
		}

		return buffer.toString();
	}
	**/
	
	public String toString(){
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("NEW CHAIN" + newline);
		
		for(SingleCoreferenceAnnotation sca : singleCoreferences){
			Set<Terminal> current = null;
			Set<Terminal> coref = null;
			
			for(FrameElement fe : sca.getFes()){
				buffer.append("\t");
				if(fe.getName().equalsIgnoreCase("Current")){
					current = corpusProcessor.getAllTerminals(fe.getFenodes());
					buffer.append(fe.getFenodes().get(0).getIdref().getId());
				}else if(fe.getName().equalsIgnoreCase("Coreferent")){
					coref = corpusProcessor.getAllTerminals(fe.getFenodes());
					buffer.append(fe.getFenodes().get(0).getIdref().getId());
				}
			}
			buffer.append("\t");
			if(null != current){
				for(Terminal t : current){
					buffer.append(t.getWord() + " ");
				}
				buffer.append(" -> ");
			}
			if(null != coref){
				for(Terminal t : coref){
					buffer.append(t.getWord() + " ");
				}
				buffer.append(newline);
			}
		}
		
		return buffer.toString();
	}
}
