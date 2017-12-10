package tradeHero.behaviours;

import java.util.ArrayList;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import structures.Rank;
import tradeHero.Market;

public class RankingRequest extends CyclicBehaviour {
	private static final long serialVersionUID = 1L;
	
	private Market market;
	
	public RankingRequest(Market market) {
		super();
		this.market = market;
	}
	
	
	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId("ranking"));
		ACLMessage msg = myAgent.receive(mt);			
		
		ArrayList<Rank> ranking = market.getRanking();
		
		if(msg != null) {
			String s = "";
			
			for( int i = 0; i < 10 && i < ranking.size() ; i++) {
				s += ranking.get(i).aid + "\n";	
				
			}
			
			System.out.println("\n\n An user requested the ranking info \n\n");
			
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			reply.setConversationId("ranking");
			reply.setContent(s);
			myAgent.send(reply);
			
		}else {
			block();
		}

	}
	
}