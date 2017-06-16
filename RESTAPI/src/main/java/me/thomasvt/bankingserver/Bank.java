package me.thomasvt.bankingserver;

import java.util.HashMap;
import java.util.Map;

public class Bank {

	private BankObject SOFA = new BankObject("SOFA", "https://api.sofabank.ml",
			"aae45b32-4a99-11e7-9f16-de2b444c2004", "72fb3847-f197-4f6e-a471-78b2fc5b7e1a");

	private BankObject BANA = new BankObject("BANA", "https://api.bananabank.ml",
			"60685dae-6e73-4340-970e-925b5bea54a4", "e4b25ebd-a798-430c-bd55-9c78ea39f426");

	private BankObject WANK = new BankObject("WANK", "https://api.wallstreetwankers.ml",
			"2", "cbmJLpVq53aZ");

	private BankObject RBMB = new BankObject("RMMB", "https://api.rbmbanking.ml",
			"40ab990a-6c99-4642-97f2-1ed562df1d8d", "32601296-5686-4ae6-aa6b-21e489a02e0b");

	private BankObject PBVS = new BankObject("PPBS", "https://api.paybinvis.ml",
			"524e957d-d588-4c95-a478-8a32dc91c739", "569d8085-9df0-495e-87db-ad0fb7a224e2");
	
	private BankObject MOGE = new BankObject("PSYB", "api.themoneygenerators.ml", 
			"o2jdZbhGfapRBOOh05jsBTTi5o0yZhBYQGX04gX5HhNNUpnGJJ",
			"aJmQ1ZX8mYbt13wYDf2rBXpSx4dkmek5Bh4eBAcjmzLhX7juLs");

	private BankObject PSYB = new BankObject("PSYB", "", 
			"o2jdZbhGfapRBOOh05jsBTTi5o0yZhBYQGX04gX5HhNNUpnGJJ",
			"aJmQ1ZX8mYbt13wYDf2rBXpSx4dkmek5Bh4eBAcjmzLhX7juLs");

	final Map<String, BankObject> map = new HashMap<String, BankObject>() {
		private static final long serialVersionUID = 1L;
		{
			//put("SOFA", SOFA);
			put("BANA", BANA);
			put("WANK", WANK);
			put("RBMB", RBMB);
			put("PBVS", PBVS);
			//put("PSYB", PSYB);
			put("MOGE", MOGE);
		}
	};
	
	public Map<String, BankObject> getBankList() {
		return map;
	}
	public BankObject getBankObject(String bank) {
		if (map.get(bank) == null)
			System.out.println("ERROR: Bank not in code " + bank);
		return map.get(bank);
	}

	public class BankObject {

		private String name, url, id, secret;

		public BankObject(String name, String url, String id, String secret) {
			this.name = name;
			this.url = url;
			this.id = id;
			this.secret = secret;
		}
		
		public String getName() {
			return name;
		}

		public String getUrl() {
			return url;
		}

		public String getId() {
			return id;
		}

		public String getSecret() {
			return secret;
		}
	}
}