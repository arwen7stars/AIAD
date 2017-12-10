package tradeHero.behaviours;

import java.util.ArrayList;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.AID;
import sajas.core.behaviours.SimpleBehaviour;
import sajas.domain.DFService;
import structures.randomCalc;
import tradeHero.UserNormalAgent;

public class InicializeFollowing extends SimpleBehaviour {
	private static final long serialVersionUID = 1L;
	
	private int step = 0;
	private MessageTemplate mt;
	private ArrayList<String> Following;
	
	public InicializeFollowing(UserNormalAgent usa) {
		super();
		Following = usa.getFollowing();
	}
	
	@Override
	public void action() {
		switch(step) {
		case 0:			// request ranking to Market
			
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("market");
			template.addServices(sd);
			
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(result[0].getName());
				 
				msg.setConversationId("ranking");

				myAgent.send(msg);
							
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mt = MessageTemplate.MatchConversationId("ranking");
			step = 1;
			break;	
		case 1:			// decide wich users should be following
			
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null) {
				if(reply.getPerformative() == ACLMessage.INFORM) {
					
					String parts[] = reply.getContent().split("\n");
					 
					if(parts.length != 0) { 
						Following.add(parts[0]);
					}else {
						step = 3;
						break;
					}
					
					for(int i = 1; i < parts.length; i++) {
						if(Math.random() < randomCalc.FOLLOWING_USER_PROB) {
							Following.add(parts[i]);
						}
					}
					
					step = 2;
				}
				
				
			}else {
				block();
			}
			
			
			break;
		case 2:			// inform agents that are being followed
			
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("following");
			for(int i = 0; i < Following.size(); i++) {
				msg.addReceiver(new AID(Following.get(i), AID.ISLOCALNAME));
				System.out.println("Im " + myAgent.getLocalName() + " and I will be following " + Following.get(i));
			}
			msg.setContent("add&" + myAgent.getLocalName());
			
			myAgent.send(msg);
			step = 3;
			break;
			
		}
	}

	@Override
	public boolean done() {
		return (step == 3);
	}
	
}
