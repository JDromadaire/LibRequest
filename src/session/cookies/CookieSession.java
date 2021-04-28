package session.cookies;

import session.HTTPContext;

public class CookieSession {

	private CookiePath root;
	
	private CookieSession() {
		root = CookiePath.createRoot();
	}
	
	public Cookie get(String path, HTTPContext ctx) {
		while(path.length() > 0 && path.charAt(0) == '/') {
			path = path.replaceFirst("/", "");
		}
		String[] path_dt = path.split("/");
		return root.get(path_dt, 0, path_dt.length - 1, ctx);
	}

	public void set(String path, Cookie cookie) {
		while(path.length() > 0 && path.charAt(0) == '/') {
			path = path.replaceFirst("/", "");
		}
		String[] path_dt = path.split("/");
		root.set(path_dt, 0, path_dt.length - 1, cookie);
	}
	
	public static CookieSession create() {
		return new CookieSession();
	}

}
