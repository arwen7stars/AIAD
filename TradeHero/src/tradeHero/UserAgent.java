package tradeHero;

import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;
import sajas.domain.DFService;
import sajas.proto.SSContractNetResponder;
import sajas.proto.SSIteratedAchieveREResponder;
import sajas.proto.SSResponderDispatcher;
import tradeHero.StockAgent.Stock;

public abstract class UserAgent extends Agent {
	/*
	 * 	Agent agent = new Agent();
		agent.getAID();
	 */
	private double cash = 100000.0;												// quantidade de dinheiro que o utilizador tem no inicio
	private int followers;														// numero de seguidores determina quanto o utilizador vai receber de premiacao
	private double gainRate;													// media de ganhos
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

	public double getGainRate() {
		return gainRate;
	}

	public void setGainRate(double gainRate) {
		this.gainRate = gainRate;
	}

	public Map<Agent, Integer> getStocksOwned() {
		return stocksOwned;
	}

	public void setStocksOwned(Map<Agent, Integer> stocks_owned) {
		this.stocksOwned = stocks_owned;
	}
	
	public void buyStocks(StockAgent market, Integer noStocks) {
		Stock actualStock = market.getActualStockValue().stock;
		double value = actualStock.value;
		double boughtValue = noStocks * value;
		
		cash = cash - boughtValue;						// atualizar dinheiro do utilizador
		stocksOwned.put(market, noStocks);				// atualizar array de stocks que possuí no momento
	}

	public void sellStocks(StockAgent market, Integer noStocks) {
		Stock actualStock = market.getActualStockValue().stock;
		double value = actualStock.value;
		double soldValue = noStocks * value;
		
		cash = cash + soldValue;						// atualizar dinheiro do utilizador
	    for(Iterator<Map.Entry<Agent, Integer>> it = stocksOwned.entrySet().iterator(); it.hasNext(); ) {
	        Map.Entry<Agent, Integer> entry = it.next();
	        if(entry.getKey().equals(market)) {
	          it.remove();
	        }
	        }
	}
	
	public void commonSetup() {
		// register provider at DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addProtocols(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName() + "-trade-hero");
		sd.setType("trade-hero");
		dfd.addServices(sd);
		
		// System.out.println(getLocalName());		// displays agent name
		
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(e.getMessage());
		}
		
		// behaviours
		//addBehaviour(new CNetResponderDispatcher(this));
		//addBehaviour(new RequestResponderDispatcher(this));
	}
	
	private class CNetResponderDispatcher extends SSResponderDispatcher {

		private static final long serialVersionUID = 1L;

		public CNetResponderDispatcher(Agent agent) {
			super(agent, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET));
		}

		@Override
		protected Behaviour createResponder(ACLMessage cfp) {
			return new CNetResp(myAgent, cfp);
		}

	}
	
	private class CNetResp extends SSContractNetResponder {

		private static final long serialVersionUID = 1L;

		private boolean expectedSuccessfulExecution;
		
		public CNetResp(Agent a, ACLMessage cfp) {
			super(a, cfp);
		}

		@Override
		protected ACLMessage handleCfp(ACLMessage cfp) {
			ACLMessage reply = cfp.createReply();

			reply.setPerformative(ACLMessage.PROPOSE);

			return reply;
		}
		
		@Override
		protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
			ACLMessage result = accept.createReply();
			
			// random service execution
			if(expectedSuccessfulExecution) {
				result.setPerformative(ACLMessage.INFORM);
			} else {
				result.setPerformative(ACLMessage.FAILURE);
			}
			
			return result;
		}
		
		@Override
		protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {}

	}
	
	private class RequestResponderDispatcher extends SSResponderDispatcher {

		private static final long serialVersionUID = 1L;

		public RequestResponderDispatcher(Agent agent) {
			super(agent, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET));
		}

		@Override
		protected Behaviour createResponder(ACLMessage request) {
			return new RequestResp(myAgent, request);
		}

	}
	
	private class RequestResp extends SSIteratedAchieveREResponder {

		private static final long serialVersionUID = 1L;

		public RequestResp(Agent a, ACLMessage request) {
			super(a, request);
		}

		@Override
		protected ACLMessage handleRequest(ACLMessage request) {
			ACLMessage reply = request.createReply();
			
			return reply;
		}
	}
}
