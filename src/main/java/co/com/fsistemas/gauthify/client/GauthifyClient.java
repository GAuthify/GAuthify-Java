package co.com.fsistemas.gauthify.client;

import org.apache.http.HttpResponse;

import co.com.fsistemas.gauthify.http.GauthifyHttpClient;

public interface GauthifyClient {
	public String getApiKey();
	public void setApiKey(String apiKey);

	public int getMode();
	public void setMode(int mode);
	public boolean isEnableDebug();
	public void setEnableDebug(boolean enableDebug);

	public String getUrlBase();

	public void setGauthifyHttpClient(GauthifyHttpClient gauthifyHttpClient);
	public GauthifyHttpClient getGauthifyHttpClient();

	public GauthifyResponse handleResponse(HttpResponse response) throws Exception;
	public GauthifyResponse getUsers() throws Exception;
	public GauthifyResponse getUserById(String userId) throws Exception;
	public GauthifyResponse getUserByToken(String token) throws Exception;
	public GauthifyResponse getUserAndCheckToken(String userId,String token) throws Exception;
	public GauthifyResponse createUser(String userId,String displayName,String email,String phoneNumber) throws Exception;
	public GauthifyResponse updateUser(String userId,String displayName,String email,String phoneNumber,String meta,boolean resetKey) throws Exception;
	public GauthifyResponse deleteUser(String userId) throws Exception;
	public GauthifyResponse sendEmailOpt(String userId,String email) throws Exception;
	public GauthifyResponse sendSmsOpt(String userId,String phoneNnumber) throws Exception;
	public GauthifyResponse sendVoiceOpt(String userId,String phoneNnumber) throws Exception;
	public String generateBasicAuthString(String username,String password) throws Exception;

	public static final String CRLF = "\r\n";
	public static final String ALPHA_URL_BASE = "https://alpha.gauthify.com/v1/";
	public static final String BETA_URL_BASE = "https://beta.gauthify.com/v1/";
	public static final String PDN_URL_BASE = "https://api.gauthify.com/v1/";

	public static final String USER_URI = "users/%s/";
	public static final String USERS_URI = "users/";
	public static final String USER_TOKEN_URI = "token/";
	public static final String USER_SMS_URI = "sms/";
	public static final String USER_VOICE_URI = "voice/";
	public static final String USER_EMAIL_URI = "email/";
	public static final String USER_CHECK_TOKEN_URI = "check/";
	public static final String EMPTY_USERNAME = "";

	public static final int ALPHA_MODE = 2;
	public static final int BETA_MODE = 1;
	public static final int PDN_MODE = 0;
	public static final int DEFAULT_MODE = ALPHA_MODE;
}
