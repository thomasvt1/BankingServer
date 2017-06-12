package me.thomasvt.bankingserver;

import java.util.UUID;

public class TokenManager {

	public String getNewToken(String ip) {
		UUID randomUUID = UUID.randomUUID();
		String UUID = randomUUID.toString();

		String sql = "INSERT INTO `token` (`accountid`, `token`, `authed`, `time`, `ip`) VALUES (NULL, '{UUID}', '0', CURRENT_TIMESTAMP, '{IP}');";

		sql = sql.replace("{UUID}", UUID);
		sql = sql.replace("{IP}", ip);

		App.getDatabase().createStatement(sql);

		return UUID;//
	}

	public boolean tokenInDatabase(String token) {
		String sql = "SELECT `token` FROM `token` WHERE `token` LIKE '" + token + "'";
		String s = App.getDatabase().selectStatement(sql);

		return s != null;
	}
	
	public String getIp(String token) {
		String sql = "SELECT `ip` FROM `token` WHERE `token` LIKE '" + token + "'";
		String s = App.getDatabase().selectStatement(sql);

		return s;
	}

	public boolean tokenValidated(String token) {
		String sql = "SELECT `authed` FROM `token` WHERE `token` LIKE '" + token + "'";
		String s = App.getDatabase().selectStatement(sql);

		if (s == null)
			return false;

		int x = Integer.parseInt(s);

		if (x == 0)
			return false;
		return true;

	}

	public void validateToken(String token) {
		String sql = "UPDATE `token` SET `authed` = '1' WHERE `token`.`token` = '" + token + "'";

		App.getDatabase().createStatement(sql);
	}

	public boolean authToken(String id, String secret) {
		String sql = "SELECT `secret` FROM `banks` WHERE `id` LIKE '" + id + "'";
		String s = App.getDatabase().selectStatement(sql);

		if (s == null)
			return false;

		if (secret.matches(s))
			return true;
		return false;
	}

}