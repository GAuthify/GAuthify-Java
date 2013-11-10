package co.com.gauthify.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;


public class GauthifyHttpClientImpl implements GauthifyHttpClient {
	/**
	 * Hacer una peticion HTTP GET
	 * @param url
	 * @param headers
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@Override
	public HttpResponse getURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		HttpParams params = new BasicHttpParams();

		if(headers != null) {
			Set<Entry<String, String>> entrySetHeaders = headers.entrySet();

			Iterator<Entry<String, String>> itHeaders = entrySetHeaders.iterator();

			while(itHeaders.hasNext()) {
				Entry<String, String> currentHeader = itHeaders.next();

				request.addHeader(currentHeader.getKey(), currentHeader.getValue());
			}
		}

		if(fields != null) {
			Set<Entry<String, String>> entrySetFields = fields.entrySet();

			Iterator<Entry<String, String>> itFields = entrySetFields.iterator();

			while(itFields.hasNext()) {
				Entry<String, String> currentField = itFields.next();

				params.setParameter(currentField.getKey(), currentField.getValue());
			}
		}

		request.setParams(params);

		HttpResponse response = client.execute(request);

		return response;
	}

	/**
	 * Hacer una peticion HTTP POST
	 * @param url
	 * @param headers
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@Override
	public HttpResponse postURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(url);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		if(headers != null) {
			Set<Entry<String, String>> entrySetHeaders = headers.entrySet();

			Iterator<Entry<String, String>> itHeaders = entrySetHeaders.iterator();

			while(itHeaders.hasNext()) {
				Entry<String, String> currentHeader = itHeaders.next();

				request.addHeader(currentHeader.getKey(), currentHeader.getValue());
			}
		}

		if(fields != null) {
			Set<Entry<String, String>> entrySetFields = fields.entrySet();

			Iterator<Entry<String, String>> itFields = entrySetFields.iterator();

			while(itFields.hasNext()) {
				Entry<String, String> currentFidld = itFields.next();

				nameValuePairs.add(new BasicNameValuePair(currentFidld.getKey(), currentFidld.getValue()));
			}

			request.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		}

		HttpResponse response = client.execute(request);

		return response;
	}

	/**
	 * Hacer una peticion HTTP PUT
	 * @param url
	 * @param headers
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@Override
	public HttpResponse putURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpPut request = new HttpPut(url);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

		if(headers != null) {
			Set<Entry<String, String>> entrySetHeaders = headers.entrySet();

			Iterator<Entry<String, String>> itHeaders = entrySetHeaders.iterator();

			while(itHeaders.hasNext()) {
				Entry<String, String> currentHeader = itHeaders.next();

				request.addHeader(currentHeader.getKey(), currentHeader.getValue());
			}
		}

		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		if(fields != null) {
			Set<Entry<String, String>> entrySetFields = fields.entrySet();

			Iterator<Entry<String, String>> itFields = entrySetFields.iterator();

			while(itFields.hasNext()) {
				Entry<String, String> currentFidld = itFields.next();

				nameValuePairs.add(new BasicNameValuePair(currentFidld.getKey(), currentFidld.getValue()));
			}
		}

		HttpResponse response = client.execute(request);

		return response;
	}

	/**
	 * Hacer una peticion HTTP DELETE
	 * @param url
	 * @param headers
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	@Override
	public HttpResponse deleteURL(String url,Map<String,String> headers,Map<String,String> fields) throws Exception {
		HttpClient client = new DefaultHttpClient();
		HttpDelete request = new HttpDelete(url);

		HttpParams params = new BasicHttpParams();

		if(headers != null) {
			Set<Entry<String, String>> entrySetHeaders = headers.entrySet();

			Iterator<Entry<String, String>> itHeaders = entrySetHeaders.iterator();

			while(itHeaders.hasNext()) {
				Entry<String, String> currentHeader = itHeaders.next();

				request.addHeader(currentHeader.getKey(), currentHeader.getValue());
			}
		}

		if(fields != null) {
			Set<Entry<String, String>> entrySetFields = fields.entrySet();

			Iterator<Entry<String, String>> itFields = entrySetFields.iterator();

			while(itFields.hasNext()) {
				Entry<String, String> currentField = itFields.next();

				params.setParameter(currentField.getKey(), currentField.getValue());
			}
		}

		request.setParams(params);

		HttpResponse response = client.execute(request);

		return response;
	}
}