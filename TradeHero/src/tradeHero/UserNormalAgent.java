package tradeHero;

import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import sajas.domain.DFService;

public class UserNormalAgent extends UserAgent {

	public UserNormalAgent() {
		super();
	}
	
	/*@Override
	public void setup() {
		// register provider at DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		dfd.addProtocols(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName() + "-service-provider");
		sd.setType("service-provider");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			System.err.println(e.getMessage());
		}
	}*/
}
