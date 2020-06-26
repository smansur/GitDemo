package frameworkUtilities;

public enum APIResources {
	
	DreesTeamLoginAPI(""),
	DreesTeam("");
	
	private String resource;
	private APIResources(String resource) {
		this.resource=resource;
	}
	
	public String getAPIUrl() {
		return resource;
	}
	
}
