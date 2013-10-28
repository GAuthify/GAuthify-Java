package co.com.fsistemas.gauthify.exceptions;

public class GauthifyApiException extends GauthifyException {
	public GauthifyApiException(String httpStatusCode, String message) {
		super(httpStatusCode, message);
	}

	private static final long serialVersionUID = 2854256330628412766L;
}