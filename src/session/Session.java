package session;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import main.Scanner;
import main.Token;
import main.TokenType;
import session.cookies.Cookie;
import session.cookies.CookieSession;

public class Session {

	private CookieSession cookies;
	
	public CookieSession getCookies() {
		return cookies;
	}
	
	private Session() {
		cookies = CookieSession.create();
	}
	
	private int index;
	private boolean result;
	private char chr;
	private String string;
	private char advance() {
		this.index += 1;
		result = false;
		if (index < string.length()) {
			chr = string.charAt(index);
			result = true;
		}
		return chr;
	}
	
	public void importHeader(String header, HTTPContext ctx) {
		header = header.replaceAll(" ", "");
		
		if (header.startsWith("Set-Cookie:")) {
			header = header.replace("Set-Cookie:", "");
			
			String[] spl_data = header.split(";");
			String[] value_dat = spl_data[0].split("=");
			if (value_dat.length != 2) {
				throw new SessionException.MalformedCookieException("Cookie header contains more or less than 1 '=' in base head");
			}
			
			long expires = Long.MAX_VALUE;
			long max_age = Long.MAX_VALUE / 1000 - 10000 - System.currentTimeMillis() / 1000;
			String path = ctx.getPath();
			String domain = ctx.getDomain();
			boolean subdomain_enabled = false;
			int samesite = Cookie.UNDEFINED_SAMESITE;
			boolean httponly = false;
			boolean secure = false;
			for(int i = 1; i < spl_data.length; i++) {
				if (spl_data[i].startsWith("Expires=")) {
					expires = dateFormat(spl_data[i].substring(12));
				} else if (spl_data[i].startsWith("Max-Age=")) {
					max_age = Long.parseLong(spl_data[i].substring(8));
				} else if (spl_data[i].startsWith("Path=")) {
					path = spl_data[i].substring(5);
				} else if (spl_data[i].startsWith("Domain=")) {
					domain = spl_data[i].substring(7);
					subdomain_enabled = true;
				} else if (spl_data[i].startsWith("SameSite=")) {
					if (spl_data[i].equals("SameSite=Strict")) {
						samesite = Cookie.STRICT_SAMESITE;
					}
					if (spl_data[i].equals("SameSite=Lax")) {
						samesite = Cookie.LAX_SAMESITE;
					}
					if (spl_data[i].equals("SameSite=Node")) {
						samesite = Cookie.NONE_SAMESITE;
					}
				} else if (spl_data[i].equals("HTTPOnly")) {
					httponly = true;
				} else if (spl_data[i].equals("Secure")) {
					secure = true;
				}
			}
			
			String cookiePath = path;
			if (!cookiePath.endsWith("/")) {
				cookiePath = cookiePath + "/";
			}
			cookiePath = cookiePath + value_dat[0];
			
			Cookie cookie = new Cookie(value_dat[0], domain, subdomain_enabled, max_age, expires, samesite, secure, httponly);
			cookies.set(cookiePath, cookie);
		}
	}

	private long dateFormat(String str) {
		str = str.substring(0, 2) + " "+str.substring(2,5) + " "+str.substring(5, 9) + " "+str.substring(9,17)+" "+str.substring(17);
 		String form = "dd MMM yyyy hh:mm:ss zzz";
		SimpleDateFormat df = new SimpleDateFormat(form, Locale.ENGLISH);
		java.util.Date date;
		try {
			date = df.parse(str);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new SessionException.MalformedCookieException("The date isn't in the good format");
		}
	}
	
	public static Session create() {
		return new Session();
	}
	
	public static void main(String[] args) throws InterruptedException {
		Session session = Session.create();
		HTTPContext ctx = new HTTPContext("example.com", "/");
		session.importHeader("Set-Cookie: name=value; Expires=Wed, 29 Apr 2021 10:52:00 GMT;Max-Age=10", ctx);
		session.getCookies().get("/name", ctx);
	}
	
}
