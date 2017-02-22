package semeval.noinstantiation;

import java.util.ArrayList;

public class FERealization {

	private int total;

	private ArrayList<ValenceUnit> valenceUnits;
	
	
	public FERealization(int total) {
		super();
		valenceUnits = new ArrayList<ValenceUnit>();
		this.total = total;
	}


	public ArrayList<ValenceUnit> getValenceUnits() {
		return valenceUnits;
	}


	public void setValenceUnits(ArrayList<ValenceUnit> valenceUnits) {
		this.valenceUnits = valenceUnits;
	}
	
	public void addValenceUnit(ValenceUnit valenceUnit) {
		
		this.valenceUnits.add(valenceUnit);
	}


	public int getTotal() {
		return total;
	}
	
	
	
	
}
