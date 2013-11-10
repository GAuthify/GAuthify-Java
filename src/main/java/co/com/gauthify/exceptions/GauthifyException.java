package co.com.gauthify.exceptions;

public class GauthifyException extends Exception {
	private static final long serialVersionUID = -3124131083515751512L;

	private String httpStatusCode;
	private String message;

	public GauthifyException(String httpStatusCode,String message) {
		super(message);

		this.httpStatusCode = httpStatusCode;
		this.message = message;
	}

	public String getHttpStatusCode() {
		return httpStatusCode;
	}
	public void setHttpStatusCode(String httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
