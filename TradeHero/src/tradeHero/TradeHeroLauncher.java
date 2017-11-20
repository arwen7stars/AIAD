package tradeHero;

import java.util.ArrayList;

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
	private static int N_NORMAL_USERS = 70;
	private static int N_GOOD_USERS = 20;
	private static int N_RANDOM_USERS = 10;
	
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
			
			ArrayList<Stock> stockHistory = st.getStockHistory();
			
			for(int i = 0; i < stockHistory.size(); i++) {
                System.out.println(" [day = " + stockHistory.get(i).day + " , month = " + stockHistory.get(i).month + 
                		" , year = " + stockHistory.get(i).year + " , value = " + stockHistory.get(i).value + "]");
			}
			Stock actualStock = st.getActualStockValue();
			System.out.println("ACTUAL STOCK [day = " + actualStock.day + " , month = " + actualStock.month + 
                		" , year = " + actualStock.year + " , value = " + actualStock.value + "]");
			
			mainContainer.acceptNewAgent("Stock" + 1, st).start();
			
			
			// create users
			// good users
			/*for (int i = 0; i < N_GOOD_USERS; i++) {
				UserGoodAgent us = new UserGoodAgent();
				agentContainer.acceptNewAgent("GoodUser" + i, us).start();
			}*/
			/*
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

			// create stocks
			for (int i = 0; i < N_STOCKS; i++) {
				StockAgent st = new StockAgent();
				mainContainer.acceptNewAgent("Stock" + i, st).start();
			}*/

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
