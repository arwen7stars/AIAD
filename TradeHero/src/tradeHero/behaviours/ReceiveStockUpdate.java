package tradeHero.behaviours;

import java.util.ArrayList;

import bsh.This;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.AID;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.domain.DFService;
import structures.Stock;
import tradeHero.UserAgent;

public abstract class ReceiveStockUpdate extends CyclicBehaviour {

	protected ArrayList<Stock> stocksPrice;
	protected String today = "";
	private UserAgent myUserAgent = null;
	
	protected ReceiveStockUpdate(UserAgent agent){
		super();
		this.myUserAgent = agent;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub

		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("stoke-value"));
				
		ACLMessage msg = myAgent.receive(mt);
				
	
		if(msg != null) {
			
			System.out.print("I am an user and I received: " + msg.getContent()); 	// stoke value received from market agent 
			
			
			stocksPrice = new ArrayList<Stock>();
			String[] parts = msg.getContent().split("\n");
			
			today = parts[0];			
			for(int i = 1; i < parts.length; i++) {
				String[] stock = parts[i].split("&");
				if(stock.length == 2)
					stocksPrice.add(new Stock(stock[0], stock[1]));
				
			}
			
			buyerAction();	
			
		}else {
			block();
		}
		
		
		
	}
	
	public abstract void buyerAction();
	
	/* Informs followers about today's purchases/sales */
	public void alertFollowers(String s) {
		/* Common to all users */
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		ArrayList<AID> followers = myUserAgent.getFollowers();
		
		System.out.println("I, " + myAgent.getLocalName() + ", will try to send to my followers: " + s);
		
		if(followers.size() == 0)
			return;
		
		for(int i = 0; i <  followers.size(); i++) {
			msg.addReceiver( followers.get(i));
			
		}
		
		msg.setContent(s);
		msg.setConversationId("follow-tip");
		
		myAgent.send(msg);
	}
	
	/* Informs Market  */
	public void updateGain(double gain) {
		/* Common to all users */
		 
		DFAgentDescription template = new DFAgentDescription();
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("market");
		template.addServices(sd);
		
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(result[0].getName());
			msg.setContent(myAgent.getLocalName() + "&" + gain);
			msg.setConversationId("gains");
			System.out.println("I," + myAgent.getLocalName() +  ", update my gains with the following message: " + msg.getContent() );
			myAgent.send(msg);
						
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	
	
	
}
