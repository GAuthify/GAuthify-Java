package co.com.gauthify.exceptions;

public class GauthifyRateLimitException extends GauthifyException {
	public GauthifyRateLimitException(String httpStatusCode, String message) {
		super(httpStatusCode, message);
	}

	private static final long serialVersionUID = 5223274541476477005L;
}