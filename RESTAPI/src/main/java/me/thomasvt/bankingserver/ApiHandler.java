package me.thomasvt.bankingserver;

import java.io.IOException;
import java.util.Map;

import spark.Request;

import org.apache.commons.lang3.SystemUtils;

public class ApiHandler {
	
	public String handle(Request req) throws IOException {
		System.out.println("Connection from: " + getIp(req));
		System.out.println(req.queryString());
		
		String query = req.queryString();
		
		if (checkParameters(query)) {
			//returnPage(he, new Tools().getJsonWithError("NO PARAMETERS"));
			return new Tools().getJsonWithError("NO PARAMETERS");
		}
			
		if (!keyCorrect(query)) {
			//returnPage(he, new Tools().getJsonWithError("BAD KEY"));
			return new Tools().getJsonWithError("BAD KEY");
		}
		
		if (actionProvided(query)) {
			//returnPage(he, new ActionParser().actionParser(query));
			return new ActionParser().actionParser(query);
		}
		
		//returnPage(he, new Tools().getJsonWithError("We Dont Know What Happened Bank"));
		
		return new Tools().getJsonWithError("We Dont Know What Happened Bank");
	}
	/*
	public void handle(HttpExchange he) throws IOException {
		System.out.println("Connection from: " + getIp(he));
		System.out.println(he.getRequestURI());
		
		if (checkParameters(he)) {
			returnPage(he, new Tools().getJsonWithError("NO PARAMETERS"));
			return;
		}
			
		if (!keyCorrect(he)) {
			returnPage(he, new Tools().getJsonWithError("BAD KEY"));
			return;
		}
		
		if (actionProvided(he)) {
			returnPage(he, new ActionParser().actionParser(he));
		}
		
		returnPage(he, new Tools().getJsonWithError("We Dont Know What Happened Bank"));
		
	}
	*/
	
	/*
	 * Returns the IP of the connection. Even when behind (Apache) proxy.
	 */
	private String getIp(Request req) {
		if (SystemUtils.IS_OS_LINUX)
			return req.headers("X-forwarded-for");
			//return he.getRequestHeaders().getFirst("X-forwarded-for");
		else
			return req.ip();
			//return he.getRemoteAddress().getAddress()+"";
	}
	
	/*
	 * Checks if HttpExchange contains the parameter 'action'
	 */
	boolean actionProvided(String query) {
		Map<String, String> parameters;
		//req.queryString()
		parameters = new Tools().queryToMap(query);
		//parameters = new Tools().queryToMap(he.getRequestURI().getQuery());

		return parameters.get("action") != null;
	}

	/*
	 * Method called when want to send generated data to client.
	 */
	/*
	private void returnPage(HttpExchange he, String response) throws IOException {
		he.sendResponseHeaders(200, response.length());
		OutputStream os = he.getResponseBody();
		os.write(response.getBytes());
		os.close();		
	}
	*/
	
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

		return new Tools().matchRequestKey(parameters.get("key"));
	}
}