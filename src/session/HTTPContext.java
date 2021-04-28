package session;

public class HTTPContext {

	private String domain;
	private String path;
	
	public HTTPContext(String domain, String path) {
		this.domain = domain;
		this.path = path;
	}

	public String getDomain() {
		return domain;
	}
	
	public String getPath() {
		return path;
	}
	
}
