package tradeHero;

import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Receiver extends Agent 
{
	private static final long serialVersionUID = 1L;
	
	public Receiver() {}

	@Override
	public void setup() 
    {        
		addBehaviour(new ResultsListener());
    }
	
	private class ResultsListener extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;

		@Override
        public void action() 
        {
			ACLMessage inform = myAgent.receive();
			if (inform!=null)
               System.out.println( " - " + myAgent.getLocalName() + " <- " + inform.getContent() );
			block();
        }
    }
}