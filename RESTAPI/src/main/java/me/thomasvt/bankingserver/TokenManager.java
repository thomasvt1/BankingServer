package me.thomasvt.bankingserver;

import java.util.UUID;

public class TokenManager {
	
	public String getNewToken() {
		UUID randomUUID = UUID.randomUUID();
		String UUID = randomUUID.toString();
		
		String sql = "INSERT INTO `token` (`accountid`, `token`, `authed`, `time`) VALUES (NULL, 'UUID', '0', CURRENT_TIMESTAMP);";
    	
		sql = sql.replace("UUID", UUID);
    	
    	App.getDatabase().createStatement(sql);
		
		return UUID;//
	}
	
	public boolean tokenInDatabase(String token) {
		String sql = "SELECT token FROM `token` WHERE `token` LIKE '"+token+"'";
        String s = App.getDatabase().selectStatement(sql);
        
        return s != null;
	}
	
	public boolean tokenValidated(String token) {
		String sql = "SELECT `authed` FROM `token` WHERE `token` LIKE '"+token+"'";
        String s = App.getDatabase().selectStatement(sql);
        
        if (s == null)
        	return false;

        int x = Integer.parseInt(s);
        
        if (x == 0)
        	return false;
        return true;
        
	}
	
	public void validateToken(String token) {
		String sql = "UPDATE `token` SET `authed` = '1' WHERE `token`.`token` = '"+token+"'";
		
		
		App.getDatabase().createStatement(sql);
		
	}

}