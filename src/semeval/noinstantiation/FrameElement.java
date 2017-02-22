package semeval.noinstantiation;

public class FrameElement {
	
	private boolean isCore;
	
	private String name;

	public FrameElement(boolean isCore, String name) {
		super();
		this.isCore = isCore;
		this.name = name;
	}

	public boolean isCore() {
		return isCore;
	}

	public String getName() {
		return name;
	}
	
	

}
