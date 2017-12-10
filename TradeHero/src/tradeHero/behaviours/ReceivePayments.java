/*
 * 	Common  behaviour to all agents. This Behaviour increments the cash due to the agent's followers payments 
 * 
 */

package tradeHero.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import structures.randomCalc;
import tradeHero.UserAgent;

public class ReceivePayments extends CyclicBehaviour {
	private static final long serialVersionUID = 1L;
	private UserAgent userAgent;
	
	public ReceivePayments(UserAgent us) {
		super();
		userAgent = us;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("payment"));
		ACLMessage msg = myAgent.receive(mt);
		
		if(msg != null) {
			System.out.println("[PAYMENTS] Im " + myAgent.getLocalName() + " and I received a payment ; My cash was: " + userAgent.getCash());
			userAgent.increaseCash(randomCalc.PAYMENT_VALUE);
			System.out.println("[PAYMENTS] Im " + myAgent.getLocalName() + " and my cash now is: " + userAgent.getCash());
			
		}
	}
	
}
