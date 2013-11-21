package co.com.gauthify;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import co.com.gauthify.client.GauthifyClient;
import co.com.gauthify.client.GauthifyClientImpl;
import co.com.gauthify.client.GauthifyResponse;
import co.com.gauthify.exceptions.GauthifyNotFoundException;
import co.com.gauthify.http.GauthifyHttpClient;
import co.com.gauthify.http.GauthifyHttpClientImpl;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GauthifyClientTest {
	private GauthifyResponse result;
	private GauthifyResponse result2;
	private GauthifyHttpClient gauthifyHttpClient = null;
	private GauthifyClient gauthifyClient = null;
	private JsonNode data;
	private JsonNode data2;

	private String accountName;
	private String testEmail;
	private String testSmsNumber;
	private String testVoiceNumber;
	private String badAuthToken;

	private String updateTestEmail;
	private String updateTestSmsNumber;
	private String updateTestVoiceNumber;

	@Before
    public void setUp() {
		result = null;
		result2 = null;

		data = null;
		data2 = null;

		gauthifyHttpClient = new GauthifyHttpClientImpl();
		gauthifyClient = new GauthifyClientImpl();

		gauthifyClient.setGauthifyHttpClient(gauthifyHttpClient);
		gauthifyClient.setEnableDebug(true);

		gauthifyClient.setApiKey("YOUR_GAUTHIFY_API_KEY");

		accountName = "testuser@gauthify.com";

		testEmail = "firsttest@gauthify.com";
		testSmsNumber = "+19162627232";
		testVoiceNumber = "+19162627234";
		badAuthToken = "112345";

		updateTestEmail = "test@gauthify.com";
		updateTestSmsNumber = "+19162627234";
		updateTestVoiceNumber = "+19162627235";
    }

	@Test
	public void aDeletePosibleUserTest() {
        try {
        	gauthifyClient.deleteUser(accountName);
        } catch(GauthifyNotFoundException e) {}
        catch(Exception e) {}

        Assert.assertTrue(true);
	}

	@Test
	public void bCreateUserTest() throws Exception {
        System.out.println("1) Testing Creating a User...");

        result = gauthifyClient.createUser(accountName,accountName,testEmail,testSmsNumber,testVoiceNumber,null);

        Assert.assertNotNull(result);
        Assert.assertTrue(HttpStatus.SC_OK == result.getHttpStatus() || HttpStatus.SC_CREATED == result.getHttpStatus());

        data = result.getData();

        Assert.assertNotNull(data);
        Assert.assertEquals(accountName, data.get("unique_id").asText());
        Assert.assertEquals(accountName, data.get("display_name").asText());
        Assert.assertEquals(testEmail, data.get("email").asText());
        Assert.assertEquals(testSmsNumber, data.get("sms_number").asText());
        Assert.assertEquals(testVoiceNumber, data.get("voice_number").asText());
	}

	@Test
	public void dGetUserTest() throws Exception {
        System.out.println("2) Retrieving Created User...");

        result = gauthifyClient.getUserById(accountName);

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());

        data = result.getData();

        Assert.assertNotNull(data);
	}

	@Test
	public void eGetAllUserTest() throws Exception {
        System.out.println("3) Retrieving All Users...");

        result = gauthifyClient.getUsers();

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());

        data = result.getData();

        Assert.assertNotNull(data);
	}

	@Test
	public void fCheckBadAuthCodeTest() throws Exception {
        System.out.println("4) Bad Auth Code...");
        Assert.assertEquals(false,gauthifyClient.checkAuth(accountName, badAuthToken));
	}

	@Test
	public void gCheckAuthCodeTest() throws Exception {
        System.out.println("5) Testing one time pass (OTP)....");

        result = gauthifyClient.getUserById(accountName);

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());

        data = result.getData();

        Assert.assertNotNull("Server error. OTP not working. Contact support@gauthify.com for help.",data);

        gauthifyClient.checkAuth(accountName, data.get("otp").asText());
	}

	@Test
	public void hSendEmailTest() throws Exception {
        System.out.printf("5A) Testing email to %s\n",accountName);

        result = gauthifyClient.sendEmailOtp(accountName, testEmail);

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());
	}

	@Test
	public void iSendSmsTest() throws Exception {
        System.out.printf("5B) Testing SMS to %s\n",accountName);

        result = gauthifyClient.sendSmsOtp(accountName, testSmsNumber);

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());
	}

	@Test
	public void jSendVoiceTest() throws Exception {
        System.out.printf("5C) Testing call to %s\n",accountName);

        result = gauthifyClient.sendVoiceOtp(accountName, testVoiceNumber);

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());
	}

	@Test
	public void kUpdateEmailPhoneMetaUserTest() throws Exception {
        System.out.println("6) Testing updating email, phone, and meta");

        result = gauthifyClient.updateUser(accountName, updateTestEmail, updateTestSmsNumber, updateTestVoiceNumber, "{\"a\":\"b\"}", false);

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());

        data = result.getData();
        System.out.println(result.getMessage());

        Assert.assertNotNull(data);

        Assert.assertEquals(updateTestEmail, data.get("email").asText());
        Assert.assertEquals(updateTestSmsNumber, data.get("sms_number").asText());
        Assert.assertEquals(updateTestVoiceNumber, data.get("voice_number").asText());
        Assert.assertEquals("b", data.get("meta").get("a").asText());
	}

	@Test
	public void lTestUserKeySecretTest() throws Exception {
        System.out.println("7) Testing key/secret");

        result = gauthifyClient.getUserById(accountName);

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());

        data = result.getData();

        Assert.assertNotNull(data);

        String currentKey = data.get("key").asText();

        result2 = gauthifyClient.updateUser(accountName, null, null, null, null, true);

        Assert.assertNotNull(result2);
        Assert.assertEquals(HttpStatus.SC_OK,result2.getHttpStatus());



        data2 = result2.getData();

        Assert.assertNotNull(data2);

        String key = data2.get("key").asText();

        Assert.assertNotNull(key);

        System.out.println("currentKey: " + currentKey);
        System.out.println("key: " + key);

        Assert.assertNotEquals(currentKey, key);
	}

	@Test
	public void mDeleteUserTest() throws Exception {
        System.out.println("8) Deleting Created User...");

        result = gauthifyClient.deleteUser(accountName);

        Assert.assertNotNull(result);
        Assert.assertTrue(HttpStatus.SC_OK == result.getHttpStatus() || HttpStatus.SC_ACCEPTED == result.getHttpStatus());
	}

	@Test
	public void nBackupServerTest() throws Exception {
        System.out.println("9) Testing backup server...");

        gauthifyClient.setUserAccesPoint("https://beta.gauthify.com/v1/");

        result = gauthifyClient.getUsers();

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpStatus.SC_OK,result.getHttpStatus());
	}
}
