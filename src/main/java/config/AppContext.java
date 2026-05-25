package config;

import service.MatchService;

public class AppContext {
	private static final MatchService matchService = new MatchService();
	
	
	public static MatchService getMatchService() {
		return matchService;
	}
}
