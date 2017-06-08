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

import me.thomasvt.bankingserver.Bank.BankObject;

public class ExternalConnect {
	
	public static void main(String[] args) throws Exception {

		ExternalConnect http = new ExternalConnect();
		
		BankObject SOFA = new Bank().SOFA;
		
		// TESTING 1 - REQUEST TOKEN

		System.out.println("Testing 1 - Send request token");
		
		System.out.println(http.getToken(SOFA));
		
		//TESTING 3 - WITHDRAW MONEY
		
		Map<String, String> post = new HashMap<String, String>();
		post.put("Token", "c5197594-6e1f-4ccc-9c7b-de6bb4f507f1");
		post.put("Amount", "-100");

		System.out.println("Testing 2 - Send Http POST request");
		//http.sendPost(post);
	}
	
	String getToken(BankObject bank) {
		Map<String, String> get = new HashMap<String, String>();
		get.put("Client-Id", bank.getId());
		get.put("Client-Secret", bank.getSecret());
		
		try {
			String s = sendGet(bank, "/token", get);
			
			JSONObject json = new JSONObject(s);
			
			if (json.has("error"))
				return null;
			
			else
			return json.getJSONObject("token").getString("id");
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	boolean withdrawMoney(BankObject bank, String token, String amount) {
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", token);
		get.put("Amount", amount);
		
		try {
			String s = sendGet(bank, "/token", get);
			
			JSONObject json = new JSONObject(s);
			
			if (json.has("error"))
				return false;
			
			else if (json.has("card"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// HTTP GET request
	private String sendGet(BankObject bank, String suffix, Map<String, String> map) throws Exception {

		String url = bank.getUrl() + suffix;
		//String url = "https://api.bank.thomasvt.xyz/token";

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}

		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' request to URL : " + url);

		BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());
		
		return result.toString();

	}

	// HTTP POST request
	private String sendPost(BankObject bank, String suffix, Map<String, String> map) throws Exception {

		String url = bank.getUrl() + suffix;
		//String url = "https://api.bank.thomasvt.xyz/card/17393F25/balance";

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		// Add header
		for (Map.Entry<String, String> entry : map.entrySet()) {
			post.addHeader(entry.getKey(), entry.getValue());
		}

		HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' request to URL : " + url);

		BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());
		return result.toString();
	}
}