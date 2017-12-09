package tradeHero;

import java.util.ArrayList;

import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import sajas.domain.DFService;
import structures.Rank;
import structures.randomCalc;
import tradeHero.behaviours.UpdateStokes;
import tradeHero.behaviours.RankingRequest;
import tradeHero.behaviours.UpdateRanking;

public class Market extends Agent {
	
	private AID[] stokeAgents;
	private AID[] userAgents;

	
	private ArrayList<Rank> ranking = new ArrayList<Rank>();
	
	
	
	public static Market market = null;
	
	Market() throws Exception { 
		super();
		if(market != null)
			throw new Exception("Only one market can be created");
		 
		market = this;
	}
	
	protected void setup() {
		System.out.println("Hallo! Market-agent "+getAID().getName()+" is ready.");
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("market");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		
		addBehaviour(new TickerBehaviour(this, randomCalc.DAY_PERIOD*1000) {
			protected void onTick() {
				 System.out.println("-----------------------------------------------------------------------------------------------------------");
				// Update the list of seller agents
				DFAgentDescription template = new DFAgentDescription();
				DFAgentDescription template2 = new DFAgentDescription();
				
				ServiceDescription sd = new ServiceDescription();
				sd.setType("stock");
				template.addServices(sd);
				
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType("buyers");
				template2.addServices(sd2);
				
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					//System.out.println("Market-agent "+getAID().getName()+":"+"Found the following stoke agents:");
					stokeAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						stokeAgents[i] = result[i].getName();
						//System.out.println(stokeAgents[i].getName());
					}
					
					result = DFService.search(myAgent, template2);
					//System.out.println("Market-agent "+getAID().getName()+":"+"Found the following user agents:");
					userAgents = new AID[result.length];
					for(int i = 0; i < result.length; ++i) {
						userAgents[i] = result[i].getName();
						//System.out.println(userAgents[i].getName());
					}
					
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}

				// Perform the request
				myAgent.addBehaviour(new UpdateStokes(market));
				
			}
		} );
		
		addBehaviour(new UpdateRanking(this));
		addBehaviour(new RankingRequest(this));
		
	}
	
	
	// Put agent clean-up operations here
	protected void takeDown() {
	// Printout a dismissal message
		System.out.println("Market-agent "+getAID().getName()+" terminating.");
	}
	

	public ArrayList<Rank> getRanking() {
		// TODO Auto-generated method stub
		return ranking;
	}

	public AID[] getStokeAgents() {
		// TODO Auto-generated method stub
		return this.stokeAgents;
	}

	public AID[] getUserAgents() {
		// TODO Auto-generated method stub
		return this.userAgents;
	}

	
	

}
