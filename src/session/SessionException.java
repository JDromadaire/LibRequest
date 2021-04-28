package session;

public class SessionException {

	public static class NoCookieFoundException extends RuntimeException {

		public NoCookieFoundException(String string) {
			super(string);
		}}

	public static class PathAlreadyCreatedException extends RuntimeException {

		public PathAlreadyCreatedException(String string) {
			super(string);
		}}

	public static class MalformedCookieException extends RuntimeException {

		public MalformedCookieException(String string) {
			super(string);
		}}
	
}
