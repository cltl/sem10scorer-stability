package salsa.corpora.noelement;

/**
 * This class represents an 'id' or 'idref' in the salsa corpora. It overwrites
 * the equals() and the getHash() method so that two Id objects are the same
 * if they have the same id. 
 * @author Fabian Shirokov
 *
 */
public class Id {
	
	private String id;
	private String head;
	private boolean isTerminal;
	
	/**
	 * Sets the head of the Id
	 * @param head the head of this Id
	 */
	public void setHead(String head){
		this.head = head;
	}
	/**
	 * gets the head of the Id
	 * @return the head of this Id
	 */
	public String getHead(){
		return this.head;
	}
	
	/**
	 * Default constructor that takes the id as an argument.
	 * @param id
	 */
	public Id(String id) {
		this.id = id;
		this.isTerminal = false;
		this.head = null;
	}
	
	/**
	 * Constructor that takes the id and isTerminal as an argument.
	 * Set <code>isTerminal</code> to <code>false</code> if this <code>Id</code> 
	 * belongs to a nonterminal in the corpus.
	 * @param id
	 */
	public Id(String id, boolean isTerminal) {
		this.id = id;
		this.isTerminal = isTerminal;
		this.head = null;
	}
	
	public Id(String id, String head, boolean isTerminal){
		this.id = id;
		this.isTerminal = isTerminal;
		this.head = head;
	}
	
	/**
	 * Returns the id.
	 * @return
	 */
	public String getId(){
		return this.id;
	}
	
	/**
	 * Sets the id.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns <code>true</code> if this <code>Id</code> belongs to a terminal
	 * node in the given corpus.
	 * @return
	 */
	public boolean getIsTerminal() {
		return this.isTerminal;
	}
	
	/**
	 * Set <code>isTerminal</code> to <code>true</code> if this <code>Id</code>
	 * belongs to a terminal node in the given corpus.
	 * @param isTerminal
	 */
	public void setIsTerminal(boolean isTerminal) {
		this.isTerminal = isTerminal;
	}
	
	public boolean equals(Object anObject){
		if (this == anObject)
			return true;
		if (!(anObject instanceof Id))
			return false;
		Id ID2 = (Id)anObject;
		if(this.getHead() == null && ID2.getHead() == null)
			return this.getId().equals(ID2.getId()) && this.getIsTerminal() == ID2.getIsTerminal();
		return this.getId().equals(ID2.getId()) && this.getHead().equals(ID2.getHead()) && this.getIsTerminal() == ID2.getIsTerminal();
	}
}
