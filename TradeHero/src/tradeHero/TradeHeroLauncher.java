package tradeHero;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import sajas.core.Runtime;
import sajas.sim.repasts.RepastSLauncher;
import sajas.wrapper.ContainerController;

public class TradeHeroLauncher extends RepastSLauncher {
	private static int N_NORMAL_USERS = 2;
	private static int N_GOOD_USERS = 2;
	private static int N_RANDOM_USERS = 2;
	
	private ContainerController mainContainer;
	private ContainerController agentContainer;
	
	public static final boolean SEPARATE_CONTAINERS = false;
	
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
			StockAgent st = new StockAgent("goog");
			st.readHistory("goog.csv");
			mainContainer.acceptNewAgent("Stock" + 1, st).start();
			
			StockAgent st2 = new StockAgent("atlas");
			st2.readHistory("goog.csv");
			mainContainer.acceptNewAgent("Stock2", st2).start();
			
			Market mt = null;
			mt = new Market();
			
			mainContainer.acceptNewAgent("Market", mt).start();
			
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
		} catch(Exception e){
			 
			e.printStackTrace();
		}
	}
	
	@Override
	public Context<?> build(Context<Object> context) {
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