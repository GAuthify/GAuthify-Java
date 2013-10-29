package co.com.fsistemas.gauthify.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import co.com.fsistemas.gauthify.exceptions.GauthifyApiException;
import co.com.fsistemas.gauthify.exceptions.GauthifyNotFoundException;
import co.com.fsistemas.gauthify.exceptions.GauthifyParameterException;
import co.com.fsistemas.gauthify.exceptions.GauthifyRateLimitException;
import co.com.fsistemas.gauthify.http.GauthifyHttpClient;

public class GauthifyClientImpl implements GauthifyClient {
	public int mode = DEFAULT_MODE;
	private boolean enableDebug = true;

	private String apiKey;
	private GauthifyHttpClient gauthifyHttpClient;
	
	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

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
	 * Consultar un usuario dado el legacy id del usuario
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse getUsers() throws Exception {
		final String url = getUrlBase() + USERS_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		HttpResponse response = gauthifyHttpClient.getURL(url, headers, null);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;
	}

	/**
	 * Consultar un usuario dado el legacy id del usuario
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse getUserById(String userId) throws Exception {
		String url = getUrlBase() + String.format(USER_URI, userId);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		HttpResponse response = gauthifyHttpClient.getURL(url, headers, null);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;
	}

	/**
	 * Consultar un usuario dado el token de Gauthify
	 * @param token
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse getUserByToken(String token) throws Exception {
		String url = getUrlBase() + String.format(USER_TOKEN_URI, token);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> params = new HashMap<String, String>();
		params.put("token",token);

		HttpResponse response = gauthifyHttpClient.postURL(url, headers, params);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;
	}

	/**
	 * Consultar la informacion de un usuario y validar un token de autenticacion, si el token es valido el atributo authenticated del objeto json data que se obtiene como respuesta tiene como valor true, en caso contrario tiene como valor false
	 * @param userId
	 * @param authCode
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse getUserAndCheckToken(String userId,String authCode) throws Exception {
		String url = getUrlBase() + USER_CHECK_TOKEN_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> params = new HashMap<String, String>();
		params.put("unique_id",userId);
		params.put("auth_code",authCode);

		HttpResponse response = gauthifyHttpClient.postURL(url, headers, params);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;
	}

	/**
	 * Crear un usuario o actualiza uno existente
	 * @param userId
	 * @param displayName
	 * @param email
	 * @param phoneNumber
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse createUser(String userId,String displayName,String email,String phoneNumber) throws Exception {
		String url = getUrlBase() + String.format(USER_URI, userId);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();

		fields.put("display_name", displayName);
		fields.put("phone_number", phoneNumber);
		fields.put("email", email);

		HttpResponse response = gauthifyHttpClient.postURL(url, headers, fields);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;
	}
	
	/**
	 * Actualizar un usuario, crea uno nuevo si no existe
	 * @param userId
	 * @param displayName
	 * @param email
	 * @param phoneNumber
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse updateUser(String userId,String displayName,String email,String phoneNumber,String meta,boolean resetKey) throws Exception {
		String url = getUrlBase() + String.format(USER_URI, userId);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> fields = new HashMap<String, String>();

		fields.put("display_name", displayName);
		fields.put("phone_number", phoneNumber);
		fields.put("email", email);
		fields.put("meta", meta);
		fields.put("reset_key", resetKey == true ? "1": "0");

		HttpResponse response = gauthifyHttpClient.putURL(url, headers, fields);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;		
	}

	/**
	 * Eliminar un uusuario en forma permanente
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse deleteUser(String userId) throws Exception {
		String url = getUrlBase() + String.format(USER_URI, userId);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		HttpResponse response = gauthifyHttpClient.deleteURL(url, headers, null);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;		
	}

	/**
	 * Send a e-mail to user with OTP code 
	 * @param userId
	 * @param email
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse sendEmailOpt(String userId,String email) throws Exception {
		String url = getUrlBase() + USER_EMAIL_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> params = new HashMap<String, String>();
		params.put("unique_id",userId);
		params.put("email",email);

		HttpResponse response = gauthifyHttpClient.postURL(url, headers, params);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;
	}

	/**
	 * Send a SMS to user with OTP code
	 * @param userId
	 * @param phoneNumber
	 * @return
	 * @throws Exception
	 */
	@Override
	public GauthifyResponse sendSmsOpt(String userId,String phoneNumber) throws Exception {
		String url = getUrlBase() + USER_SMS_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> params = new HashMap<String, String>();
		params.put("unique_id",userId);
		params.put("sms_number",phoneNumber);

		HttpResponse response = gauthifyHttpClient.postURL(url, headers, params);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;
	}

	@Override
	public GauthifyResponse sendVoiceOpt(String userId, String phoneNumber) throws Exception {
		String url = getUrlBase() + USER_VOICE_URI;

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", generateBasicAuthString(EMPTY_USERNAME, apiKey));

		Map<String, String> params = new HashMap<String, String>();
		params.put("unique_id",userId);
		params.put("voice_number",phoneNumber);

		HttpResponse response = gauthifyHttpClient.postURL(url, headers, params);

		GauthifyResponse userResponse = this.handleResponse(response);

		return userResponse;
	}

	/**
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
	public String getUrlBase() {
		switch(getMode()) {
			case PDN_MODE:
					return PDN_URL_BASE;
			case BETA_MODE:
				return BETA_URL_BASE;
			default:
				return ALPHA_URL_BASE;
		}
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