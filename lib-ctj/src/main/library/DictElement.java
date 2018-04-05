package main.library;

public class DictElement {
	private boolean function;
	private int frequency;
	private int syllables;
	private String spelling;
	private String arpabet;
	private String morphemes;
	private String cognate;
	private double biphAve;
	private double pSegAve;
	private String[] neighborhood;
	
	
	public DictElement() {
		neighborhood = null;
	}
	

	public boolean isFunction() { return function; }
	public void setFunction(boolean function) { this.function = function; }

	public int getFrequency() { return frequency; }
	public void setFrequency(int frequency) { this.frequency = frequency; }

	public int getSyllables() { return syllables; }
	public void setSyllables(int syllables) { this.syllables = syllables; }

	public String getSpelling() { return spelling; }
	public void setSpelling(String spelling) { this.spelling = spelling; }

	public String getArpabet() { return arpabet; }
	public void setArpabet(String arpabet) { this.arpabet = arpabet; }

	public String getMorphemes() { return morphemes; }
	public void setMorphemes(String morphemes) { this.morphemes = morphemes; }

	public String getCognate() { return cognate; }
	public void setCognate(String cognate) { this.cognate = cognate; }

	public double getBiphAve() {return biphAve; }
	public void setBiphAve(double biphAve) {this.biphAve = biphAve; }

	public double getPSegAve() { return pSegAve; }
	public void setpSegAve(double pSegAve) { this.pSegAve = pSegAve; }

	public String[] getNeighborhood() { if (neighborhood != null) return neighborhood; else return null; }
	public void setNeighborhood(String[] neighborhood) { this.neighborhood = neighborhood; }
	
	public String getNeighborhoodDisplay() { 
			String display = "";
			if(neighborhood != null) {
				for(int i = 0; i < neighborhood.length; i++) {
					if(i != neighborhood.length - 1) display += neighborhood[i] + ", ";
					else display += neighborhood[i];
				}
				return display;
			}
			else return null; }
}
