package me.thomasvt.bankingserver;

/*
 * AppNew uses the SPARK framework to provide fast and reliable connection.
 */

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import spark.Request;

public class AppNew {

	HashMap<String, String> hmap = new HashMap<String, String>();

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

		get("/", (req, res) -> {
			return "Welcome to SOFA bank!";
		});

		get("/oldapi", (req, res) -> {
			ApiHandler api = new ApiHandler();
			return api.handle(req);
		});

		get("/lol", (req, res) -> {
			JSONObject response = new JSONObject();

			response.put("testing", true);
			response.put("balance", "500");
			response.put("response", "success");

			return response;
		});

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

		get("/token/:user/:pin", (req, res) -> {
			return requestToMap(req);
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
	}
}