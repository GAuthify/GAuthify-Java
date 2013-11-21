package co.com.gauthify.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import co.com.gauthify.exceptions.GauthifyApiException;
import co.com.gauthify.exceptions.GauthifyException;
import co.com.gauthify.exceptions.GauthifyNotFoundException;
import co.com.gauthify.exceptions.GauthifyParameterException;
import co.com.gauthify.exceptions.GauthifyRateLimitException;
import co.com.gauthify.http.GauthifyHttpClient;

public class GauthifyClientImpl implements GauthifyClient {
	private boolean enableDebug = true;
	private String userAccesPoint;

	public String getUserAccesPoint() {
		return userAccesPoint;
	}

	public void setUserAccesPoint(String userAccesPoint) {
		this.userAccesPoint = userAccesPoint;
	}

	private String apiKey;
	private GauthifyHttpClient gauthifyHttpClient;

	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public void setGauthifyHttpClient(GauthifyHttpClient gauthifyHttpClient) {
		this.gauthifyHttpClient = gauthifyHttpClient;
	}

	@Override
	public GauthifyHttpClient getGauthifyHttpClient() {
		return gauthifyHttpClient;
	}

	/**
     * Prcess a gauthify request and obtain a response
	 * Procesar una respuesta de Gauthify y obtener la respuesta
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse handleResponse(HttpResponse response) throws Exception {
		GauthifyResponse userResponse = new GauthifyResponse();

		if(response != null) {
			int httpStatusCode = response.getStatusLine().getStatusCode();

			userResponse.setHttpStatus(httpStatusCode);
			userResponse.setMessage(response.getStatusLine().getReasonPhrase());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuilder sb = new StringBuilder();

			String line = "";
			while ((line = rd.readLine()) != null) {
				sb.append(line).append(CRLF);
			}

			if(isEnableDebug()) {
				System.out.println("Response: " + sb.toString());
			}

			ObjectMapper mapper = new ObjectMapper();
			JsonFactory factory = mapper.getJsonFactory();
			JsonParser jp = factory.createJsonParser(sb.toString());
			JsonNode userJsonNode = mapper.readTree(jp);

			JsonNode errorMessageJsonNode = userJsonNode.get("error_message");
			String errorMessage = errorMessageJsonNode == null ? "" : errorMessageJsonNode.getTextValue();

			JsonNode errorCodeJsonNode = userJsonNode.get("error_code");

			String errorCode = errorCodeJsonNode == null ? "" : errorCodeJsonNode.getTextValue();

			JsonNode data = userJsonNode.get("data");

			userResponse.setData(data);

			if(httpStatusCode == 401) {
				throw new GauthifyApiException(errorCode,errorMessage);
			} else if(httpStatusCode == 402) {
				throw new GauthifyRateLimitException(errorCode,errorMessage);
			} else if(httpStatusCode == 404) {
				throw new GauthifyNotFoundException(errorCode,errorMessage);
			} else if(httpStatusCode == 406) {
				throw new GauthifyParameterException(errorCode,errorMessage);
			}
		} else {
			userResponse.setMessage("Error response is null");
		}

		return userResponse;
	}

	/**
     *  Use a user id to retrieve all users
	 * Consultar un usuario dado el legacy id del usuario
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse getUsers() throws Exception {
		String urlModule =  USERS_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		return this.handleRequest("GET", urlModule, headers, null);
	}

	/**
     * Retreive a user by ID
	 * Consultar un usuario dado el legacy id del usuario
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse getUserById(String userId) throws Exception {
		String urlModule = String.format(USER_URI, userId);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		return this.handleRequest("GET", urlModule, headers, null);
	}

	/**
     * Retrieve a user by token
	 * Consultar un usuario dado el token de Gauthify
	 * @param token
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse getUserByToken(String token) throws Exception {
		String urlModule = String.format(USER_TOKEN_URI, token);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();
		fields.put("token",token);

		return this.handleRequest("POST", urlModule, headers, fields);
	}

	/**
     *  Retrieve a user and checks the auth token
	 * Consultar la informacion de un usuario y validar un token de autenticacion, si el token es valido el atributo authenticated del objeto json data que se obtiene como respuesta tiene como valor true, en caso contrario tiene como valor false
	 * @param userId
	 * @param authCode
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse getUserAndCheckToken(String userId,String authCode) throws Exception {
		String urlModule = USER_CHECK_TOKEN_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();
		fields.put("unique_id",userId);
		fields.put("auth_code",authCode);

		return this.handleRequest("POST", urlModule, headers, fields);
	}

	/**
     *  Check whether a user is authenticated (alias of getUserAndCheckToken but bool return)
	 * Validar si un usuario esta autenticado
	 * @param userId
	 * @param authCode
	 * @return
	 * @throws Exception
	 */
	public boolean checkAuth(String userId,String authCode) throws Exception {
		boolean isAuthenticated = false;

		GauthifyResponse response = this.getUserAndCheckToken(userId, authCode);

		if(response != null && response.getHttpStatus() == HttpStatus.SC_OK) {
			JsonNode data = response.getData();

			if(data != null) {
				JsonNode authenticated = data.get("authenticated");

				if(authenticated != null) {
					isAuthenticated = authenticated.asBoolean();
				}
			}
		}

		return isAuthenticated;
	}

	/**
     *  Create a user
	 * Crear un usuario o actualiza uno existente
	 * @param userId
	 * @param displayName
	 * @param email
	 * @param smsNumber
	 * @param voiceNumber
	 * @param meta: String in json object format
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse createUser(String userId,String displayName,String email,String smsNumber,String voiceNumber,String meta) throws Exception {
		String urlModule =  USERS_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();

		fields.put("unique_id", userId);
		fields.put("display_name", displayName);

		if(email != null && email.length() > 0) {
			fields.put("email", email);
		}

		if(smsNumber != null && smsNumber.length() > 0) {
			fields.put("sms_number", smsNumber);
		}

		if(voiceNumber != null && voiceNumber.length() > 0) {
			fields.put("voice_number", voiceNumber);
		}

		if(meta != null && meta.length() > 0) {
			fields.put("meta", meta);
		}

		return this.handleRequest("POST", urlModule, headers, fields);
	}

	/**
     *  Update a user
	 * Actualizar un usuario, crea uno nuevo si no existe
	 * @param userId
	 * @param displayName
	 * @param email
	 * @param smsNumber
	 * @param voiceNumber
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse updateUser(String userId,String email,String smsNumber,String voiceNumber,String meta,boolean resetKey) throws Exception {
		String urlModule = String.format(USER_URI, userId);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();

		fields.put("sms_number", smsNumber);
		fields.put("voice_number", voiceNumber);
		fields.put("email", email);
		fields.put("meta", meta);
		fields.put("reset_key", resetKey == true ? "true": "false");

		return this.handleRequest("PUT", urlModule, headers, fields);
	}

	/**
     *  Permanently delete a user
	 * Eliminar un uusuario en forma permanente
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse deleteUser(String userId) throws Exception {
		String urlModule = String.format(USER_URI, userId);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		return this.handleRequest("DELETE", urlModule, headers, null);
	}

	/**
     *  Send user an email with their OTP code
	 * Send a e-mail to user with OTP code
	 * @param userId
	 * @param email
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse sendEmailOtp(String userId,String email) throws Exception {
		String urlModule = USER_EMAIL_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();
		fields.put("unique_id",userId);
		fields.put("email",email);

		return this.handleRequest("POST", urlModule, headers, fields);
	}

	/**
     * Call a user with their OTP code
	 * Send a SMS to user with OTP code
	 * @param userId
	 * @param phoneNumber
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse sendSmsOtp(String userId,String phoneNumber) throws Exception {
		String urlModule = USER_SMS_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();
		fields.put("unique_id",userId);
		fields.put("sms_number",phoneNumber);

		return this.handleRequest("POST", urlModule, headers, fields);
	}

	@Override
	public GauthifyResponse sendVoiceOtp(String userId, String phoneNumber) throws Exception {
		String urlModule = USER_VOICE_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();
		fields.put("unique_id",userId);
		fields.put("voice_number",phoneNumber);

		return this.handleRequest("POST", urlModule, headers, fields);
	}

	/**
     *  HTTP request handler
	 * Handle any HTTP request
	 * @param httpMethod: POST,GET,PUT,DELETE
	 * @param urlModule: Relative URL
	 * @param headers: Http Headers
	 * @param fields: Fiels to http request
	 * @return GauthifyResponse
	 * @throws Exception
	 */
	public GauthifyResponse handleRequest(String httpMethod,String urlModule,Map<String,String> headers,Map<String,String> fields) throws Exception {
		httpMethod = httpMethod.toUpperCase();

		GauthifyResponse gauthifyResponse = null;
		HttpResponse httpResponse = null;

		String[] endPoints = END_POINTS;

		String userEndPoint = this.getUserAccesPoint();

		//Specific
		if(userEndPoint != null && userEndPoint.length() > 0) {
			String url = userEndPoint + urlModule;

			try {
				if(httpMethod.equals("GET")) {
					httpResponse = this.getGauthifyHttpClient().getURL(url, headers, fields);
				} else if(httpMethod.equals("POST")) {
					httpResponse = this.getGauthifyHttpClient().postURL(url, headers, fields);
				} else if(httpMethod.equals("PUT")) {
					httpResponse = this.getGauthifyHttpClient().putURL(url, headers, fields);
				} else if(httpMethod.equals("DELETE")) {
					httpResponse = this.getGauthifyHttpClient().deleteURL(url, headers, fields);
				}

				gauthifyResponse = this.handleResponse(httpResponse);

				return gauthifyResponse;
			} catch(GauthifyException e) {
				throw e;
			} catch(Exception e) {
				throw new GauthifyException("500", e.getMessage() + " Please contact support@gauthify.com for help, endPoint: " + userEndPoint + " is not working");
			}
		} else {
			for(String endPoint : endPoints) {
				String url = endPoint + urlModule;

				try {
					if(httpMethod.equals("GET")) {
						httpResponse = this.getGauthifyHttpClient().getURL(url, headers, fields);
					} else if(httpMethod.equals("POST")) {
						httpResponse = this.getGauthifyHttpClient().postURL(url, headers, fields);
					} else if(httpMethod.equals("PUT")) {
						httpResponse = this.getGauthifyHttpClient().putURL(url, headers, fields);
					} else if(httpMethod.equals("DELETE")) {
						httpResponse = this.getGauthifyHttpClient().deleteURL(url, headers, fields);
					}

					gauthifyResponse = this.handleResponse(httpResponse);

					return gauthifyResponse;
				} catch(GauthifyException e) {
					throw e;
				} catch(Exception e) {
					//Last endpoint
					if(endPoint.equals(endPoints[endPoints.length -1])) {
						throw new GauthifyException("500", e.getMessage() + " Please contact support@gauthify.com for help");
					}

					continue;
				}
			}
		}

		return null;
	}

	/**
     *  Generate headers for http Auth using API key
	 * Generate String to Http Basic Autentication
	 * @param username
	 * @param password
	 * @return
	 */
	public String generateBasicAuthString(String username,String password) {
		StringBuilder sb = new StringBuilder(username).append(":").append(password);
		byte[] base64 = Base64.encodeBase64(sb.toString().getBytes());

		return new StringBuilder("Basic ").append(new String(base64)).toString();
	}

	@Override
	public boolean isEnableDebug() {
		return enableDebug;
	}

	@Override
	public void setEnableDebug(boolean enableDebug) {
		this.enableDebug = enableDebug;
	}
}
