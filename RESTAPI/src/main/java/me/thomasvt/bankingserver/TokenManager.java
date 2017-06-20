package me.thomasvt.bankingserver;

import java.util.UUID;

public class TokenManager {
	
	//INSERT INTO `token` (`token`, `time`, `ip`, `bank`) VALUES ('{UUID}', CURRENT_TIMESTAMP, '{IP}', 'SOFA');
	//INSERT INTO `token` (`token`, `time`, `ip`, `bank`) VALUES ('{UUID}', CURRENT_TIMESTAMP, '{IP}', (SELECT `bank` FROM `banks` WHERE `id` LIKE '{ID}'));
	public String getNewToken(String clientid, String ip) {
		UUID randomUUID = UUID.randomUUID();
		String UUID = randomUUID.toString();

		//String sql = "INSERT INTO `token` (`accountid`, `token`, `authed`, `time`, `ip`) VALUES (NULL, '{UUID}', '0', CURRENT_TIMESTAMP, '{IP}');";

		String sql = "INSERT INTO `token` (`token`, `time`, `ip`, `bank`) VALUES ('{UUID}', CURRENT_TIMESTAMP, '{IP}', (SELECT `bank` FROM `banks` WHERE `id` LIKE '{ID}'));";
		
		
		sql = sql.replace("{ID}", clientid);
		sql = sql.replace("{UUID}", UUID);
		sql = sql.replace("{IP}", ip);

		App.getDatabase().createStatement(sql);

		return UUID;
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
	
	public boolean bankPrivileged(String clientid) {
		String sql = "SELECT `privileged` FROM `banks` WHERE `id` LIKE '"+clientid+"'";
		String s = App.getDatabase().selectStatement(sql);

		return Integer.parseInt(s) == 1;
	}

	public boolean tokenValidated(String token) {
		String sql = "SELECT `authed` FROM `token` WHERE `token` LIKE '" + token + "'";
		String s = App.getDatabase().selectStatement(sql);

		if (s == null)
			return false;
		
		return Integer.parseInt(s) == 1;
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