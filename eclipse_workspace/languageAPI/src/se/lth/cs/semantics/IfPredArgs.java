package se.lth.cs.semantics;

public class IfPredArgs implements ProgramStatement{
	public ProgramStatement cond, consequence;
	
	public IfPredArgs(ProgramStatement cond, ProgramStatement consequence){
		this.cond = cond;
		this.consequence = consequence;
	}
	
	
	public String toString(){
		String tmp1 = cond!= null? cond.toString(): "";
		String tmp2 = consequence!= null? consequence.toString(): "";
		
		return "IF " + tmp1 + " THEN " + tmp2; 
		
	}
	
	public String getType(){
		return "IF";
		
	}
	
	int sentenceNbr;
	

	@Override
    public void setSentenceNbr(int i) {
		sentenceNbr = i;
		if(cond != null) cond.setSentenceNbr(i);
		if(consequence != null)consequence.setSentenceNbr(i);
		
	}


	@Override
	public int getSentenceNbr() {
		// TODO Auto-generated method stub
		return sentenceNbr;
	}

}
