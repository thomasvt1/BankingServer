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
		String card = "17393F25";
		
		// TESTING 1 - REQUEST TOKEN

		System.out.println("Testing 1 - Send Http GET - request token");
		
		String token = http.getToken(SOFA);
		
		System.out.println(token);
		
		// TESTING 2 - VALIDATE TOKEN

		System.out.println("\nTesting 2 - Send Http GET - validate token");
				
		boolean validated = http.validateToken(SOFA, token, "1111", card);
		
		System.out.println(validated);
		
		//TESTING 3 - WITHDRAW MONEY
		
		System.out.println("\nTesting 3 - Send Http POST - withdraw");
		
		boolean withdrawn = http.withdrawMoney(SOFA, card, token, "-100");
		
		System.out.println(withdrawn);

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
	
	boolean validateToken(BankObject bank,String token, String pin, String card) {
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", token);
		get.put("Pin", pin);
		
		try {
			String suffix = "/card/{CARD}/validate";
			suffix = suffix.replace("{CARD}", card);
			
			String s = sendGet(bank, suffix, get);
			
			JSONObject json = new JSONObject(s);
			
			if (json.has("error"))
				return false;
			
			if (!json.has("card"))
				return false;
			
			else
				return json.getJSONObject("card").getBoolean("validate");
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	boolean withdrawMoney(BankObject bank, String card, String token, String amount) {
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", token);
		get.put("Amount", amount);
		
		try {
			String suffix = "/card/{CARD}/balance";
			suffix = suffix.replace("{CARD}", card);
			
			String s = sendPost(bank, suffix, get);
			
			
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