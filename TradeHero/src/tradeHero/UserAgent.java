package tradeHero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sajas.core.Agent;

public class UserAgent extends Agent {
	/*
	 * 	Agent agent = new Agent();
		agent.getAID();
	 */
	private double cash = 100000.0;												// quantidade de dinheiro que o utilizador tem no inicio
	private int followers;														// numero de seguidores determina quanto o utilizador vai receber de premiacao
	private double gain_rate;													// media de ganhos
	private ArrayList<Agent> following = new ArrayList<Agent>();				// se o utilizador seguir alguem, vai receber "dicas" de investimento desse utilizador
	private Map<Agent, Integer> stocksOwned = new HashMap<Agent, Integer>();	// numero de stocks possuídos e de que empresas foram comprados
	
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

	public ArrayList<Agent> getFollowing() {
		return following;
	}

	public void setFollowing(ArrayList<Agent> following) {
		this.following = following;
	}

	public double getGain_rate() {
		return gain_rate;
	}

	public void setGain_rate(double gain_rate) {
		this.gain_rate = gain_rate;
	}

	public Map<Agent, Integer> getStocksOwned() {
		return stocksOwned;
	}

	public void setStocksOwned(Map<Agent, Integer> stocks_owned) {
		this.stocksOwned = stocks_owned;
	}

}
