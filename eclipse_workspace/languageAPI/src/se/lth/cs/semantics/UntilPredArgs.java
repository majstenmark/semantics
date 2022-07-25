package se.lth.cs.semantics;

public class UntilPredArgs implements ProgramStatement{
	public ProgramStatement doThis, until;
	
	public UntilPredArgs(ProgramStatement doThis, ProgramStatement cond){
		if(doThis == null || cond== null) throw new IllegalArgumentException();
		this.doThis = doThis;
		this.until = cond;
	}
	
	public String toString(){
		if(doThis == null || until== null) return "Null fucking pointer";
		return "DO " + doThis.toString() + " UNTIL " + until.toString();
		
	}
	
	public String getType(){
		return "UNTIL";
		
	}
	
	int sentenceNbr;
	

	@Override
    public void setSentenceNbr(int i) {
		sentenceNbr = i;
		doThis.setSentenceNbr(i);
		until.setSentenceNbr(i);
		
	}
    public int getSentenceNbr() {
		// TODO Auto-generated method stub
		return sentenceNbr;
	}
	
}
