package co.com.gauthify.client;

import java.util.Map;

import org.apache.http.HttpResponse;

import co.com.gauthify.http.GauthifyHttpClient;

public interface GauthifyClient {
	public String getApiKey();
	public void setApiKey(String apiKey);

	public boolean isEnableDebug();
	public void setEnableDebug(boolean enableDebug);

	public void setGauthifyHttpClient(GauthifyHttpClient gauthifyHttpClient);
	public GauthifyHttpClient getGauthifyHttpClient();

	public String getUserAccesPoint();
	public void setUserAccesPoint(String userAccesPoint);

	public GauthifyResponse handleResponse(HttpResponse response) throws Exception;
	public GauthifyResponse getUsers() throws Exception;
	public GauthifyResponse getUserById(String userId) throws Exception;
	public GauthifyResponse getUserByToken(String token) throws Exception;
	public GauthifyResponse getUserAndCheckToken(String userId,String token) throws Exception;
	public boolean checkAuth(String userId,String authCode) throws Exception;
	public GauthifyResponse createUser(String userId,String displayName,String email,String smsNumber,String voiceNumber,String meta) throws Exception;
	public GauthifyResponse updateUser(String userId,String email,String smsNumber,String voiceNumber,String meta,boolean resetKey) throws Exception;
	public GauthifyResponse deleteUser(String userId) throws Exception;
	public GauthifyResponse sendEmailOpt(String userId,String email) throws Exception;
	public GauthifyResponse sendSmsOpt(String userId,String phoneNnumber) throws Exception;
	public GauthifyResponse sendVoiceOpt(String userId,String phoneNnumber) throws Exception;
	public GauthifyResponse handleRequest(String httpMethod,String urlModule,Map<String,String> headers,Map<String,String> fields) throws Exception;

	public String generateBasicAuthString(String username,String password) throws Exception;

	public static final String CRLF = "\r\n";

	public static final String USER_URI = "users/%s/";
	public static final String USERS_URI = "users/";
	public static final String USER_TOKEN_URI = "token/";
	public static final String USER_SMS_URI = "sms/";
	public static final String USER_VOICE_URI = "voice/";
	public static final String USER_EMAIL_URI = "email/";
	public static final String USER_CHECK_TOKEN_URI = "check/";
	public static final String EMPTY_USERNAME = "";

	public String[] END_POINTS = { "https://alpha.gauthify.com/v1/","https://beta.gauthify.com/v1/" };
}
