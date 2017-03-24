package me.thomasvt.bankingserver;

import java.util.HashMap;
import java.util.Map;

class ActionWithdrawMoney {

	public String withdrawMoney(Map<String, String> parameters) {
		Map<String, String> response = new HashMap<String, String>();
		
		String auth = new Tools().authUserOrReturnError(parameters);
		if (!auth.matches("OK"))
			return auth;
		
		if (!new Tools().isDouble(parameters.get("amount")))
			return new Tools().getJsonWithError("amount not double");
		
		String cardid = parameters.get("user");
		double balance = new UserManagement().getBalance(cardid);
		double amountToWithdraw = Double.parseDouble(parameters.get("amount"));
		
		if (balance < amountToWithdraw)
			return new Tools().getJsonWithError("not enough balance");
		if (amountToWithdraw < 0)
			return new Tools().getJsonWithError("can not withdraw negative amount");
		if (amountToWithdraw < 1)
			return new Tools().getJsonWithError("can not withdraw too small amount");
		
		new UserManagement().removeMoney(cardid, amountToWithdraw);
		
		response.put("balance", (balance - amountToWithdraw) + "");
		response.put("response", "success");
		
		return response.toString();
	}
}