package tradeHero;

import java.util.ArrayList;
import java.util.Collections;

import jade.core.AID;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.TickerBehaviour;
import sajas.domain.DFService;
import structures.Rank;
import structures.randomCalc;
import tradeHero.behaviours.UpdateStokes;

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
		
		
		addBehaviour(new TickerBehaviour(this, 5*1000) {
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
		
		addBehaviour(new updateRanking());
		
	}
	
	class updateRanking extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("gains"));
					
			ACLMessage msg = myAgent.receive(mt);
			
			if(msg != null) {
				
				String parts[] = msg.getContent().split("&");
				String localName = parts[0];
				Double value = Double.parseDouble(parts[1]);
				
				boolean duplicated = false;
				
				for(int i = 0; i < ranking.size(); i++) {
					if(ranking.get(i).aid.equals(localName) ) {
						ranking.get(i).gain = value;
						duplicated = true;
						break;
					}
				}
				
				if(!duplicated)				
					ranking.add(new Rank(localName, value));
				
				Collections.sort(ranking);
				
				System.out.println("ranking: " + ranking.size()  + " ; " +  ranking.toString() );
				 
				
				
			}else {
				block();
			}			
		}		
	}
	
	
	class rankingRequest extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchConversationId("ranking"));
			ACLMessage msg = myAgent.receive(mt);			
			
			if(msg != null) {
				String s = "";
				
				for( int i = 0; i < 10 && i < ranking.size() ; i++) {
					s += ranking.get(i).aid + "\n";	
					
				}
				
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("ranking");
				myAgent.send(reply);
				
			}
			
			
		}
		
	}
	
	
	
	// Put agent clean-up operations here
	protected void takeDown() {
	// Printout a dismissal message
		System.out.println("Market-agent "+getAID().getName()+" terminating.");
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
