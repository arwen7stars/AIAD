package structures;

import java.util.ArrayList;
import java.util.Random;

import jade.core.AID;

public  class randomCalc {

	public static Random fRandom = new Random();

	public static final double GOOD_AGENTS_SELECTION_PROB = 0.2;
	public static final double AGENTS_BUY_ACTION_PROB = 0.08;
	public static final double MAX_NUMBER_OF_STOCKS_TO_BUY = 50;
	public static final double AGENTS_SELL_ACTION_PROB = 0.05;
	public static final double AGENTS_SELL_ACTION_PROB_PROFIT = 0.2;
	public static final double MINIMUM_POSSIBLE_GAIN = 1000.0;
	public static final double FOLLOWING_USER_PROB = 0.25;
	public static final int DAY_PERIOD = 3;
	public static final int PAYMENT_PERIOD = 15;
	public static final int PAYMENT_VALUE = 100;
	public static final double ROC_CONST = 0.5 ; 
	public static final double ROC_SUCCESS = 0.6;
	public static final double ROC_INSUCCESS = 0.08;
	
	public static final String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public static final int monthsTotal[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

	public static int this_day = 1;
	public static int this_month = 8;
	public static int this_year = 17;
	
	public static AID[] getRandomGoodAgents(AID[] goodAgents) {
		/* Selecao de bons agentes (a partir dos disponivies) que irao receber dicas */
		
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
		return (int) (maxN*Math.random());
	}

	public static boolean sellAction(boolean d) {
		if((!d && Math.random() <=  AGENTS_SELL_ACTION_PROB) || (d && Math.random() <= AGENTS_SELL_ACTION_PROB_PROFIT))
			return true;
		return false;
	}
	
	
	public static String today() {
		return "" + randomCalc.this_day +"-" + randomCalc.months[randomCalc.this_month - 1] + "-" + randomCalc.this_year;
	}
	
	public static String nextDay() {
				
		randomCalc.this_day++;
		 
		if(((randomCalc.this_day - 1) % randomCalc.monthsTotal[randomCalc.this_month - 1]) == 0) {
			if(randomCalc.this_month == 12) {
				randomCalc.this_year++;
				randomCalc.this_day = 1;
				randomCalc.this_month = 1;
			}else {
				randomCalc.this_day = 1;
				randomCalc.this_month++;
			}			
		}		
		return today();
	}
	
	
	public static String tomorrow(String today) {
		String[] parts = today.split("-");
		int day = Integer.parseInt(parts[0]) + 1;
		int month = 1;
		 
		for(int i = 0; i < months.length; i++) {
			
			if(randomCalc.months[i].equals(parts[1])) {
				month = i + 1;
				break;
			}
		}
		
		int year = Integer.parseInt(parts[2]);
		
		
		if(((day - 1) % randomCalc.monthsTotal[month - 1]) == 0) {
			if(month == 11) {
				year++;
				day = 1;
				month = 1;
			}else {
				day = 1;
				month++;
			}			
		}
		
		return "" + day +"-" + randomCalc.months[month - 1] + "-" + year;	
	}

	public static String yesterday(String date) {
		String[] parts = date.split("-");
		int day = Integer.parseInt(parts[0]) - 1;
		int month = 1;
		
		for(int i = 0; i < months.length; i++) {
					
					if(randomCalc.months[i].equals(parts[1])) {
						month = i + 1;
						break;
					}
				}
		
		int year = Integer.parseInt(parts[2]);
		
		if(day == 0) {
			if(month == 1) {
				year--;
				day = 31;
				month = 12;
			} else {
				month--;
				day = monthsTotal[month -1];
				
			}			
		}

		return "" + day +"-" + randomCalc.months[month - 1] + "-" + year;
	}

	public static String lastMonth(String today) {
		String[] parts = today.split("-");
		int day = Integer.parseInt(parts[0]);
		int month = 1;
		
		for(int i = 0; i < months.length; i++) {
					
			if(randomCalc.months[i].equals(parts[1])) {
					month = i + 1;
					break;
			}
		}
		
		int year = Integer.parseInt(parts[2]);
		
		if(month == 1) {
			month = 12;
			year--;
		} else {
			month--;
			if(day > monthsTotal[month-1])
				day = monthsTotal[month -1];
		}
		
		return "" + day +"-" + randomCalc.months[month - 1] + "-" + year;
	}
}
