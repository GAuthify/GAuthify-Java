package co.com.gauthify.exceptions;

public class GauthifyParameterException extends GauthifyException {
	public GauthifyParameterException(String httpStatusCode, String message) {
		super(httpStatusCode, message);
	}

	private static final long serialVersionUID = -2846228261204361362L;
}