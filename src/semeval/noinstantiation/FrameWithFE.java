package semeval.noinstantiation;

import java.util.ArrayList;

import semeval.mapping.Frame;

public class FrameWithFE extends Frame {

	private ArrayList<FrameElement> fes;
	

	public FrameWithFE(String name) {
		super(name);
		fes = new ArrayList<FrameElement>();
		
	}

	public ArrayList<FrameElement> getFes() {
		return fes;
	}

	
	
	public void addFe(FrameElement fe) {
		fes.add(fe);
	}
}
