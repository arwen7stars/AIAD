package tradeHero.behaviours;

import java.util.ArrayList;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import structures.Stock;
import tradeHero.UserGoodAgent;

public class ReceiveTip extends CyclicBehaviour{
	
	private UserGoodAgent This;
	
	public ReceiveTip(UserGoodAgent uga) {
		super();
		This = uga;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub

		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("stoke-tip"));
				
		ACLMessage msg = myAgent.receive(mt);
		
		
		
	
		if(msg != null) {
			
			System.out.print("I am a good user and I received: " + msg.getContent()); 	// stoke tip received from stock agents 
						
			
		}else {
			block();
		}
		
		
		
	}
}
