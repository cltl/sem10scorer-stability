package semeval.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import salsa.corpora.elements.Corpus;
import salsa.corpora.elements.Edge;
import salsa.corpora.elements.Fenode;
import salsa.corpora.elements.Frame;
import salsa.corpora.elements.FrameElement;
import salsa.corpora.elements.Nonterminal;
import salsa.corpora.elements.Sentence;
import salsa.corpora.elements.Terminal;
import salsa.corpora.noelement.Id;
import salsa.corpora.processing.CorpusProcessor;
import semeval.coreference.CoreferenceChain;
import semeval.coreference.CoreferenceChainsExtractor;
import semeval.coreference.SingleCoreferenceAnnotation;
import semeval.headMerge.HeadMerger;

/**
 * the AnnotationEvaluator evaluates an automatic annotation of
 * nullinstantiations against the annotation of a gold standard
 * 
 * @author Philip John Gorinski
 */
public class AnnotationEvaluator {

	private Set<CoreferenceChain> goldChains;
	private Corpus annoCorpus;
	private Corpus goldCorpus;
	private Corpus mapCorpus;
	private CorpusProcessor goldProcessor;
	private CorpusProcessor annoProcessor;

	/**
	 * this prepares a <code>Corpus</code> for processing with the
	 * AnnotationEvaluator
	 * 
	 * @param mapCorpus
	 *            a Corpus containing syntactic information
	 * @param aCorpus
	 *            corpus to be prepared for processing
	 */
	private void prepare(Corpus mapCorpus, Corpus aCorpus) {
		HeadMerger merger = new HeadMerger();
		merger.merge(aCorpus, mapCorpus);
		for (Sentence s : aCorpus.getBody().getSentences()) {
			for (Terminal t : s.getGraph().getTerminals().getTerminals()) {
				t.getId().setHead(t.getWord());
				t.getId().setIsTerminal(true);
			}
		}
	}

	/**
	 * this prepares a <code>Corpus</code> for processing with the
	 * AnnotationEvaluator without the need of head-mapping
	 * 
	 * @param aCorpus
	 *            corpus to be prepared for processing
	 */
	private void prepare(Corpus aCorpus) {
		for (Sentence s : aCorpus.getBody().getSentences()) {
			for (Terminal t : s.getGraph().getTerminals().getTerminals()) {
				t.getId().setHead(t.getWord());
				t.getId().setIsTerminal(true);
			}
		}
	}

	/**
	 * gets the <code>Id</code> of the <code>Nonterminal</code> or
	 * <code>Terminal</code> that has the same Id-Value as the given Id
	 * 
	 * @param anID
	 *            an Id of any element in a <code>Corpus</code>
	 * @param aProcessor
	 *            a <code>CorpusProcessor</code> to get the new Id from
	 * @return the Id of the Nonterminal or Terminal matching the given Id
	 */
	private Id getRealID(Id anID, CorpusProcessor aProcessor) {
		for (Nonterminal nt : aProcessor.getAllNonterminalsInCorpus()) {
			if (nt.getId().getId().equals(anID.getId())) {
				return nt.getId();
			}
		}
		for (Terminal t : aProcessor.getAllTerminalsInCorpus()) {
			if (t.getId().getId().equals(anID.getId())) {
				return t.getId();
			}
		}
		System.err.println("oO No ID found...");
		System.exit(-1);
		return null;
	}

	/**
	 * Returns the intersection size (see "overlap" in the paper) of two
	 * <code>HashSet</code>s of <code>Terminal</code>s.
	 * 
	 * @param set1
	 *            the first set
	 * @param set2
	 *            the second set
	 * @return the intersection (overlap) of the two sets
	 */
	private double getIntersectSize(HashSet<Terminal> set1,
			HashSet<Terminal> set2) {
		double intersectSize = 0.0;
		for (Terminal t1 : set1) {
			if (this.contains(set2, t1)) {
				intersectSize += 1.0;
			}
		}
		return 2 * intersectSize / (double) (set1.size() + set2.size());
	}

	/**
	 * gets the number of annotated No-Instantiations in a <code>Corpus</code>
	 * 
	 * @param aCorpus
	 *            the Corpus
	 * @return the number of annotated No-Instantiations in the Corpus
	 */
	private int NIsize(Corpus aCorpus) {
		ArrayList<Sentence> sentences = aCorpus.getBody().getSentences();
		int count = 0;
		for (Sentence s : sentences) {
			if (s.getSem().getFrames().size() != 0) {
				for (Frame frame : s.getSem().getFrames().get(0).getFrames()) {
					for (FrameElement fe : frame.getFes()) {
						if ((fe.getFenodes().size() != 0)
								&& (fe.hasFlag("Definite_Interpretation") || fe
										.hasFlag("Indefinite_Interpretation"))) {
							count++;
						}
					}
				}
			}
		}
		return count;
	}

	/**
	 * returns a mapping of annotated No-Instantiations'
	 * <code>FrameElement</code>s in an automatically annotated
	 * <code>Corpus</code> to the corresponding FrameElements of a gold-standard
	 * 
	 * @param anno
	 *            the automatically annotated Corpus
	 * @param gold
	 *            the gold-standard Corpus
	 * @return a <code>HashMap</code> Mapping anno-FEs to gold-FEs
	 */
	private HashMap<FrameElement, FrameElement> getNImapping(Corpus anno,
			Corpus gold) {
		HashMap<FrameElement, FrameElement> noInstantiations = new HashMap<FrameElement, FrameElement>();

		ArrayList<Sentence> goldSentences = gold.getBody().getSentences();
		ArrayList<Sentence> annoSentences = anno.getBody().getSentences();

		for (Sentence s : annoSentences) {
			if (s.getSem().getFrames().size() != 0) {
				for (Sentence s2 : goldSentences) {
					if (s2.getId().getId().equals(s.getId().getId())) {
						if (s2.getSem().getFrames().size() != 0) {
							for (Frame frame : s.getSem().getFrames().get(0)
									.getFrames()) {
								for (Frame frame2 : s2.getSem().getFrames()
										.get(0).getFrames()) {
									if (frame.getId().getId().equals(
											frame2.getId().getId())) {
										for (FrameElement fe : frame.getFes()) {
											if (fe.getFenodes().size() != 0
													&& (fe
															.hasFlag("Definite_Interpretation") || fe
															.hasFlag("Indefinite_Interpretation"))) {
												noInstantiations.put(fe, null);
												for (FrameElement fe2 : frame2
														.getFes()) {
													if (fe2
															.getName()
															.equalsIgnoreCase(
																	fe
																			.getName())) {
														noInstantiations.put(
																fe, fe2);
													}
												}
											}
										}
									}
								}
							}
						} else {
							System.err
									.println("Frames have been changed. That should not have been done, exiting!");
							System.exit(-4);
						}
						continue;
					}
				}
			}
		}
		return noInstantiations;
	}

	/**
	 * Checks if two <code>HashSet</code>s of <code>Terminal</code> contain
	 * exactly the same Terminals
	 * 
	 * @param set1
	 *            the first set
	 * @param set2
	 *            the second set
	 * @return true if both sets share exactly the same elements, false
	 *         otherwise
	 */
	private boolean equals(HashSet<Terminal> set1, HashSet<Terminal> set2) {
		return this.containsAll(set1, set2) && this.containsAll(set2, set1);
	}

	/**
	 * Checks for tow <code>ArrayList</code>s of <code>Fenode</code>s whether
	 * all Fenodes found in list1 are present in list 2 and vice versa
	 * 
	 * @param list1
	 *            the first list
	 * @param list2
	 *            the second list
	 * @return true iff all Fenodes in list1 are present in list2 and vice versa
	 */
	private boolean equals(ArrayList<Fenode> list1, ArrayList<Fenode> list2) {
		if (list1.size() != list2.size())
			return false;

		for (Fenode node1 : list1) {
			boolean found = false;
			for (Fenode node2 : list2) {
				if (node1.getIdref().getId().equals(node2.getIdref().getId()))
					found = true;
			}
			if (!found)
				return false;
		}
		for (Fenode node2 : list2) {
			boolean found = false;
			for (Fenode node1 : list1) {
				if (node2.getIdref().getId().equals(node1.getIdref().getId()))
					found = true;
			}
			if (!found)
				return false;
		}
		return true;
	}

	/**
	 * Checks if a <code>HashSet</code> of contains a specified
	 * <code>Terminal</code>
	 * 
	 * @param set
	 *            the set, containing Terminals
	 * @param term
	 *            the Terminal to be checked
	 * @return true if the Terminal is contained in the Set, false otherwise
	 */
	private boolean contains(HashSet<Terminal> set, Terminal term) {
		for (Terminal t : set)
			if (t.equals(term))
				return true;
		return false;
	}

	/**
	 * Checks if a <code>HashSet</code> of <code>Terminal</code>s contains all
	 * the Terminals of another HashSet
	 * 
	 * @param set1
	 *            the first set
	 * @param set2
	 *            the second set
	 * @return true if set1 contains all the Terminals of set2, false otherwise
	 */
	private boolean containsAll(Set<Terminal> set1, Set<Terminal> set2) {
		for (Terminal t1 : set1) {
			boolean found = false;
			for (Terminal t2 : set2) {
				if (t1.equals(t2))
					found = true;
			}
			if (!found)
				return false;
		}
		return true;
	}

	/**
	 * Returns the <code>CoreferenceChain</code> (of the gold corpus) that is
	 * the chain belonging to a given ID
	 * 
	 * @param anID
	 *            <code>Id</code> of some word/consitutent of a sentence
	 * @return the CoreferenceChain where that word/constituent serves as a
	 *         'Current'-Element or null if no such chain exists
	 */
	private CoreferenceChain getChain(Id anID) {
		for (CoreferenceChain chain : this.goldChains) {
			for (SingleCoreferenceAnnotation single : chain
					.getSingleCoreferences()) {
				for (FrameElement fe : single.getFes()) {
					for (Fenode node : fe.getFenodes()) {
						if (node.getIdref().getId().equals(anID.getId()))
							return chain;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the <code>CoreferenceChain</code> (of the gold corpus) that is
	 * the chain containing a given <code>FrameElement</code>
	 * 
	 * @param anFe
	 *            the FrameElement to be contained in the chain that is returned
	 * @return the chain containing the given FrameElement or null, if no such
	 *         chain exists
	 */
	private CoreferenceChain getChain(FrameElement anFe) {
		ArrayList<String> IDs1 = new ArrayList<String>();
		for (Fenode node : anFe.getFenodes())
			IDs1.add(node.getIdref().getId());
		for (CoreferenceChain chain : this.goldChains) {
			for (SingleCoreferenceAnnotation single : chain
					.getSingleCoreferences()) {
				for (FrameElement fe : single.getFes()) {
					ArrayList<String> IDs2 = new ArrayList<String>();
					for (Fenode node : fe.getFenodes())
						IDs2.add(node.getIdref().getId());
					if (IDs1.equals(IDs2))
						return chain;
				}
			}
		}
		return null;
	}

	/**
	 * evaluates the precision, recall and linking overlap of this
	 * AnnotationEvaluator's annoCorpus and goldCorpus with respect to
	 * No-Instantiations
	 * 
	 * @return an Array of <code>double</code>. Positions 0/1/2/3 correspond to
	 *         precision/recall/F-Score/linking overlap
	 */
	public double[] evaluateNoInstantiations() {
		System.out
				.println("\nYou are evaluating the No-Instantiations of a corpus\n");

		int correct = 0;
		int count = 0;
		int overlapCounter = 0;
		double overlap = 0.0;

		// get a mapping of the no-instantiations of both corpora
		HashMap<FrameElement, FrameElement> noInstantiations = this
				.getNImapping(this.annoCorpus, this.goldCorpus);

		for (FrameElement annoFE : noInstantiations.keySet()) {
			count++;

			// get all the terminals spanned by the current FE of the anno
			// corpus
			HashSet<Terminal> annoTerms = (HashSet<Terminal>) this.annoProcessor
					.getAllTerminals(annoFE.getFenodes());
			HashSet<Terminal> toRemove = new HashSet<Terminal>();
			for(Terminal t : annoTerms){
				if(t.getPos().contains("PUNC"))
					toRemove.add(t);
			}
			for(Terminal t : toRemove){
				annoTerms.remove(t);
			}
			toRemove.clear();

			FrameElement goldFE = noInstantiations.get(annoFE);
			if (goldFE != null && goldFE.getFenodes().size() != 0) {
				// get all the terminals spanned by the current FE of the gold
				// corpus
				HashSet<Terminal> goldTerms = (HashSet<Terminal>) this.goldProcessor
						.getAllTerminals(goldFE.getFenodes());
				for(Terminal t : goldTerms){
					if(t.getPos().contains("PUNC"))
						toRemove.add(t);
				}
				for(Terminal t : toRemove){
					goldTerms.remove(t);
				}
				toRemove.clear();

				// best case: both sets of terminals are the same.
				if (this.equals(annoTerms, goldTerms)) {
					correct++;
					overlapCounter++;
					overlap += 1.0;
				} else {
					// second best case: annoTerms fully contains goldTerms or
					// vice versa
					if (this.containsAll(annoTerms, goldTerms)
							|| this.containsAll(goldTerms, annoTerms)) {
						// get the Head(s) (there may be several due to several
						// Fenodes annotated) of the goldFE and the annoFE
						ArrayList<String> goldHeads = new ArrayList<String>();
						ArrayList<String> annoHeads = new ArrayList<String>();
						for (Fenode goldNode : goldFE.getFenodes()) {
							goldHeads.add(this.getRealID(goldNode.getIdref(),
									goldProcessor).getHead());
						}
						for (Fenode annoNode : annoFE.getFenodes()) {
							annoHeads.add(this.getRealID(annoNode.getIdref(),
									annoProcessor).getHead());
						}
						// it's only a true positive, if the annotated heads are
						// the same (e.g. this provides for cases where a system
						// has only annotated the article of a Noun Phrase)
						if (goldHeads.containsAll(annoHeads)) {
							correct++;
							overlapCounter++;
							overlap += this.getIntersectSize(annoTerms,
									goldTerms);
						} else {
							if (annoHeads.containsAll(goldHeads)) {
								correct++;
								overlapCounter++;
								overlap += this.getIntersectSize(goldTerms,
										annoTerms);
							}
						}
					} else {
						// if we got here, we have to check all the
						// CoreferenceChains for annotation just like before
						boolean found = false;
						if (this.goldChains == null)
							this.goldChains = new CoreferenceChainsExtractor(
									goldCorpus).extractCoreferenceChains();
						CoreferenceChain chain = this.getChain(goldFE);
						if (chain != null) {
							for (SingleCoreferenceAnnotation single : chain
									.getSingleCoreferences()) {
								if (found)
									break;

								for (FrameElement fe : single.getFes()) {
									if (found)
										break;

									HashSet<Terminal> corefTerms = (HashSet<Terminal>) this.goldProcessor
											.getAllTerminals(fe.getFenodes());

									if (this.containsAll(corefTerms, annoTerms)
											|| this.containsAll(annoTerms,
													corefTerms)) {

										ArrayList<String> corefHeads = new ArrayList<String>();
										ArrayList<String> annoHeads = new ArrayList<String>();
										for (Fenode corefNode : fe.getFenodes())
											corefHeads.add(this.getRealID(
													corefNode.getIdref(),
													goldProcessor).getHead());
										for (Fenode annoNode : annoFE
												.getFenodes())
											corefHeads.add(this.getRealID(
													annoNode.getIdref(),
													annoProcessor).getHead());

										if (corefHeads.containsAll(annoHeads)) {
											found = true;
											correct++;
											overlapCounter++;
											overlap += this.getIntersectSize(
													annoTerms, corefTerms);
										} else {
											if (this.containsAll(annoTerms,
													corefTerms)) {
												found = true;
												correct++;
												overlapCounter++;
												overlap += this
														.getIntersectSize(
																corefTerms,
																annoTerms);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		double[] result = new double[7];
		result[0] = correct;
		result[1] = count - correct;
		result[2] = this.NIsize(this.goldCorpus) - correct;
		// Precision
		result[3] = (double) correct / count;
		// Recall
		result[4] = (double) correct / this.NIsize(this.goldCorpus);
		// F-Score
		result[5] = 2 * ((result[3] * result[4]) / (result[3] + result[4]));
		// NI linking overlap
		result[6] = (double) (overlap / overlapCounter);
		return result;
	}

	public double[] evaluateFullTask() {
		System.out.println("\nYou are performing a Full-Task Evaluation.\n");

		// True Positives and False Positives/Negatives for phrases that have
		// been assigned any role of a Frame
		int roleAssignTP = 0;
		int roleAssignFP = 0;
		int roleAssignFN = 0;

		// Count of labels annotated in the gold standard
		int labelAssignCount = 0;
		// Labels correctly annotated in the annotation
		int labelAssignCorrect = 0;

		// if there is an unequal number of sentences, the files that should be
		// evaluated don't match
		if (this.annoCorpus.getBody().getSentences().size() != this.goldCorpus
				.getBody().getSentences().size()) {
			System.err
					.println("Your files for evaluation have a different number of sentences. That's not good at all, exiting.");
			System.exit(-2);
		}

		// Collect all the Fenodes that have been annotated for a specific frame
		// in the gold standard and the annoation
		ArrayList<HashSet<Terminal>> goldTerminals = new ArrayList<HashSet<Terminal>>();
		ArrayList<HashSet<Terminal>> annoTerminals = new ArrayList<HashSet<Terminal>>();

		// Map each assigned role label to the Fenodes that have been annotated
		// for it
		HashMap<String, HashSet<Terminal>> goldLabels = new HashMap<String, HashSet<Terminal>>();
		HashMap<String, HashSet<Terminal>> annoLabels = new HashMap<String, HashSet<Terminal>>();

		// loop over each sentence
		for (int i = 0; i < this.goldCorpus.getBody().getSentences().size(); i++) {
			Sentence goldSentence = this.goldCorpus.getBody().getSentences()
					.get(i);
			Sentence annoSentence = this.annoCorpus.getBody().getSentences()
					.get(i);
			System.out.println("Gold: " + goldSentence.getId().getId());
			System.out.println("Anno: " + annoSentence.getId().getId());
			// possibly, there are no Frames annotated for a sentence
			if (goldSentence.getSem().getFrames().size() < 1)
				continue;
			// loop over each Frame annotated in the gold standard
			for (Frame goldFrame : goldSentence.getSem().getFrames().get(0)
					.getFrames()) {

				// Coreference-Frames are excluded from evaluation
				if (goldFrame.getName().equalsIgnoreCase("Coreference") || goldFrame.getName().equalsIgnoreCase("Relativization"))
					continue;

				// Frames are pre-assigned for the annotated corpus, if a
				// specific frame is not found, the file is not valid
				Frame annoFrame = null;
				for (Frame candidate : annoSentence.getSem().getFrames().get(0)
						.getFrames()) {
					if (candidate.getId().getId().equals(
							goldFrame.getId().getId()))
						annoFrame = candidate;
				}
				if (annoFrame == null) {
					System.err
							.println("A Frame has been modified in a way that is not allowed. Exiting.");
					System.exit(-3);
				}

				// clear the collections
				goldTerminals.clear();
				annoTerminals.clear();
				goldLabels.clear();
				annoLabels.clear();

				/*
				 * If we got here, we now have (1) a Frame in the gold standard
				 * and (2) the corresponding frame in the annotated file
				 */

				// collect all the Terminals annotated for the current goldFrame
				for (FrameElement goldFE : goldFrame.getFes()) {
					if (goldLabels.containsKey(goldFE.getName()))
						goldLabels.get(goldFE.getName()).addAll(
								(HashSet<Terminal>) goldProcessor
										.getAllTerminals(goldFE.getFenodes()));
					else {
						labelAssignCount++;
						goldLabels.put(goldFE.getName(),
								(HashSet<Terminal>) goldProcessor
										.getAllTerminals(goldFE.getFenodes()));
					}
				}
				// collect all the Terminals annotated for the current annoFrame
				for (FrameElement annoFE : annoFrame.getFes()) {
					if (annoLabels.containsKey(annoFE.getName()))
						annoLabels.get(annoFE.getName()).addAll(
								(HashSet<Terminal>) annoProcessor
										.getAllTerminals(annoFE.getFenodes()));
					else
						annoLabels.put(annoFE.getName(),
								(HashSet<Terminal>) annoProcessor
										.getAllTerminals(annoFE.getFenodes()));
				}

				// Role Labeling: count correctly assigned labels
				for (String key : goldLabels.keySet()) {
					if (annoLabels.containsKey(key)) {
						if (this.equals(annoLabels.get(key), goldLabels
								.get(key))) {
							labelAssignCorrect++;
						} else {
						}
					}
				}

				/*
				 * Role Assignment: True Positive: Terminals in Gold and Anno
				 * that are identical. False Postive: Terminals in Anno are not
				 * in Gold False Negative: Terminals in Gold are not in Anno
				 */
				for (String key : goldLabels.keySet()) {
					goldTerminals.add(goldLabels.get(key));
				}
				for (String key : annoLabels.keySet()) {
					annoTerminals.add(annoLabels.get(key));
				}
				// True positives:
				for (HashSet<Terminal> annoTerms : annoTerminals){
					try{
						for (int j = 0; j <= goldTerminals.size(); j++){
							if (this.equals(annoTerms, goldTerminals.get(j))){
								roleAssignTP++;
								goldTerminals.remove(j);
								break;
							}
						}
					}catch(IndexOutOfBoundsException e){
						roleAssignFP++;
					}
				}
				// False Negatives is the number of remaining goldFeNodes
				roleAssignFN += goldTerminals.size();
				// False Positives is the number of remaining annoFeNodes
				// roleAssignFP += annoTerminals.size();
			}

		}

		double[] result = new double[9];
		result[0] = roleAssignTP;
		result[1] = roleAssignFP;
		result[2] = roleAssignFN;
		result[3] = labelAssignCount;
		result[4] = labelAssignCorrect;
		// RoleAssignPrecision
		result[5] = (double) roleAssignTP / (roleAssignTP + roleAssignFP);
		// RoleAssignRecall
		result[6] = (double) roleAssignTP / (roleAssignTP + roleAssignFN);
		// RoleAssignF-Score
		result[7] = 2 * ((result[5] * result[6]) / (result[5] + result[6]));
		// LabelAssignAccuracy
		result[8] = (double) labelAssignCorrect / labelAssignCount;
		return result;
	}

	/**
	 * Constructor defining three Objects of typ <code>Corpus</code> as input,
	 * one of which is used for head-mapping
	 * 
	 * @param goldStandard
	 *            the gold-standard corpus
	 * @param autoAnno
	 *            the automatically annotated corpus
	 * @param mapCorpus
	 *            the Corpus that is used for head-mapping
	 */
	public AnnotationEvaluator(Corpus goldStandard, Corpus autoAnno,
			Corpus mapCorpus) {

		this.goldCorpus = goldStandard;
		this.annoCorpus = autoAnno;
		this.mapCorpus = mapCorpus;
		this.prepare(this.mapCorpus, this.goldCorpus);
		this.prepare(this.mapCorpus, this.annoCorpus);

		this.goldProcessor = new CorpusProcessor(this.goldCorpus);
		this.annoProcessor = new CorpusProcessor(this.annoCorpus);
	}

	/**
	 * Constructor defining two Objects of typ <code>Corpus</code> as input
	 * 
	 * @param goldStandard
	 *            the gold-standard corpus
	 * @param autoAnno
	 *            the automatically annotated corpus
	 */
	public AnnotationEvaluator(Corpus goldStandard, Corpus autoAnno) {
		this.goldCorpus = goldStandard;
		this.annoCorpus = autoAnno;
		this.prepare(this.goldCorpus);
		this.prepare(this.annoCorpus);

		this.goldProcessor = new CorpusProcessor(this.goldCorpus);
		this.annoProcessor = new CorpusProcessor(this.annoCorpus);
	}

	/**
	 * Unused private default constructor
	 */
	private AnnotationEvaluator() {

	}
}
