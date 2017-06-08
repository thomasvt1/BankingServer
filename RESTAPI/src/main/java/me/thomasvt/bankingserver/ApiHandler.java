package me.thomasvt.bankingserver;

import java.io.IOException;
import java.util.Map;

import spark.Request;

import org.apache.commons.lang3.SystemUtils;
import org.json.JSONObject;

public class ApiHandler {

	public JSONObject handle(Request req) throws IOException {
		System.out.println("Connection from: " + getIp(req));
		System.out.println(req.queryString());

		String query = req.queryString();

		if (checkParameters(query))
			return new JSONObject().put("error", "NO PARAMETERS");

		if (!keyCorrect(query))
			return new JSONObject().put("error", "BAD KEY");

		if (actionProvided(query))
			return new ActionParser().actionParser(query);

		return new JSONObject().put("error", "We Don't Know What Happened Bank");
	}

	/*
	 * Returns the IP of the connection. Even when behind (Apache)
	 * reverse-proxy.
	 */
	private String getIp(Request req) {
		if (SystemUtils.IS_OS_LINUX)
			return req.headers("X-forwarded-for");
		else
			return req.ip();
	}

	/*
	 * Checks if HttpExchange contains the parameter 'action'
	 */
	boolean actionProvided(String query) {
		Map<String, String> parameters;
		parameters = new Tools().queryToMap(query);

		return parameters.get("action") != null;
	}

	/*
	 * Checks if parameters were given.
	 */
	private boolean checkParameters(String query) {
		return query == null;
	}

	/*
	 * Checks if the key (time based) matches to ensure safe communication.
	 */
	boolean keyCorrect(String query) {
		Map<String, String> parameters;
		parameters = new Tools().queryToMap(query);

		if (parameters.get("key") == null)
			return false;

		// TODO: Disable debug
		if (parameters.get("key").matches("debug"))
			return true;

		return new Tools().matchRequestKey(parameters.get("key"));
	}
}