/* 
 * 	Common Behaviour to all UserAgents. This Behaviour Handles the information that a user is being followed by another one.
 * 	The agent that has this instance of the behaviour, when receives a message id = "following", means that another agent is following him
 * 	To do so, this behaviour waits for a message sent from another user. The content of this message is: [add|remove]&myAgent.getLocalName()
 * 	The first part of the message tells which operation is to perform (add a follower or remove a follower)
 *  The second part of the message is the localName of the agent that is being following this agent 
 **/



package tradeHero.behaviours;

import java.util.ArrayList;

import sajas.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.core.behaviours.CyclicBehaviour;
import tradeHero.UserAgent;

public class FollowingServer extends CyclicBehaviour{
	private static final long serialVersionUID = 1L;
	
	private UserAgent userAgent;
	
	public FollowingServer(UserAgent userAgent) {
		this.userAgent = userAgent;
	}
	
	@Override
	public void action() {
		// TODO Auto-generated method stub
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchConversationId("following"));
		ACLMessage msg = myAgent.receive(mt);
		
		if(msg != null) {
			
			System.out.println("      !!IMP!! FOLLOWING SERVER " + msg.getContent() + "!!!");
			
			String parts[] = msg.getContent().split("&");
			
			if(parts[0].equals("add")) {
				System.out.println("name of normal user: " + parts[1]);
				userAgent.addFollower(new AID(parts[1], AID.ISLOCALNAME));
				System.out.println("Im " + myAgent.getLocalName() + " and " + parts[1] + " wants to follow-me");
				
			}else if(parts[0].equals("remove")) {
				
				ArrayList<AID> followers = userAgent.getFollowers();
				
				for(int i = 0; i < followers.size(); i++) {
					if(followers.get(i).getLocalName().equals(parts[1])) 
						followers.remove(i);
				}
				
			}	
		} else {
			block();
		}
	}

}
