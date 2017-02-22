package semeval.coreference;

import salsa.corpora.elements.Element;
import salsa.corpora.elements.Flag;
import salsa.corpora.elements.Frame;
import salsa.corpora.elements.FrameElement;

/**
 * A <code>SingleCoreferenceAnnotation</code> stands for one annotation of the
 * 'Coreference' frame.
 * 
 * @author The SALSA Project team
 * 
 */
public class SingleCoreferenceAnnotation extends Frame {

	/**
	 * Default constructor that takes the <code>Frame</code> as an argument.
	 * It simply converts the 'frame' into a
	 * <code>SingleCoreferenceAnnotation</code> element.
	 * 
	 * @param frame
	 */
	public SingleCoreferenceAnnotation(Frame frame) {

		super(frame.getName());

		setId(frame.getId());

		setSource(frame.getSource());

		setTarget(frame.getTarget());

		for (Element element : frame.getElements()) {
			addElement(element);
		}

		for (FrameElement fe : frame.getFes()) {
			addFe(fe);
		}

		for (Flag flag : frame.getFlags()) {
			addFlag(flag);
		}

		setUsp(frame.getUsp());

	}

}
