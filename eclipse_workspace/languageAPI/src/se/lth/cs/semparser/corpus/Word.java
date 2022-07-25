package se.lth.cs.semparser.corpus;
import java.util.*;
import java.io.*;

public class Word implements Serializable,Comparable<Word>{
	private static final long serialVersionUID = 12;
	
	public static final String AFTER = "After";
	public static final String BEFORE = "Before";
	public static final String ON = "On";
	
	public static enum WordData { Form, Lemma, POS, Deprel, Pred };
	
	Sentence mySentence;
	String[] args;
	
	String Form;
	String Lemma;
	String POS;
	String Deprel;
	boolean isPred = false;
	int headID;
	Word head;
	
	HashSet<Word> children;
	boolean isBOS;
	
	Word potentialArgument; //This is basically an attribute of Predicate rather than Word,
							//but this way things work smoother with the features.
	
	public Word() {
		isBOS=true;
		children=new HashSet<Word>();
		this.headID=-1;
	}
	
	public Word(String form,String lemma,String POS,Sentence mySentence){
		this.Form=form;
		this.Lemma=lemma;
		this.POS=POS;
		this.mySentence=mySentence;
		children=new HashSet<Word>();
	}
	
	
	/**
	 * Used to replace an old word with a new (updates dependencies). 
	 * Used to make a predicate from a word during predicate identification.
	 * @param w The Word
	 */
	
	public Word(Word w, boolean ligth) {
		this.Form=w.Form;
		this.Lemma=w.Lemma;
		this.POS=w.POS;
		this.Deprel=w.Deprel;
		this.head=w.head;
		this.headID=w.headID;
		this.children=w.children;
		this.mySentence=w.mySentence;
		this.isBOS=w.isBOS;
		//Then we have to update our children to make them point to this head rather than the old
		for(Word child:children)
			child.head=this;
		//And update our head's children to forget the old and add this one
	
	}
	
	public Word(Word w) {
		this.Form=w.Form;
		this.Lemma=w.Lemma;
		this.POS=w.POS;
		this.Deprel=w.Deprel;
		this.head=w.head;
		this.headID=w.headID;
		this.children=w.children;
		this.mySentence=w.mySentence;
		this.isBOS=w.isBOS;
		//Then we have to update our children to make them point to this head rather than the old
		for(Word child:children)
			child.head=this;
		//And update our head's children to forget the old and add this one
		head.children.remove(w);
		head.children.add(this);
	}
	
	public Word(String[] CoNLL2009Columns){
		this.Form=CoNLL2009Columns[1];
		this.Lemma=CoNLL2009Columns[3];
		this.POS=CoNLL2009Columns[5];
		this.headID=Integer.parseInt(CoNLL2009Columns[9]);
		this.Deprel=CoNLL2009Columns[11];
		args=new String[CoNLL2009Columns.length-14];
		for(int i=0;i<args.length;++i){
			args[i]=CoNLL2009Columns[14+i];
		}
		children=new HashSet<Word>();
	}

	/*
	 * Getters
	 */
	public String getAttr(WordData attr){
		switch(attr){
		case Form: return Form;
		case Lemma: return Lemma;
		case POS: return POS;
		case Deprel: return Deprel;
		default: return null; //We shouldn't enter here
		}
	}
	
	public boolean isPred(){
		return isPred;
		
	}
	public String getForm() {
		return Form;
	}
	public String getLemma() {
		return Lemma;
	}
	public String getPOS() {
		return POS;
	}
	public Word getHead() {
		return head;
	}
	public int getHeadId() {
		return headID;
	}
	public String getDeprel() {
		return Deprel;
	}
	public Word getPotentialArgument() {
		return potentialArgument;
	}
	public HashSet<Word> getChildren(){
		return children;
	}
	public Sentence getMySentence() {
		return mySentence;
	}
	public String getArg(int i){
		try {
			return args[i];
		} catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Corpus contains errors, missing semantic arguments, Word: "+this);
			return "_";
		}
	}

	/*
	 * Setters
	 */
	public void setHead(Word h) {
		head=h;
		headID=mySentence.indexOf(h);
		h.children.add(this);
	}
	public void setDeprel(String deprel){
		this.Deprel=deprel;
	}
	public void setPotentialArgument(Word potentialArgument) {
		this.potentialArgument = potentialArgument;
	}
	protected void setChildren(HashSet<Word> children){
		this.children=children;
	}
	public void setMySentence(Sentence mySentence) {
		this.mySentence = mySentence;
	}


	
	public void clearArgArray(){
		args=null;
	}
	

	
	public boolean isBOS(){
		return isBOS;
	}
	public boolean isPassiveVoiceEng() {
		if(!getPOS().equals("VBN")) 
			return false;
		if(!head.isBOS && head.Form.matches("(be|am|are|is|was|were|been)"))
			return true;
		
		return false;
	}

	/*
	 * Getters for siblings and dependents
	 */
	public Word getLeftSibling() {
		for(int i=(mySentence.indexOf(this)-1);i>0;--i){
			if(head.children.contains(mySentence.get(i)))
				return mySentence.get(i);
		}
		return null;
	}
	
	public Word getRightSibling() {
		for(int i=(mySentence.indexOf(this)+1);i<mySentence.size();++i){
			if(head.children.contains(mySentence.get(i)))
				return mySentence.get(i);
		}
		return null;
	}
	public Word getRightmostDep(){
		if(children.isEmpty()) 
			return null;
		Word ret=null;
		for(int i=mySentence.indexOf(this);i<mySentence.size();++i){
			if(children.contains(mySentence.get(i)))
				ret=mySentence.get(i);
		}
		return ret;
	}
	public Word getLeftmostDep(){
		if(children.isEmpty()) 
			return null;
		Word ret=null;
		for(int i=mySentence.indexOf(this);i>0;--i){
			if(children.contains(mySentence.get(i)))
				ret=mySentence.get(i);
		}
		return ret;
	}	
	
	/**
	 * Comparator used by method joinChildAttrs
	 */
	private static final Comparator<Word> wordComparator = new Comparator<Word>(){
		public int compare(Word o1, Word o2) {
			if(o1.mySentence!=o2.mySentence)
				throw new Error("You are wrong here: Trying to sort words of different sentences");
			Sentence mySentence=o1.mySentence;
			if(mySentence.indexOf(o1) < mySentence.indexOf(o2)){
				return -1;
			} else if(mySentence.indexOf(o1) > mySentence.indexOf(o2)){
				return 1;
			} else {
				return 0;
			}
		}
	};
	public String joinChildAttrs(WordData attr,boolean sorted){
		StringBuffer ret=new StringBuffer();
		HashSet<Word> words=children;
		Word[] t=words.toArray(new Word[0]);;
		if(sorted){
			Arrays.sort(t); //TODO this looks weird. Drop this block and the parameter sorted? Doesn't seem to be used anyway
		} else {
			Arrays.sort(t,wordComparator); //Sorted by word order in sentence
		}
		for(Word w:t)
			ret.append(w.getAttr(attr)+" ");
		return ret.toString().trim();
	}
	
	/**
	 * Compares the position of two words in a sentence.
	 * @param w1 First word
	 * @param w2 Second word
	 * @return AFTER,BEFORE,ON or null. null if w1 and w2 is not same sentence
	 */
	public static String getPosition(Word w1,Word w2) {
		if(w1.mySentence!=w2.mySentence) return null;
		int a=w1.mySentence.indexOf(w1);
		int b=w1.mySentence.indexOf(w2);
		if (a-b<0) return BEFORE;
		else if (a-b>0) return AFTER;
		else return ON;
	}


	public static ArrayList<Word> findPath(Word pred,Word arg){
		ArrayList<Word> predPath=pathToRoot(pred);
		ArrayList<Word> argPath=pathToRoot(arg);
		ArrayList<Word> ret=new ArrayList<Word>();
		
		int commonIndex=0;
		int min=(predPath.size()<argPath.size()?predPath.size():argPath.size());
		for(int i=0;i<min;++i) {
			if(predPath.get(i)==argPath.get(i)){ //Always true at root (ie first index)
				commonIndex=i;
			}
		}
		for(int j=predPath.size()-1;j>=commonIndex;--j){
			ret.add(predPath.get(j));
		}
		for(int j=commonIndex+1;j<argPath.size();++j){
			ret.add(argPath.get(j));
		}
		return ret;
	}
	
	public static ArrayList<Word> pathToRoot(Word w){
		ArrayList<Word> path;
		if(w.isBOS){
			path=new ArrayList<Word>();
			path.add(w);
			return path;
		}
		path=pathToRoot(w.head);
		path.add(w);
		return path;
	}
	/**
	 * Converts this Word object one line following the CoNLL 2009 format.
	 * However, it does not include all columns, since this is part of a sentence.
	 * For proper CoNLL 2009 format output, use the Sentence.toString() method
	 */
	public String toString() {
		return Form+"\t"+Lemma+"\t"+Lemma+"\t"+POS+"\t"+POS+"\t_\t_\t"+headID+"\t"+headID+"\t"+Deprel+"\t"+Deprel;
		//return Form+"\t"+Lemma+"\t"+POS+"\t"+headID+"\t"+Deprel;
	}
	/**
	 * Recursive function that returns all nodes (words) dominated by the nodes,
	 * 
	 * @param words The nodes to descend from
	 * @return
	 */
	private static Collection<Word> getDominated(Collection<Word> words){
		Collection<Word> ret=new HashSet<Word>(words);
		for(Word c:words)
			ret.addAll(getDominated(c.getChildren()));
		return ret;
	}
	/**
	 * Returns the yield of this word, ie the complete phrase that defines the argument,
	 * with respect to the predicate. It follows algorithm 5.3 in Richard Johansson (2008), page 88
	 * @param pred The predicate of the proposition, required to deduce the yield
	 * @return the Yield
	 */
	public Yield getYield(Predicate pred,String argLabel,Set<Word> argSet){
		Yield ret=new Yield(pred,mySentence,argLabel);
		ret.add(this);
		if(pred==this) //If the predicate is the argument, we don't consider the yield
			return ret;
		for(Word child:children){
			if(!argSet.contains(child)){ //We don't branch down this child if 
				Collection<Word> subtree=getDominated(Arrays.asList(child));
				if(!subtree.contains(pred))
					ret.addAll(subtree);
			}
		}
		//ret.addAll(getDominated(children));
		return ret;
	}
	/**
	 * Compares words with respect to their order in a sentence.
	 * Words are assumed to belong to the same sentence, and this is NOT checked during runtime. 
	 * (And no exception is thrown, even if they don't)
	 */
	@Override
	public int compareTo(Word arg0) {
		return mySentence.indexOf(this)-mySentence.indexOf(arg0);
	}
}
