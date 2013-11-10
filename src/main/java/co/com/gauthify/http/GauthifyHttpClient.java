package co.com.gauthify.http;

import java.util.Map;

import org.apache.http.HttpResponse;

public interface GauthifyHttpClient {
	public HttpResponse getURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception;
	public HttpResponse postURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception;
	public HttpResponse putURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception;
	public HttpResponse deleteURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception;
}
