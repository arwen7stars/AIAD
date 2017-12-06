package tradeHero;

import java.util.*;

import javax.media.j3d.Behavior;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.AID;
import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.domain.DFService;
import structures.Stock;
import tradeHero.behaviours.ReceiveStockUpdate;

public class UserAgent extends Agent {
	/*
	 * 	Agent agent = new Agent();
		agent.getAID();
	 */
	protected double cash = 100000.0;												// quantidade de dinheiro que o utilizador tem no inicio
	protected int followers;														// numero de seguidores determina quanto o utilizador vai receber de premiacao
	protected double gain_rate;														// media de ganhos
	protected ArrayList<AID> following = new ArrayList<AID>();				    	// se o utilizador seguir alguem, vai receber "dicas" de investimento desse utilizador
	protected Map<String, Stock> stocksOwned = new HashMap<String, Stock>();	// numero de stocks possu�dos e de que empresas foram comprados
	
	public UserAgent() {}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public ArrayList<AID> getFollowing() {
		return following;
	}

	public void setFollowing(ArrayList<AID> following) {
		this.following = following;
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
	
	
	
	
	
		
		

		
		
	
	
	
}
