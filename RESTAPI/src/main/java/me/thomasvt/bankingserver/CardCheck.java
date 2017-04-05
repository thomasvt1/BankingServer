package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

public class CardCheck {
	
	JSONObject cardExists(Map<String, String> parameters) {
		JSONObject response = new JSONObject();
		
		String cardid = parameters.get("user");
		
		boolean exists = new UserManagement().accountExists(cardid);
		
		response.put("exists", exists);
		return response;
	}
	

}
