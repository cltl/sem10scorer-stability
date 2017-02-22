package semeval.mapping;

public class Lemma {

	String name;
	String id;
	String pos;

	public Lemma(String id, String name) {

		this.name = name;
		this.id = id;
	}

	public String getName() {

		return this.name;
	}

	public String getId() {

		return this.id;
	}
	
	public String getPos() {
		return pos;
	}
	
	public void setPos(String pos) {
		this.pos = pos;
	}

}
