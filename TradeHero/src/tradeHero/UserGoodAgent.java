package tradeHero;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.domain.DFService;
import structures.Stock;
import structures.Tip;
import structures.randomCalc;
import tradeHero.behaviours.FollowingServer;
import tradeHero.behaviours.ReceivePayments;
import tradeHero.behaviours.ReceiveStockUpdate;
import tradeHero.behaviours.ReceiveTip;

public class UserGoodAgent extends UserAgent {	
	public static final double GOOD_AGENTS_MAX_BUY_ACTION_PROB = 0.1;
	public static final double GOOD_AGENTS_MAX_SELL_ACTION_PROB = 0.9;

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
		addBehaviour(new FollowingServer(this));
		addBehaviour(new ReceivePayments(this));
		
	}
	
	
	class ReceiveStockUpdateAgent extends ReceiveStockUpdate {
		private static final long serialVersionUID = 1L;

		ReceiveStockUpdateAgent(UserAgent agent) {
			super(agent);
		}

		@Override
		public void buyerAction() {
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
						s += "buy&" + stock.getName() + "&" + noStocks  +  "&" + stock.getValue() + "\n" ;
						buyStocks = true;
					}
				}
				
				if(getStocksOwned().containsKey(stock.getName()) && (!buyStocks || Math.random() < 0.3)) {
					if((noStocks = sellAction(stock, noStocks)) > -1){
						s += "sell&" + stock.getName() + "&" + noStocks  + "&" + stock.getValue() + "\n";
						buyStocks = true;
					}
				}
				
				if(!buyStocks) {
					s += getProb(stock);
				}
			}
			
			if(!s.equals("")) {
				alertFollowers(s);
				
			}
			updateGain(gains(stocksPrice));
		}
		
		private int buyAction(Stock stock, int noStocks) {			
			if(scheduled_tips.containsKey(stock.getName())) {
				Tip stockTip = scheduled_tips.get(stock.getName());
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
				Date date1 = null;
				Date date2 = null;
				
				try {
					date1 = sdf.parse(today);
					date2 = sdf.parse(stockTip.getDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				if(stockTip.getType().equals("min") && ((date1.after(date2)) || (date1.equals(date2)))) {
					System.out.println(" I will buy a stock");

					int maxStocks = ((int)(cash/stock.getValue()));
					System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will buy: " +  maxStocks + " stocks of value " + stock.getValue());

					buyStocks(stock.getName(), stock.getValue(), maxStocks);
					System.out.println("I am " +  myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash);
				}
				
			} else if(tips.containsKey(stock.getName())) {
				Tip stockTip = tips.get(stock.getName());
				scheduled_tips.put(stock.getName(), stockTip);
				
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
					System.out.println("");
					System.out.println(getLocalName() + " recebeu uma dica valida!");
					
					if(Math.random() <= GOOD_AGENTS_MAX_BUY_ACTION_PROB) {
						if(stockTip.getType().equals("max")) {
							System.out.println(" I will buy a stock");
							noStocks = getNoStocks(stock.getValue(), stock, true, true);
							System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will buy: " +  noStocks + " stocks of value " + stock.getValue());
							
							buyStocks(stock.getName(), stock.getValue(), noStocks);
							
							System.out.println("I am " +  myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash);
						} else return -1;
					} else if(stockTip.getType().equals("min")) {
						System.out.println(" I will buy a stock");
						noStocks = getNoStocks(stock.getValue(), stock, false, true);
						System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will buy: " +  noStocks + " stocks of value " + stock.getValue());

						buyStocks(stock.getName(), stock.getValue(), noStocks);
						
						System.out.println("I am " +  myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash);
					} else return -1;
				} else return -1;
			} else return -1;
			
			return noStocks;
		}
		
		private int sellAction(Stock stock, int noStocks) {	
			noStocks = -1;
				
			if(scheduled_tips.containsKey(stock.getName())) {
				Tip stockTip = scheduled_tips.get(stock.getName());
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
				Date date1 = null;
				Date date2 = null;
				
				try {
					date1 = sdf.parse(today);
					date2 = sdf.parse(stockTip.getDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if(stockTip.getType().equals("max") && ((date1.after(date2)) || (date1.equals(date2)))) {
					int stocksLeft = -1;
					for(Map.Entry<String, Stock> it : stocksOwned.entrySet()) {
						if(it.getKey().equals(stockTip.getName())) {
							stocksLeft = it.getValue().getQuantity();
							
						}
					}

					System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will sell: " +  stocksLeft + " stocks of value " + stock.getValue());

					sellStocks(stock.getName(), stock.getValue(), stocksLeft);
					System.out.println("I am " + getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash  + " ; SavedValue: " + stocksOwned.get(stock.getName()).getSavedValue());

				}
				
			} else if(tips.containsKey(stock.getName())) {
				Tip stockTip = tips.get(stock.getName());
				scheduled_tips.put(stock.getName(), stockTip);

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
					System.out.println(getLocalName() + " recebeu uma dica valida!");

					if(Math.random() <= GOOD_AGENTS_MAX_SELL_ACTION_PROB) {
						if(stockTip.type.equals("max")) {
							noStocks = getNoStocks(stock.getValue(), stock, true, false);
							System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will sell: " +  noStocks + " stocks of value " + stock.getValue());
	
							sellStocks(stock.getName(), stock.getValue(), noStocks);
							
							System.out.println("I am " + getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash  + " ; SavedValue: " + stocksOwned.get(stock.getName()).getSavedValue());
						} else return -1;
					} else if(stockTip.type.equals("min")) {
						noStocks = getNoStocks(stock.getValue(), stock, false, false);						
						System.out.println("I am " + myAgent.getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash + " ; I will sell: " +  noStocks + " stocks of value " + stock.getValue());

						sellStocks(stock.getName(), stock.getValue(), noStocks);
						
						System.out.println("I am " + getLocalName() + " ; I have: " + ((UserAgent)myAgent).cash  + " ; SavedValue: " + stocksOwned.get(stock.getName()).getSavedValue());

					} else return -1;

				} else return -1;
			} else return -1;
			
			return noStocks;
		}
		
		private int getNoStocks(double actualPrice, Stock stockTip, boolean type, boolean buy) {
			int noStocks = -1;
			double elapsedValue = 0.0;
			double stockTipValue = stockTip.getValue();
			
			if(type) {		// tip is a maximum value (most likely will sell stocks)
				elapsedValue = (stockTipValue - actualPrice)/actualPrice;
			} else {		// tip is a minimum value (most likely will buy stocks)
				elapsedValue = (actualPrice - stockTipValue)/stockTipValue;
			}
			
			if(buy) {
				int maxStocks = ((int)(cash/actualPrice));
				
				noStocks = ((int)(5*elapsedValue*maxStocks));
				
				if(noStocks < 1) {
					if(maxStocks > 5) {
						noStocks = 5;
					} else noStocks = maxStocks;
				} else if(noStocks > maxStocks) {
					noStocks = ((int)(0.5*maxStocks));
				}

			} else {
				int stocksLeft = -1;
				for(Map.Entry<String, Stock> it : stocksOwned.entrySet()) {
					if(it.getKey().equals(stockTip.getName())) {
						stocksLeft = it.getValue().getQuantity();
						
					}
				}
				noStocks = ((int)(5*elapsedValue*stocksLeft));
				
				if(noStocks < 1) {
					if(stocksLeft > 5) {
						noStocks = 5;
					} else noStocks = stocksLeft;
				} else if(noStocks > stocksLeft) {
					noStocks = ((int)(0.5*stocksLeft));
				}
			}
			return noStocks;
		}	
	}
}