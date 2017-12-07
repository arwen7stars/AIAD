package structures;

import java.util.Comparator;

import jade.core.AID;

public class Rank implements Comparator, Comparable {
	
	public String aid;
	public Double gain;
	
	
	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		Rank r1 = (Rank)(arg0);
		Rank r2 = (Rank)(arg1);
				
		
		return r2.gain.compareTo(r1.gain);
	}
	
	public Rank(String aid, Double gain) {
		this.aid = aid;
		this.gain = gain;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		Rank r2 = (Rank)o;
		return (r2.gain).compareTo(gain);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return aid + ": " + gain;
	}
	
	
}
