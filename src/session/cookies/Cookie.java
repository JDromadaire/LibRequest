package session.cookies;

import session.HTTPContext;

public class Cookie {

	private String value;
	
	private long expireDate;

	private String domain;
	private boolean subdomain_enabled;
	
	private boolean secure;
	
	private boolean HTTPOnly;
	
	public static final int UNDEFINED_SAMESITE = -1;
	public static final int STRICT_SAMESITE = 0;
	public static final int LAX_SAMESITE = 1;
	public static final int NONE_SAMESITE = 2;
	private int SameSite;
	
	public Cookie(String value, String domain, boolean subdomain_enabled, 
			long max_age, long expireDate,
			int SameSite, boolean secure, boolean HTTPOnly) {
		this.value = value;
		this.expireDate = Math.min(expireDate, System.currentTimeMillis() + max_age * 1000);
		this.domain = domain;
		this.subdomain_enabled = subdomain_enabled;
		this.SameSite = SameSite;
		this.secure = secure;
		this.HTTPOnly = HTTPOnly;
	}
	
	public Cookie check(HTTPContext ctx) {
		if (this.expireDate < System.currentTimeMillis()) {
			return null;
		}
		if (!((ctx.getDomain().endsWith("."+domain) && subdomain_enabled) || domain.equals(ctx.getDomain()))) {
			if (secure && SameSite == STRICT_SAMESITE) {
				return null;
			}
		}
		return this;
	}
	
	public String getValue() {
		return value;
	}

	public String getCheckError(HTTPContext ctx) {
		if (this.expireDate < System.currentTimeMillis()) {
			return "The date is expired since "+(System.currentTimeMillis() - this.expireDate)+"ms";
		}
		if (!((ctx.getDomain().endsWith("."+domain) && subdomain_enabled) || domain.equals(ctx.getDomain()))) {
			if (secure && SameSite == STRICT_SAMESITE) {
				return "The requested domain "+ctx.getDomain()+" does not match sub- or domain of the cookie ("+domain+")";
			}
		}
		return "No error detected in cookie";
	}
	
}
