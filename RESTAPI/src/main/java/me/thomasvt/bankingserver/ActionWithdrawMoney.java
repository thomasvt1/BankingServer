package me.thomasvt.bankingserver;

import java.util.Map;

import org.json.JSONObject;

class ActionWithdrawMoney {
	
	public JSONObject withdrawMoney(Map<String, String> parameters) {
		String cardid = parameters.get("user");
		
		if (!new Tools().isDouble(parameters.get("amount")))
			return new JSONObject().put("error", "amount not a double");
		
		int amountToWithdraw = Integer.parseInt(parameters.get("amount"));
		
		return withdrawMoney(amountToWithdraw, cardid);
	}
	
	public JSONObject withdrawMoney(String amountToWithdraw, String cardid) {
		
		if (amountToWithdraw == null)
			return new JSONObject().put("error", "amount not provided");
		
		if (!new Tools().isDouble(amountToWithdraw))
			return new JSONObject().put("error", "amount not a double");
		
		int x = Integer.parseInt(amountToWithdraw);
		
		return withdrawMoney(x, cardid);
	}

	public JSONObject withdrawMoney(int amountToWithdraw, String cardid) {
		JSONObject response = new JSONObject();
		
		double balance = new UserManagement().getBalance(cardid);
		
		if (balance < amountToWithdraw)
			return new JSONObject().put("error", "not enough balance");
		if (amountToWithdraw < 0)
			return new JSONObject().put("error", "can not withdraw negative amount");
		if (amountToWithdraw < 1)
			return new JSONObject().put("error", "can not withdraw too small amount");
		
		new UserManagement().removeMoney(cardid, amountToWithdraw);
		
		int finalmoney = (int) ((balance - amountToWithdraw) * 100);
		
		response.put("balance", finalmoney + "");
		response.put("response", "success");
		
		return response;
	}
}