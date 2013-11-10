package co.com.gauthify.exceptions;

public class GauthifyNotFoundException extends GauthifyException {
	public GauthifyNotFoundException(String httpStatusCode, String message) {
		super(httpStatusCode, message);
	}

	private static final long serialVersionUID = -1452165939507946515L;
}
