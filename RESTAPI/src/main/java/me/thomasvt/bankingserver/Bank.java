package me.thomasvt.bankingserver;

public class Bank {

	BankObject SOFA = new BankObject("https://api.sofabank.ml", "aae45b32-4a99-11e7-9f16-de2b444c2004",
			"72fb3847-f197-4f6e-a471-78b2fc5b7e1a");
	
	BankObject BANA = new BankObject("https://api.bananabank.ml", "60685dae-6e73-4340-970e-925b5bea54a4", "e4b25ebd-a798-430c-bd55-9c78ea39f426");

	public class BankObject {

		private String url, id, secret;

		public BankObject(String url, String id, String secret) {
			this.url = url;
			this.id = id;
			this.secret = secret;
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