package me.thomasvt.bankingserver;

/*
 * AppNew uses the SPARK framework to provide fast and reliable connection.
 */

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
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
		get("/balance/:user/:pin", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			JSONObject error = authCorrect(map);
			if (error != null)
				return error;

			return new ActionGetBalance().getBalance(map);
		});

		get("/withdraw/:user/:pin/:amount", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			JSONObject error = authCorrect(map);
			if (error != null)
				return error;

			return new ActionWithdrawMoney().withdrawMoney(map);
		});

		get("/cardcheck/:user", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			JSONObject error = authCorrect(map);
			if (error != null)
				return error;

			return new ActionWithdrawMoney().withdrawMoney(map);
		});
		 */
		
		get("/token", (req, res) -> {
			String clientid = req.headers("clientid");
			String clientsecret = req.headers("clientsecret");
			
			if (clientid == null || clientsecret == null)
				return new Tools().getJsonWithError("clientid or secret not provided");
			
			if (!tm.authToken(clientid, clientsecret))
				return new Tools().getJsonWithError("clientid or secret not correct");
			
			String token = tm.getNewToken();
			
			JSONObject json = new JSONObject();
			
			JSONObject x = new JSONObject();
			
			
			
			x.put("id", token);
			x.put("expires", 300);
			
			json.put("token", x);
			
			String xy = json.toString();
			System.out.println(xy);
			
			JSONObject y = new JSONObject(xy);
			
			System.out.println(y.getJSONObject("token").get("id"));
			
			return json;
		});
		
		get("/token/validated", (req, res) -> {
			//TODO: Auth
			
			TokenManager tm = new TokenManager();
			//TODO: Token check
			String token = req.headers("token");
			
			if (token == null)
				return new Tools().getJsonWithError("no token provided");
			
			if (!tm.tokenInDatabase(token))
				return new Tools().getJsonWithError("token not in database");
			
			JSONObject response = new JSONObject();
			response.put("status", true);
			return response;
			
			
		});
		
		get("/card/:UUID", (req, res) -> {
			Map<String, String> map = requestToMap(req);
			
			TokenManager tm = new TokenManager();
			String token = req.headers("token");
			
			if (token == null)
				return new Tools().getJsonWithError("no token provided");
			
			if (!tm.tokenInDatabase(token))
				return new Tools().getJsonWithError("token not in database");
			
			String card = map.get("uuid");
			//exists: 
			return new UserManagement().cardExists(card);
		});
		
		get("/card/:UUID/enabled", (req, res) -> {
			Map<String, String> map = requestToMap(req);
			
			TokenManager tm = new TokenManager();
			String token = req.headers("token");
			
			if (token == null)
				return new Tools().getJsonWithError("no token provided");
			
			if (!tm.tokenInDatabase(token))
				return new Tools().getJsonWithError("token not in database");
			
			String card = map.get("uuid");
			
			return !new UserManagement().cardBlocked(card);
		});
		
		get("/card/:UUID/balance", (req, res) -> {
			Map<String, String> map = requestToMap(req);
			
			TokenManager tm = new TokenManager();
			String token = req.headers("token");
			
			if (token == null)
				return new Tools().getJsonWithError("no token provided");
			
			if (!tm.tokenInDatabase(token))
				return new Tools().getJsonWithError("token not in database");
			
			if (!tm.tokenValidated(token))
				return new Tools().getJsonWithError("token not validated");
			
			String card = map.get("uuid");
			
			return new ActionGetBalance().getBalance(card);
		});
		
		post("/card/:UUID/balance", (req, res) -> {
			Map<String, String> map = requestToMap(req);
			
			TokenManager tm = new TokenManager();
			String token = req.headers("token");
			
			if (token == null)
				return new Tools().getJsonWithError("no token provided");
			
			if (!tm.tokenInDatabase(token))
				return new Tools().getJsonWithError("token not in database");
			
			if (!tm.tokenValidated(token))
				return new Tools().getJsonWithError("token not validated");
			
			String card = map.get("uuid");
			String amount = req.headers("amount");
			
			return new ActionWithdrawMoney().withdrawMoney(amount, card);
		});
		
		get("/card/:UUID/validate/:PIN", (req, res) -> {
			Map<String, String> map = requestToMap(req);

			String token = req.headers("token");

			// TODO: Token check

			if (token == null)
				return new Tools().getJsonWithError("no token provided");

			String card = map.get("uuid");
			String pin = map.get("pin");

			UserManagement um = new UserManagement();

			int auth = new Tools().authUser(card, pin);

			if (um.cardBlocked(card))
				return new JSONObject().put("error", "card blocked");

			JSONObject response = new JSONObject();

			if (auth != 0) {

				um.increaseTries(card);

				int tries = um.getTries(card);

				if (tries == 3) {
					return new JSONObject().put("error", "card blocked").put("tries", 3);
				}

				response.put("tries", tries);
				response.put("error", new Tools().getErrorMessage(auth));

				return response;

			}

			new TokenManager().validateToken(token);

			response.put("status", true);

			return response;
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

	public void main(String[] args) {
		startWebServer();
		tm = new TokenManager();
	}
}