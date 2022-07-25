package se.lth.cs.semantics;

public class WhilePredArgs implements ProgramStatement{
	public ProgramStatement doThis, whileThis;
	
	public WhilePredArgs(ProgramStatement doThis, ProgramStatement cond){
		this.doThis = doThis;
		this.whileThis = cond;
	}
	
	public String toString(){
		return "DO " + doThis.toString() + " WHILE " + whileThis.toString();
		
	}
	
	public String getType(){
		return "WHILE";
		
	}
	int sentenceNbr;

	@Override
    public void setSentenceNbr(int i) {
		sentenceNbr = i;

		doThis.setSentenceNbr(i);
		whileThis.setSentenceNbr(i);
		
	}

	@Override
    public int getSentenceNbr() {
		// TODO Auto-generated method stub
		return sentenceNbr;
	}
}
