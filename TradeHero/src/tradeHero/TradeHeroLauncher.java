package tradeHero;

import java.util.ArrayList;
import java.util.Map;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;
import tradeHero.StockAgent.Stock;

public class TradeHeroLauncher extends RepastSLauncher {
	private static int N_NORMAL_USERS = 5;
	private static int N_GOOD_USERS = 5;
	private static int N_RANDOM_USERS = 5;
	
	private ContainerController mainContainer;
	private ContainerController agentContainer;
	
	public static final boolean SEPARATE_CONTAINERS = false;
	
	/*public static Agent getAgent(Context<?> context, AID aid) {
		for(Object obj : context.getObjects(Agent.class)) {
			if(((Agent) obj).getAID().equals(aid)) {
				return (Agent) obj;
			}
		}
		return null;
	}*/
	
	protected void launchJADE() {
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		if(SEPARATE_CONTAINERS) {
			Profile p2 = new ProfileImpl();
			agentContainer = rt.createAgentContainer(p2);
		} else {
			agentContainer = mainContainer;
		}
		
		launchAgents();
	}

	private void launchAgents() {
		try {			
			StockAgent st = new StockAgent();
			st.readHistory("goog.csv");
			
			st.updateStockValue();
			Stock actualStockValue = st.getActualStockValue().stock;
			System.out.println(" [day = " + actualStockValue.day + " , month = " + actualStockValue.month + 
            		" , year = " + actualStockValue.year + " , value = " + actualStockValue.value + "]");
			
			
			//ArrayList<Stock> market = st.getStockHistory();
			
			/*for (Map.Entry<String, ArrayList<Stock>> entry : market.entrySet()) {
			    String company = entry.getKey();
			    ArrayList<Stock> stockHistory = entry.getValue();
			    
			    System.out.println("Company: " + company);
				for(int i = 0; i < stockHistory.size(); i++) {
	                System.out.println(" [day = " + stockHistory.get(i).day + " , month = " + stockHistory.get(i).month + 
	                		" , year = " + stockHistory.get(i).year + " , value = " + stockHistory.get(i).value + "]");
				}
			}*/
			
			Receiver results = new Receiver();
			mainContainer.acceptNewAgent("ResultsCollector", results).start();
			
			mainContainer.acceptNewAgent("Stock" + 1, st).start();
			
			// create users
			// good users
			for (int i = 0; i < N_GOOD_USERS; i++) {
				UserGoodAgent us = new UserGoodAgent();
				agentContainer.acceptNewAgent("GoodUser" + i, us).start();
			}
			
			// normal users
			for (int i = 0; i < N_NORMAL_USERS; i++) {
				UserNormalAgent us = new UserNormalAgent();
				agentContainer.acceptNewAgent("NormalUser" + i, us).start();
			}
			
			// random users
			for (int i = 0; i < N_RANDOM_USERS; i++) {
				UserRandomAgent us = new UserRandomAgent();
				agentContainer.acceptNewAgent("RandomUser" + i, us).start();
			}
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Context build(Context<Object> context) {
		// http://repast.sourceforge.net/docs/RepastJavaGettingStarted.pdf
		
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("TradeHero network", context, true);
		netBuilder.buildNetwork();
		
		return super.build(context);
	}

	@Override
	public String getName() {
		return "TradeHero -- SAJaS RepastS Test";
	}
}
