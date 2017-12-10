package structures;

import java.util.Comparator;

public class Rank implements Comparator<Object>, Comparable<Object> {
	
	public String aid;
	public Double gain;
	
	@Override
	public int compare(Object arg0, Object arg1) {
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
		Rank r2 = (Rank)o;
		return (r2.gain).compareTo(gain);
	}
	
	@Override
	public String toString() {
		return aid + ": " + gain;
	}
	
}
