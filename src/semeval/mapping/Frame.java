package semeval.mapping;

public class Frame {

	String name;

	public Frame(String name) {

		this.name = name;
	}

	public String getName() {

		return this.name;
	}

	public boolean equals(Object o) {

		if (o instanceof Frame) {

			if (((Frame)o).getName().equals(this.name)) { return true; }
			else { return false; }
		} else { return false; }
	}

	public int hashCode() {

		return this.name.hashCode();
	}

}
