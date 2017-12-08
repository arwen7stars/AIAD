package tradeHero;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.domain.DFService;
import structures.Stock;
import structures.Tip;
import structures.randomCalc;
import tradeHero.behaviours.ReceiveStockUpdate;
import tradeHero.behaviours.ReceiveTip;

public class UserGoodAgent extends UserAgent {	
	public static final double GOOD_AGENTS_MAX_BUY_ACTION_PROB = 0.1;
	public static final double GOOD_AGENTS_MIN_BUY_ACTION_PROB = 0.9;
	public static final double GOOD_AGENTS_MAX_SELL_ACTION_PROB = 0.9;
	public static final double GOOD_AGENTS_MIN_SELL_ACTION_PROB = 0.1;

	public UserGoodAgent() {
		super();
	}
	
	@Override
	public void setup() {
		
		// Register user service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("buyers");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		
		
		DFAgentDescription dfd2 = new DFAgentDescription();
		dfd2.setName(getAID());
		ServiceDescription sd2 = new ServiceDescription();
		sd2.setType("goodbuyers");
		sd2.setName(getLocalName());
		dfd2.addServices(sd2);
		
		
		try {
			DFService.register(this, dfd);
			DFService.register(this, dfd2);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new ReceiveStockUpdateAgent(this));
		addBehaviour(new ReceiveTip(this));
	}
	
	
	class ReceiveStockUpdateAgent extends ReceiveStockUpdate {
		
		ReceiveStockUpdateAgent(UserAgent agent) {
			super(agent);
		}

		@Override
		public void buyerAction() {
			System.out.println("GOOD ACTION");
			/* Implementacao do comportamento do agente apos receber a informacao sobre o valor das acoes 
			 * 		Atributos:	
			 * 			today: 						String
			 * 			stoksPrice<Stock> :	 		ArrayList<Stock> com nome da stock e valor
			*/
			if(gains(stocksPrice) < randomCalc.MINIMUM_POSSIBLE_GAIN)
				myAgent.doDelete();		
			
			String s = "";
			
			for(int i = 0; i < stocksPrice.size(); i++) {
				boolean buyStocks = false;
				Stock stock = stocksPrice.get(i);
				int noStocks = -1;
				
				if(cash > stock.getValue()) {
					if((noStocks = buyAction(stock, noStocks)) > -1) {
						s += "buy&" + stock.getName() + "&" + noStocks  + "\n";
						buyStocks = true;
					}
				}
				
				if(getStocksOwned().containsKey(stock) && (!buyStocks || Math.random() < 0.3)) {
					if((noStocks = sellAction(stock, noStocks)) > -1){
						s += "sell&" + stock.getName() + "&" + noStocks  + "\n";
					}
				}	
			}
			
			if(!s.equals("")) {
				alertFollowers(s);
				updateGain(gains(stocksPrice));
			}		
		}
		
		private int buyAction(Stock stock, int noStocks) {		    
			if(tips.containsKey(stock.getName())) {
				
				Tip stockTip = tips.get(stock.getName());
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
				Date date1 = null;
				Date date2 = null;
						
				try {
					date1 = sdf.parse(today);
					date2 = sdf.parse(stockTip.getDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			
				if(date2.after(date1)) {					
					System.out.println(stock.getName() + " recebeu uma dica valida!");
					System.out.println(" I will buy a stock");
					
					if(stockTip.getType().equals("max") && Math.random() <=  GOOD_AGENTS_MAX_BUY_ACTION_PROB) {
						noStocks = getNoStocks(stock.getValue(), stockTip.getStockValue(), true);
						System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will buy: " +  noStocks + " stocks");
						buyStocks(stock.getName(), stock.getValue(), noStocks);
	
					} else if(stockTip.getType().equals("min") && Math.random() <=  GOOD_AGENTS_MIN_BUY_ACTION_PROB) {
						noStocks = getNoStocks(stock.getValue(), stockTip.getStockValue(), false);
						System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will buy: " +  noStocks + " stocks");

						buyStocks(stock.getName(), stock.getValue(), noStocks);
					}
					System.out.println("I am " +  myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash);
				}
			} else return getProb(stock);
			
			// TODO :
			// if dica && dica.date < today:
			// max - 0.1
			// min - 0.9
			// else :
			// return getProb(stock)
			
			return noStocks;
		}
		
		private int sellAction(Stock stock, int noStocks) {		
			if(tips.containsKey(stock.getName())) {
				Tip stockTip = tips.get(stock.getName());
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
				Date date1 = null;
				Date date2 = null;
						
				try {
					date1 = sdf.parse(today);
					date2 = sdf.parse(stockTip.getDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			
				if(date1.after(date2)) {
					System.out.println(stock.getName() + " recebeu uma dica valida!");

					if(stockTip.type.equals("max") && Math.random() <=  GOOD_AGENTS_MAX_SELL_ACTION_PROB) {
						noStocks = getNoStocks(stock.getValue(), stockTip.getStockValue(), true);
						System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will sell: " +  noStocks + " stocks");

						sellStocks(stock.getName(), stock.getValue(), noStocks);
					} else if(stockTip.type.equals("min") && Math.random() <=  GOOD_AGENTS_MIN_SELL_ACTION_PROB) {
						noStocks = getNoStocks(stock.getValue(), stockTip.getStockValue(), false);						
						System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will sell: " +  noStocks + " stocks");

						sellStocks(stock.getName(), stock.getValue(), noStocks);
					}
					System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash  + " ; SavedValue: " + stocksOwned.get(stock).getSavedValue());
				}
			} else return getProb(stock);
			
			// TODO:			
			// if dicas && dica.date < today:
			// max - 0.1
			// min - 0.9
			// else :
			// return getProb(stock)
			
			return noStocks;
		}
		
		private int getProb(Stock stock) {
			// TODO:			
			if(Math.random() < 0.5)
				return 1;
			return -1;
		}
		
		private int getNoStocks(double actualPrice, double stockTip, boolean type) {
			int maxStocks = ((int)(cash/actualPrice));
			int noStocks = -1;
			double elapsedValue = 0.0;
			
			if(type) {		// tip is a maximum value (most likely will sell stocks)
				elapsedValue = (stockTip - actualPrice)/actualPrice;
			} else {		// tip is a minimum value (most likely will buy stocks)
				elapsedValue = (actualPrice - stockTip)/stockTip;
			}
			
			noStocks = ((int)(5*elapsedValue*maxStocks));
			
			if(noStocks < 1) {
				if(maxStocks > 5) {
					noStocks = 5;
				} else noStocks = maxStocks;
			} else if(noStocks > maxStocks) {
				noStocks = ((int)(0.5*maxStocks));
			}

			return noStocks;
		}	
	}
}
