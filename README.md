GAuthify-Java
===============
[Direct link to library](https://github.com/GAuthify/GAuthify-Java).

This is the Java API Client for [GAuthify](https://www.gauthify.com). The GAuthify REST api helps websites quickly add multi-factor authentication through Google Authenticator, SMS, Voice, and Email. This package is a simple wrapper around that api.

Written by GAuthify user [Francisco Perez](https://github.com/fsistemas)!

Usage
--------------
####Initiate:####
First get an API key by signing up for an account [here](http://www.gauthify.com).

First instantiate GAuthify:

    import co.com.gauthify.client.GauthifyClient;
    import co.com.gauthify.client.GauthifyClientImpl;
    import co.com.gauthify.client.GauthifyResponse;
    import co.com.gauthify.exceptions.GauthifyNotFoundException;
    import co.com.gauthify.http.GauthifyHttpClient;
    import co.com.gauthify.http.GauthifyHttpClientImpl;

    gauthifyHttpClient = new GauthifyHttpClientImpl();
    gauthifyClient = new GauthifyClientImpl();
    gauthifyClient.setGauthifyHttpClient(gauthifyHttpClient);

    gauthifyClient.setApiKey("YOUR_GAUTHIFY_API_KEY");


####Create User:####

    result = gauthifyClient.createUser(String userId,String displayName,String email,String smsNumber,String voiceNumber,String meta)
    data = result.getData();

####Update User:####

    result = gauthifyClient.updateUser(String userId,String email,String smsNumber,String voiceNumber,String meta,boolean resetKey)
    data = result.getData();


####Delete User:####

    result = gauthifyClient.deleteUser(String userId)

####Get All Users:####

    result = gauthifyClient.getUsers()
    data = result.getData();

####Get User By ID:####

    result = gauthifyClient.getUserById(String userId)
    data = result.getData();

####Get User By Token:####

    result = gauthifyClient.getUserByToken(String token)
    data = result.getData();

####Check Auth Code:####

    result = gauthifyClient.getUserAndCheckToken(String userId,String token)
    data = result.getData();

####Send Email:####

    result = gauthifyClient.sendEmailOtp(String userId,String email)

####Send SMS:####

    result = gauthifyClient.sendSmsOtp(String userId,String phoneNnumber)

####Send Voice:####

    result = gauthifyClient.sendVoiceOtp(String userId,String phoneNnumber)

Errors
--------------
The primary error class is GAuthifyException which extends Exception. It has the following attributes:

    GAuthifyException.getHttpStatusCode()
    GAuthifyException.getMessage()

