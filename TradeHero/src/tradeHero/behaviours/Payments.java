package tradeHero.behaviours;

import java.util.ArrayList;

import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.AID;
import sajas.core.behaviours.Behaviour;
import sajas.domain.DFService;
import structures.randomCalc;
import tradeHero.UserNormalAgent;


/* Left TODO: The normal agent decides if still wants to follow another user  */
public class Payments extends Behaviour {
	private static final long serialVersionUID = 1L;
	
	private int step = 0;
	private UserNormalAgent normalAgent;
	private ArrayList<String> following;
	private MessageTemplate mt;
	private boolean myAgent_is_top = false;
	ArrayList<String> outOfRank = new ArrayList<String>();
	ArrayList<String> newsInRank = new ArrayList<String>();
	
	public Payments(UserNormalAgent usa) {
		super();
		normalAgent = usa;
		following = usa.getFollowing();
	}
	
	@Override
	public void action() {
		
		double cash = normalAgent.getCash();
		
		switch(step) {
		case 0:
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setConversationId("payment");
			for(int i = 0; i < following.size(); i++) {
				msg.addReceiver(new AID(following.get(i), AID.ISLOCALNAME));
				System.out.println("[PAYMENTS] Im " + myAgent.getLocalName() + " and I will send a payment to" + following.get(i)  );
			}
			myAgent.send(msg);
			
			
			cash -= following.size() * randomCalc.PAYMENT_VALUE;
			normalAgent.setCash(cash);
			 
			step = 1;				
			
			break;
	 	case 1:
			/* TODO: Ask for the ranking */
	 		DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("market");
			template.addServices(sd);
			
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				ACLMessage mseg = new ACLMessage(ACLMessage.REQUEST);
				mseg.addReceiver(result[0].getName());
				 
				mseg.setConversationId("ranking");

				myAgent.send(mseg);
							
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mt = MessageTemplate.MatchConversationId("ranking");
			step = 2; 		
	 		
	 		break;
	 	case 2:
	 		ACLMessage reply = myAgent.receive(mt);
	 		
	 		
			
	 		if(reply != null) {
				if(reply.getPerformative() == ACLMessage.INFORM) {
					
					String parts[] = reply.getContent().split("\n");
					
					
					for(int i = 0; i < parts.length; i++) {
							newsInRank.add(parts[i]);
							if(parts[i].equals(myAgent.getLocalName()))
								myAgent_is_top=true;						
					}
					System.out.println("[RANKING UPDATE] : Following: " + following.toString() );
					for(int i = 0; i < following.size(); i++) {
						if(newsInRank.indexOf(following.get(i)) < 0) {
							outOfRank.add(new String(following.get(i)));
							following.remove(i);
							i--;
						}
							
					}
					
					
					System.out.println("[RANKING UPDATE] : New rank: " + newsInRank.toString() );
					System.out.println("[RANKING UPDATE] : Followings that are not in rank: " + outOfRank.toString() );
					step = 3;
				}
				
				
			}else {
				block();
			}
	 		break;
	 	case 3:
	 		ACLMessage msgs = new ACLMessage(ACLMessage.INFORM);
	 		msgs.setConversationId("following");
			for(int i = 0; i < outOfRank.size(); i++) {
				msgs.addReceiver(new AID(outOfRank.get(i), AID.ISLOCALNAME));
				System.out.println("Im " + myAgent.getLocalName() + " and I will not be following " + outOfRank.get(i));
			}
			msgs.setContent("remove&" + myAgent.getLocalName());
			
			myAgent.send(msgs);
			
			
			
	 		if((myAgent_is_top && Math.random() < 0.1) || (!myAgent_is_top && Math.random() < 0.2) || following.size() == 0) {
	 			ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
		 		msg2.setConversationId("following");
				for(int i = 0; i < newsInRank.size(); i++) {
					if(Math.random() < randomCalc.FOLLOWING_USER_PROB) {
						msg2.addReceiver(new AID(newsInRank.get(i), AID.ISLOCALNAME));
						following.add(newsInRank.get(i));
						System.out.println("I am " + myAgent.getLocalName() + " and I will be following " + newsInRank.get(i));
					}
				}
				msg2.setContent("add&" + myAgent.getLocalName());
				
				myAgent.send(msg2);
	 		}
	 		
	 		
	 		step = 4;
	 		break;
		}
					
	}

	@Override
	public boolean done() {
		return step == 4;
	}
	
}
