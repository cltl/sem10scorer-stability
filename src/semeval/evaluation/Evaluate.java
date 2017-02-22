package semeval.evaluation;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import propbank.pbparser.Pbparser;
import salsa.corpora.elements.Corpus;
import salsa.corpora.xmlparser.CorpusParser;

/**
 * @author Philip John Gorinski a short test for the
 *         <code>AnnotationEvaluator</code>
 */
public class Evaluate {
	private static String goldFile = null;
	private static String annoFile = null;
	private static String mapFile = null;
	private static String task = null;
	private static Corpus gold;
	private static Corpus anno;
	private static AnnotationEvaluator ae;

	/**
	 * this parses the commandline arguments. Order is not important.
	 * 
	 * @param args
	 *            the commandline arguments
	 * @return true if the arguments were correct, false otherwise
	 */
	private static boolean parseCommandLine(String[] args) {
		if (args.length < 2)
			return false;
		for (String arg : args) {
			String[] argVal = arg.split("=");
			if (argVal[0].equalsIgnoreCase("gold"))
				goldFile = argVal[1];
			if (argVal[0].equalsIgnoreCase("annotation"))
				annoFile = argVal[1];
			if (argVal[0].equalsIgnoreCase("mapFile"))
				mapFile = argVal[1];
			if (argVal[0].equalsIgnoreCase("task"))
				task = argVal[1];
		}
		if (goldFile == null || annoFile == null || task == null)
			return false;
		return true;
	}

	/**
	 * generate an <code>AnnotationEvaluator</code> from a gold-corpus and an
	 * automatically annotated corpus, get precision, recall and link overlap
	 * 
	 * @param args
	 *            The commandline arguments, defining the gold-standard and
	 *            automatically annotated files. Arguments are
	 *            gold=path/to/someFile and annotation=path/to/anotherFile
	 */
	public static void main(String[] args) {
		if (parseCommandLine(args)) {
			try {
				if(goldFile.endsWith(".xml")){
					CorpusParser parser = new CorpusParser();
					gold = parser.parseCorpusFromFile(goldFile);
					anno = parser.parseCorpusFromFile(annoFile);
					Corpus map = null;
					if (mapFile != null) {
						map = parser.parseCorpusFromFile(mapFile);
						ae = new AnnotationEvaluator(gold, anno, map);
					} else {
						ae = new AnnotationEvaluator(gold, anno);
					}
				}
				if(goldFile.endsWith(".txt")){
					Pbparser parser = new Pbparser();
					gold = parser.parseCorpus(goldFile);
					anno = parser.parseCorpus(annoFile);
					/*
					BufferedWriter goldWriter = new BufferedWriter(new FileWriter("files/josef/gold.xml"));
					BufferedWriter annoWriter= new BufferedWriter(new FileWriter("files/josef/anno.xml"));
					goldWriter.write(gold.toString());
					annoWriter.write(anno.toString());
					goldWriter.close();
					annoWriter.close();
					*/
					ae = new AnnotationEvaluator(gold, anno);
				}

				if (task.equalsIgnoreCase("NoInstantiation")) {
					double[] evalValues = ae.evaluateNoInstantiations();
					System.out.println("True Positives: " + (int)evalValues[0]);
					System.out.println("False Positives: " + (int)evalValues[1]);
					System.out.println("False Negatives: " + (int)evalValues[2] + "\n");
					System.out.println("Precision: " + evalValues[3]);
					System.out.println("Recall: " + evalValues[4]);
					System.out.println("F-Score: " + evalValues[5]);
					System.out.println("Overlap: " + evalValues[6]);
				} else {
					if (task.equalsIgnoreCase("FullTask")) {
						double[] evalValues = ae.evaluateFullTask();
						System.out.println("Argument Recognition True Positives: " + (int)evalValues[0]);
						System.out.println("Argument Recognition False Positives: " + (int)evalValues[1]);
						System.out.println("Argument Recognition False Negatives: " + (int)evalValues[2]);
						System.out.println("Labels assigned in Gold Standard: " + (int)evalValues[3]);
						System.out.println("Of which where correctly assigned in Annotation: " + (int)evalValues[4] + "\n");
						System.out.println("Argument Recognition Precision: "
								+ evalValues[5]);
						System.out.println("Argument Recognition Recall: "
								+ evalValues[6]);
						System.out.println("Argument Recognition F-Score: "
								+ evalValues[7]);
						System.out.println("Label Assignment Accuracy: "
								+ evalValues[8]);
					}else{
						System.err.println("The task you specified is not available. Available tasks are: NoInstantiation, FullTask");
						System.exit(4);
					}
				}
			} catch (Exception e) {
				System.err.println(e.toString());
				System.exit(1);
			}
		} else {
			System.err
					.println("Something is wrong with your arguments...\nArguments are gold=FILE1, annotation=FILE2, mapFile=FILE3, task={NoInstantiation,FullTask}");
			System.exit(-1);
		}
	}
}
