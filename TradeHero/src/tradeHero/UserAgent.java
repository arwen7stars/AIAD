package tradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jade.lang.acl.ACLMessage;
import sajas.core.AID;
import sajas.core.Agent;
import structures.Stock;
import structures.Tip;

public class UserAgent extends Agent {
	/*
	 * 	Agent agent = new Agent();
		agent.getAID();
	 */
	protected double cash = 100000.0;											// quantidade de dinheiro que o utilizador tem no inicio
	protected double gain_rate = 0;													// media de ganhos
	protected ArrayList<AID> followers = new ArrayList<AID>();				    // se o utilizador seguir alguem, vai receber "dicas" de investimento desse utilizador
	protected Map<String, Stock> stocksOwned = new HashMap<String, Stock>();	// numero de stocks possu�dos e de que empresas foram comprados
	protected Map<String, Tip> tips = new HashMap<String, Tip>();
	protected Map<String, Tip> scheduled_tips = new HashMap<String, Tip>();
	private Map<String, Double> ROC = new HashMap<String, Double>();
	
	public static  ArrayList<Stock> stocksPrice;
	
	public UserAgent() {}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	

	public ArrayList<AID> getFollowers() {
		return followers;
	}

	public void setFollowers(ArrayList<AID> following) {
		this.followers = following;
	}
	
		

	public double getGain_rate() {
		return gain_rate;
	}

	public void setGain_rate(double gain_rate) {
		this.gain_rate = gain_rate;
	}

	public Map<String, Stock> getStocksOwned() {
		return stocksOwned;
	}

	public void setStocksOwned(Map<String, Stock> stocks_owned) {
		this.stocksOwned = stocks_owned;
	}
	
	public void buyStocks(String stock, double value, Integer noStocks) {
		
		Integer total = 0;
		double boughtValue = noStocks * value;
		
			
		if(cash >= boughtValue) {
			cash -= boughtValue;
			
			if(stocksOwned.containsKey(stock)) {
				total = stocksOwned.get(stock).getQuantity();
				double savedValue = stocksOwned.get(stock).getSavedValue();
				if(savedValue < value)
					savedValue = value;
				stocksOwned.put(stock, new Stock(stock, total + noStocks, savedValue));
						
			}else {
				stocksOwned.put(stock, new Stock(stock, noStocks, value));
						
			}		
			
		}	
		
						
	}

	public void sellStocks(String stock, double value, Integer noStocks) {
		
		
		
		// atualizar dinheiro do utilizador
	    for(Map.Entry<String, Stock> it : stocksOwned.entrySet()) {
	        
	        if(it.getKey().equals(stock)) {
	        	
	        	int stocksLeft = it.getValue().getQuantity() - noStocks;
	        	cash += noStocks * value;
	        	
	        	if(stocksLeft < 0)						// o utilizador nao tem tantas ações quanto as que quer vender 
	        		return;
	        	if(stocksLeft == 0) {
	        		stocksOwned.remove(it);
	        		return;
	        	}
	        	it.setValue(new Stock(stock, stocksLeft, it.getValue().getSavedValue()));
	        	
	        	
	        }
	       }
	}
	
	
	public double gains(ArrayList<Stock> stocksValueToday) {
		
		double total = 0.0;
		
		for(Stock stock :  stocksValueToday) {
			
			String s = stock.getName();
			
			if(stocksOwned.containsKey(s))
				total += stocksOwned.get(s).getQuantity()*stock.getValue();			
			
		}
		
		
		return total + this.cash;
	}

	public void addFollower(AID aid) {
		// TODO Auto-generated method stub
		followers.add(aid);
	}

	public void increaseCash(double paymentValue) {
		// TODO Auto-generated method stub
		cash += paymentValue;
	}
	
	
	public void updateRoc(String name, Double value) {
		ROC.put(name, value);
	}
	
	public Double getRocValue(String name) {
		if(ROC.containsKey(name))		
			return ROC.get(name);
		return 0.0;
	}
	
	
	public void addTip(ACLMessage msg) {

		String[] parts = msg.getContent().split("&");
		String name = parts[0];
		String type = parts[1];
		String date  = parts[2];
		double value = Double.parseDouble(parts[3]);
		
		Tip receivedTip = new Tip(name, type, date, value);

		tips.put(name, receivedTip);
	}
	
		
	public String followersToString() {
		String s = "";
		for(int i = 0; i < followers.size(); i++)
			s += followers.get(i).getLocalName() + "\n";
		
		return s == "" ? "none" : s;
	}
		
	public int getNoFollowers() {
		return followers.size();
	}
	
	
}
