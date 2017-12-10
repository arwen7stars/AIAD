/* Market receives the gains of all users in order to update his ranking */


package tradeHero.behaviours;

import java.util.ArrayList;
import java.util.Collections;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import structures.Rank;
import tradeHero.Market;

public class UpdateRanking extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	
	private Market market;
	
	public UpdateRanking(Market market) {
		super();
		this.market = market;
	}
	
	@Override
	public void action() {
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("gains"));
				
		ACLMessage msg = myAgent.receive(mt);
		
		ArrayList<Rank> ranking = market.getRanking();
		
		
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
			
			System.out.println("Ranking: " + ranking.size()  + " users ; " +  ranking.toString() );	
			
		}else {
			block();
		}			
	}		
}