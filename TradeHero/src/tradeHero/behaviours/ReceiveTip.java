package tradeHero.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import tradeHero.UserGoodAgent;

public class ReceiveTip extends CyclicBehaviour{
	private static final long serialVersionUID = 1L;
	
	private UserGoodAgent goodAgent;
	
	public ReceiveTip(UserGoodAgent uga) {
		super();
		goodAgent = uga;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub

		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("stoke-tip"));
				
		ACLMessage msg = myAgent.receive(mt);
		
		if(msg != null) {
			/* Tratamento da mensagem */
			System.out.println("[ReceiveTip] I am a good user " + myAgent.getLocalName() + "and I received: " + msg.getContent()); 	// stoke tip received from stock agents 	
			goodAgent.addTip(msg);
		}else {
			block();
		}
		
	}
	
}