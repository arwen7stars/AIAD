package tradeHero.behaviours;

import java.util.ArrayList;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.Behaviour;
import structures.randomCalc;
import tradeHero.Market;

public class UpdateStokes extends Behaviour {
	private static final long serialVersionUID = 1L;
	
	private AID[] stokeAgents;
	private AID[] userAgents;
	
	private int step = 0;
	private MessageTemplate mt;
	private int repliesCnt = 0;
	private ArrayList<String> stokeValues = new ArrayList<String>();
	private Market This;
	
	public UpdateStokes(Market agent) {
		super();
		This = agent;
		stokeAgents = This.getStokeAgents();
		userAgents = This.getUserAgents();
	}
	
	
	@Override
	public void action() {	
		switch(step) {
		case 0:
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			
			for(int i = 0; i <  stokeAgents.length; i++) {
				request.addReceiver( stokeAgents[i]);
				
			}
			
			System.out.println("     [UpdateStokes] Market day -> today: " + randomCalc.today());
			request.setContent(randomCalc.today());
			request.setConversationId("stoke-value");
			request.setReplyWith("request" + System.currentTimeMillis());
			myAgent.send(request);
			
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("stoke-value"), MessageTemplate.MatchInReplyTo(request.getReplyWith()));
			
			step = 1;
			
			System.out.println("     [UpdateStokes] I sent the message to: " + stokeAgents.length + " stoke agents.");
			
			break;			
		
		
		case 1:
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.INFORM) {
					String stokePrice = reply.getContent(); // stockName&value
					stokeValues.add(stokePrice);
					repliesCnt++;
					
					System.out.println("     [UpdateStokes] Market received: " + stokePrice);
					
					if(repliesCnt >= stokeAgents.length) {
						step = 2;
					}
				}	
			}else {
				block();
			}
			
			break;
		case 2:
			String msg = "";
			msg += randomCalc.today() + "\n";
			ACLMessage informUsers = new ACLMessage(ACLMessage.INFORM);
			for(int i = 0; i < userAgents.length; i++) {
				informUsers.addReceiver(userAgents[i]);
			}
			
			for(int i = 0; i < stokeValues.size(); i++) {
				msg += stokeValues.get(i) + "\n";
			}
			
			informUsers.setContent(msg);
			informUsers.setConversationId("stoke-value");
			informUsers.setReplyWith("request" + System.currentTimeMillis());
			myAgent.send(informUsers);
			step = 3;
			break;
		}
	}

	@Override
	public boolean done() {
		if(step != 3) {
			
			return false;
		}
		randomCalc.nextDay();
		System.out.println("     [UpdateStokes] Finished for this day. Preparing for next day.");
		System.out.println("");
		return true;
	}
	
}