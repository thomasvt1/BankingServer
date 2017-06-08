package me.thomasvt.bankingserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

public class ExternalConnect {

	public static void main(String[] args) throws Exception {

		ExternalConnect http = new ExternalConnect();
		
		Map<String, String> get = new HashMap<String, String>();
		get.put("Client-Id", "aae45b32-4a99-11e7-9f16-de2b444c2004");
		get.put("Client-Secret", "72fb3847-f197-4f6e-a471-78b2fc5b7e1a");

		System.out.println("Testing 1 - Send Http GET request");
		http.sendGet(get);
		
		Map<String, String> post = new HashMap<String, String>();
		post.put("Token", "c5197594-6e1f-4ccc-9c7b-de6bb4f507f1");
		post.put("Amount", "-100");

		System.out.println("\nTesting 2 - Send Http POST request");
		http.sendPost(post);

	}

	// HTTP GET request
	private void sendGet(Map<String, String> map) throws Exception {

		String url = "https://api.bank.thomasvt.xyz/token";

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}

		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " +
                       response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());
		
		JSONObject x = new JSONObject(result.toString());
		
		String y = x.getJSONObject("token").getString("id");
		
		System.out.println(y);

	}

	// HTTP POST request
	private void sendPost(Map<String, String> map) throws Exception {

		String url = "https://api.bank.thomasvt.xyz/card/17393F25/balance";

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		// Add header
		for (Map.Entry<String, String> entry : map.entrySet()) {
			post.addHeader(entry.getKey(), entry.getValue());
		}

		HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " +
                                    response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());
	}
}