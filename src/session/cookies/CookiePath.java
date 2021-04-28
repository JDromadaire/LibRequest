package session.cookies;

import java.util.HashMap;

import session.HTTPContext;
import session.SessionException;

public class CookiePath {

	private String path;
	private CookiePath parent;
	
	private HashMap<String, CookiePath> pathes = new HashMap<String, CookiePath>();
	private HashMap<String, Cookie> datas = new HashMap<String, Cookie>();
	
	public Cookie get(String[] path_data, int index, int cookie_name_index, HTTPContext ctx) {
		boolean checked = false;
		Cookie cookie = datas.getOrDefault(path_data[cookie_name_index], null);
		if (cookie != null) {
			checked = true;
			cookie = cookie.check(ctx);
			if (cookie != null) {
				return cookie;
			}
		}
		
		if (index == cookie_name_index && !checked) {
			throw new SessionException.NoCookieFoundException("ERROR 0:The path is correct but the cookie name is incorrect");
		} else if (index == cookie_name_index) {
			throw new SessionException.NoCookieFoundException("ERROR 2:"+datas.get(path_data[cookie_name_index]).getCheckError(ctx));
		}
		
		CookiePath next_path = pathes.getOrDefault(path_data[index], null);
		if (next_path == null) {
			throw new SessionException.NoCookieFoundException("ERROR 1:The path is incorrect, could not find name:\""+path_data[index]+"\"");
		}
		
		return next_path.get(path_data, index + 1, cookie_name_index, ctx);
	}
	
	public void set(String[] path_data, int index, int cookie_name_index, Cookie cookie) {
		if (index == cookie_name_index) {
			this.datas.put(path_data[index], cookie);
			return ;
		}
		
		if (!pathes.containsKey(path_data[index])) {
			CookiePath.create(path_data[index], this);
		}
		
		pathes.get(path_data[index]).set(path_data, index + 1, cookie_name_index, cookie);
	}
	
	public void registerPath(String path, CookiePath child) {
		if (this.pathes.containsKey(path)) {
			throw new SessionException.PathAlreadyCreatedException("The path "+path+" already exists in path"+this.path);
		}
		
		this.pathes.put(path, child);
	}
	
	private CookiePath(String path, CookiePath parent) {
		this.path = path;
		this.parent = parent;
	}
	
	public static CookiePath create(String path, CookiePath parent) {
		CookiePath dat = new CookiePath(path, parent);
		
		parent.registerPath(path, dat);
		
		return dat;
	}

	public static CookiePath createRoot() {
		return new CookiePath("", null);
	}
	
}
