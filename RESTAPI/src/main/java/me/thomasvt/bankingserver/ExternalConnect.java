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

		BankObject BANK = new Bank().getBankObject("SOFA");
		//String card = "17393F25";
		//String pin = "1111";
		String card = "9D47F835";
		String pin = "1234";

		// TESTING 1 - REQUEST TOKEN

		System.out.println("Testing 1 - Send Http GET - request token");

		String token = http.getToken(BANK);

		System.out.println(token);
		
		// TESTING 1 - REQUEST TOKEN

		System.out.println("\nTesting 2a - Send Http GET - card exists");

		boolean exists = http.cardExists(BANK, card);

		System.out.println(exists);

		// TESTING 2 - VALIDATE TOKEN
		
		if (!exists)
			return;

		System.out.println("\nTesting 2b - Send Http GET - validate token");

		boolean validated = http.validateToken(BANK, pin, card);

		System.out.println(validated);
		
		// TESTING 3 - GET BALANCE

		System.out.println("\nTesting 3 - Send Http GET - get balance");

		int balance = http.getBalance(BANK, pin, card);

		System.out.println(balance);

		// TESTING 4 - WITHDRAW MONEY

		System.out.println("\nTesting 4 - Send Http POST - withdraw");

		boolean withdrawn = http.withdrawMoney(BANK, card, pin, "-100");

		System.out.println(withdrawn);
		
		//main(null);

	}

	String getToken(BankObject bank) {
		
		if (App.tokens.containsKey(bank)) {
			return App.tokens.get(bank);
		}
		
		Map<String, String> get = new HashMap<String, String>();
		get.put("Client-Id", bank.getId());
		get.put("Client-Secret", bank.getSecret());

		try {
			String s = sendGet(bank, "/token", get);

			JSONObject json = new JSONObject(s);

			if (json.has("error"))
				return null;

			else {
				String token = json.getJSONObject("token").getString("id");
				App.tokens.put(bank, token);
				int delay = (json.getJSONObject("token").getInt("expires") /2) * 1000;
				
				new Thread(() -> {
					new TokenTimeout(delay, bank).start();
				}).start();
				
				return token;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	boolean getEnabled(BankObject bank, String pin, String card) {
		///card/:UUID/enabled
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", getToken(bank));

		try {
			String suffix = "/card/{CARD}/enabled";
			suffix = suffix.replace("{CARD}", card);

			String s = sendGet(bank, suffix, get);

			JSONObject json = new JSONObject(s);

			if (json.has("error") || !json.has("card"))
				return false;

			else
				return json.getJSONObject("card").getBoolean("enabled");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	int getTries(BankObject bank, String card) {
		///card/:UUID/enabled
		String token = getToken(bank);
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", token);

		try {
			String suffix = "/card/{CARD}/enabled";
			suffix = suffix.replace("{CARD}", card);

			String s = sendGet(bank, suffix, get);

			JSONObject json = new JSONObject(s);

			if (json.has("error"))
				return 0;

			if (!json.has("card"))
				return 0;

			else
				return json.getJSONObject("card").getInt("tries");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	boolean cardEnabled(BankObject bank, String card) {
		///card/:UUID/enabled
		String token = getToken(bank);
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", token);

		try {
			String suffix = "/card/{CARD}/enabled";
			suffix = suffix.replace("{CARD}", card);

			String s = sendGet(bank, suffix, get);

			JSONObject json = new JSONObject(s);
			
			if (json.has("error") | !json.has("card"))
				return false;
			
			else
				return json.getJSONObject("card").getBoolean("enabled");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	int getBalance(BankObject bank, String pin, String card) {
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", getToken(bank));
		get.put("Pin", pin);

		try {
			String suffix = "/card/{CARD}/balance";
			suffix = suffix.replace("{CARD}", card);

			String s = sendGet(bank, suffix, get);

			JSONObject json = new JSONObject(s);

			if (json.has("error"))
				return 0;

			if (!json.has("card"))
				return 0;

			else
				return json.getJSONObject("card").getInt("balance");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	boolean validateToken(BankObject bank, String pin, String card) {
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", getToken(bank));
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
	
	boolean cardExists(BankObject bank, String card) {
		Map<String, String> get = new HashMap<String, String>();
		
		if (card == null)
			System.out.println("WARNING card == null");

		try {
			String suffix = "/card/{CARD}/exists";
			suffix = suffix.replace("{CARD}", card);

			String s = sendGet(bank, suffix, get);

			JSONObject json = new JSONObject(s);

			if (json.has("error"))
				return false;

			else if (json.has("card"))
				if (json.getJSONObject("card").getBoolean("exists"))
					return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	boolean withdrawMoney(BankObject bank, String card, String pin, String amount) {
		Map<String, String> get = new HashMap<String, String>();
		get.put("Token", getToken(bank));
		get.put("Amount", amount);
		get.put("Pin", pin);

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

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

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

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		//System.out.println(result.toString());
		return result.toString();
	}
}