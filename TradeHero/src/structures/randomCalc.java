package structures;

import java.util.ArrayList;
import java.util.Random;

import jade.core.AID;

public  class randomCalc {
	
	
	public static Random fRandom = new Random();

	
	public static final double GOOD_AGENTS_SELECTION_PROB = 0.2;
	public static final double AGENTS_BUY_ACTION_PROB = 0.1;
	public static final double MAX_NUMBER_OF_STOCKS_TO_BUY = 50;
	public static final double AGENTS_SELL_ACTION_PROB = 0.05;
	public static final double AGENTS_SELL_ACTION_PROB_PROFIT = 0.2;
	public static final double MINIMUM_POSSIBLE_GAIN = 1000.0;
	
	public static AID[] getRandomGoodAgents(AID[] goodAgents) {
		/* Seleção de bons agentes (a partir dos disponivies) que irão receber dicas */
		
		ArrayList<AID> selectedOnes = new ArrayList<AID>();
		for(int i = 0; i < goodAgents.length; i++) {
			double random = Math.random();
			if(random <= GOOD_AGENTS_SELECTION_PROB)
				selectedOnes.add(goodAgents[i]);
			
			
		}		
		
		return selectedOnes.toArray(new AID[selectedOnes.size()]);
	}
	
	public static boolean buyAction() {
		// TODO Auto-generated method stub
		if(Math.random() <=  AGENTS_BUY_ACTION_PROB)
			return true;
		
		return false;
	}
	
	public static int numberOfStocks(int maxN) {
		int numOfStocks = 0;
		
		int i = 0;
		do {
			numOfStocks = (int) (MAX_NUMBER_OF_STOCKS_TO_BUY*Math.random());
		}while(numOfStocks > maxN && i++ < 20 );		
		
		if(numOfStocks > maxN)
			numOfStocks = 0;
		
		return numOfStocks;
	}

	public static boolean sellAction(boolean d) {
		// TODO Auto-generated method stub
		if((!d && Math.random() <=  AGENTS_SELL_ACTION_PROB) || (d && Math.random() <= AGENTS_SELL_ACTION_PROB_PROFIT))
			return true;
		
		
		
		return false;
	}
	
	

}
