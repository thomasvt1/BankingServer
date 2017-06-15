package me.thomasvt.bankingserver;

/*
 * AppNew uses the SPARK framework to provide fast and reliable connection.
 */

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.SystemUtils;
import org.json.JSONObject;

import spark.Request;

public class AppNew {

	HashMap<String, String> hmap = new HashMap<String, String>();

	static TokenManager tm;

	static void log(String s) {
		System.out.println(s);
	}

	static Map<String, String> requestToMap(Request req) {
		Map<String, String> response = new HashMap<String, String>();
		JSONObject json = new JSONObject(req.params());

		for (Entry<String, Object> s : json.toMap().entrySet())
			response.put(s.getKey().substring(1), s.getValue().toString());
		return response;
	}

	static public String getJsonWithError(String error) {
		Map<String, String> response = new HashMap<String, String>();
		response.put("error", error);
		return new JSONObject(response).toString();
	}

	static void startWebServer() {
		port(9010);

		get("/oldapi", (req, res) -> {
			ApiHandler api = new ApiHandler();
			return api.handle(req);
		});
		
		/*
		 * Returns a token when requested
		 */
		get("/token", (req, res) -> {
			String clientid = req.headers("Client-Id");
			String clientsecret = req.headers("Client-Secret");

			if (clientid == null || clientsecret == null)
				return new Tools().getJsonWithError(1, "clientid or secret not provided");

			if (!tm.authToken(clientid, clientsecret))
				return new Tools().getJsonWithError(1, "clientid or secret not correct");

			String token = tm.getNewToken(getIp(req));

			JSONObject json = new JSONObject();
			JSONObject x = new JSONObject();

			x.put("id", token);
			x.put("expires", 300);

			json.put("token", x);

			return json;
		});

		get("/card/:UUID/exists", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			String card = map.get("uuid");

			JSONObject json = new JSONObject();

			JSONObject x = new JSONObject();
			x.put("id", card);
			x.put("exists", new UserManagement().cardExists(card));

			json.put("card", x);

			return json;
		});

		get("/card/:UUID/enabled", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			String token = req.headers("Token");

			JSONObject auth = authToken(token, getIp(req), false);

			if (auth != null)
				return auth;

			String card = map.get("uuid");

			UserManagement um = new UserManagement();

			if (!um.cardExists(card))
				return new Tools().getJsonWithError(22, "card does not exist");

			JSONObject json = new JSONObject();

			JSONObject x = new JSONObject();
			x.put("id", card);
			x.put("enabled", !um.cardBlocked(card));
			x.put("tries", um.getTries(card));

			json.put("card", x);

			return json;
		});

		get("/card/:UUID/balance", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			String token = req.headers("Token");

			JSONObject auth = authToken(token, getIp(req), true);

			if (auth != null)
				return auth;

			String card = map.get("uuid");

			JSONObject json = new JSONObject();

			JSONObject x = new JSONObject();
			x.put("id", card);
			x.put("balance", new ActionGetBalance().getIntBalance(card));

			json.put("card", x);

			return json;
		});

		post("/card/:UUID/balance", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			String token = req.headers("Token");

			JSONObject auth = authToken(token, getIp(req), true);

			if (auth != null)
				return auth;

			String card = map.get("uuid");
			String amount = req.headers("Amount");

			double amnt = 0;

			try {
				int x = Integer.parseInt(amount);

				amnt = x / 100.0;

				if (amnt > 0)
					return new Tools().getJsonWithError(99, "You are not authorized to add money to the acccount");
				amnt = amnt * -1;
			} catch (Exception e) {
				return new Tools().getJsonWithError(99, "Amount specified not int");
			}

			return new ActionWithdrawMoney().withdrawMoney(amnt, card);
		});

		get("/card/:UUID/validate", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			String token = req.headers("Token");
			String pin = req.headers("Pin");

			JSONObject auth = authToken(token, getIp(req), false);

			if (auth != null)
				return auth;
			
			/*
			if (tm.tokenValidated(token))
				return new Tools().getJsonWithError(99, "token already verified!");
*/

			String card = map.get("uuid");

			UserManagement um = new UserManagement();

			int authtry = new Tools().authUser(card, pin);

			if (um.cardBlocked(card))
				return new Tools().getJsonWithError(18, "card blocked");

			if (authtry != 0) {
				um.increaseTries(card);

				int tries = um.getTries(card);

				if (tries == 3) {
					JSONObject json = new Tools().getJsonWithError(18, "card blocked");

					json.getJSONObject("error").put("tries", 3);

					return json;
				}

				JSONObject json = new JSONObject();

				JSONObject x = new JSONObject();

				x.put("tries", tries);
				x.put("message", new Tools().getErrorMessage(authtry));

				json.put("error", x);

				return json;
			}

			new TokenManager().validateToken(token);

			JSONObject json = new JSONObject();

			JSONObject x = new JSONObject();

			x.put("id", card);
			x.put("validate", true);
			x.put("tries", new UserManagement().getTries(card));

			json.put("card", x);

			return json;
		});

		get("/ping/:ATM", (req, res) -> {
			Map<String, String> map = requestToMap(req);
			
			String clientid = req.headers("Client-Id");
			String clientsecret = req.headers("Client-Secret");
			
			JSONObject auth = bankPrivileged(clientid, clientsecret);
			
			if (auth != null)
				return auth;
			
			AtmManager atm = new AtmManager();
			
			atm.updateLastping(map.get("ATM"));
			
			return atm.getMoneyStatus(map.get("ATM"));
		});
		
		get("*", (req, res) -> {
			return "Welcome to SOFA bank!";
		});
	}

	static JSONObject authCorrect(Map<String, String> map) {
		String cardid = map.get("user");

		int auth = new Tools().authUser(cardid, map.get("pin"));

		if (new UserManagement().cardBlocked(cardid))
			return new JSONObject().put("error", "card blocked");

		if (auth != 0) {
			JSONObject response = new JSONObject();

			new UserManagement().increaseTries(cardid);

			int tries = new UserManagement().getTries(cardid);

			if (tries == 3) {
				return new JSONObject().put("error", "card blocked").put("tries", 3);
			}

			response.put("tries", tries);
			response.put("error", new Tools().getErrorMessage(auth));

			return response;
		}
		return null;
	}
	
	/*
	 * Returns the IP of the connection. Even when behind (Apache)
	 * reverse-proxy.
	 */
	private static String getIp(Request req) {
		if (SystemUtils.IS_OS_LINUX)
			return req.headers("X-forwarded-for");
		else
			return req.ip();
	}

	/*
	 * Returns a JSONObject with an error or will return null
	 * (token, ip, should check if token is validated)
	 */
	private static JSONObject authToken(String token, String ip, boolean validated) {
		
		if (token == null)
			return new Tools().getJsonWithError(8, "no token provided");
		
		else if (!tm.tokenInDatabase(token))
			return new Tools().getJsonWithError(8, "token not in database");
		
		else if (ip != null)
			if (!ip.matches(tm.getIp(token)))
				return new Tools().getJsonWithError(8, "token validated on different ip"); 

		else if (!tm.tokenValidated(token) && validated)
			return new Tools().getJsonWithError(8, "token not validated");
		return null;
	}
	
	/*
	 * Returns a JSONObject with an error or will return null
	 * (token, ip, should check if token is validated)
	 */
	private static JSONObject bankPrivileged(String clientid, String clientsecret) {
		
		if (clientid == null || clientsecret == null)
			return new Tools().getJsonWithError(8, "no token provided");
		
		else if (!tm.authToken(clientid, clientsecret))
			return new Tools().getJsonWithError(8, "clientid or secret not correct"); 

		else if (!tm.bankPrivileged(clientid))
			return new Tools().getJsonWithError(8, "token not privilleged");
		
		return null;
	}

	public void main(String[] args) {
		startWebServer();
		tm = new TokenManager();
	}
}