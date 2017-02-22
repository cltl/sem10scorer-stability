package semeval.mapping;

public class VNToFN {
	
	String vnclass;
	
	String vnmember;
	
	String fnframe;
	
	String fnlexent;
	
	String versionID;
	
	public VNToFN(String vnclass, String vnmember, String fnframe,
			String fnlexent, String versionID) {
	
		this.vnclass = vnclass;
		this.vnmember = vnmember;
		this.fnframe = fnframe;
		this.fnlexent = fnlexent;
		this.versionID = versionID;
	}

	public String getVnclass() {
		return vnclass;
	}

	public String getVnmember() {
		return vnmember;
	}

	public String getFnframe() {
		return fnframe;
	}

	public String getFnlexent() {
		return fnlexent;
	}

	public String getVersionID() {
		return versionID;
	}
	
	

}
