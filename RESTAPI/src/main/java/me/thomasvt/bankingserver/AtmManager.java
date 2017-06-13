package me.thomasvt.bankingserver;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

/*
 * The AtmManager will keep track of all ATM activity.
 * For example, but not limited to: bills in the ATM, last ping to server.
 */

public class AtmManager {
	
	public static void main(String[] args) {
		AtmManager atm = new AtmManager();
		
		String ATMNAME = "THOMAS1";
		
		JSONObject x = atm.getMoneyStatus(ATMNAME);
		
		System.out.println(x);
		
		if (x != null && x.length() != 0) {
			System.out.println("10: " + x.getInt("10"));
			System.out.println("20: " + x.getInt("20"));
			System.out.println("50: " + x.getInt("50"));
		}
		
		atm.updateLastping(ATMNAME);
	}
	
	JSONObject getMoneyStatus(String ATMNAME) {
		String sql = "SELECT * FROM `atm` WHERE `name` LIKE '"+ATMNAME+"'";
		
		String amnt10 = null, amnt20 = null, amnt50 = null; // 10, 20, 50

		try {
			ResultSet rs = App.getDatabase().getStmt().executeQuery(sql);
			while (rs.next()) {
				amnt10 = rs.getString("10");
				amnt20 = rs.getString("20");
				amnt50 = rs.getString("50");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		JSONObject json = new JSONObject();
		json.put("10", amnt10);
		json.put("20", amnt20);
		json.put("50", amnt50);
		return json;
	}
	
	//SELECT `id` FROM `atm` WHERE `name` LIKE 'THOMAS1'
	boolean atmExists(String ATMNAME) {
		String sql = "SELECT `id` FROM `atm` WHERE `name` LIKE '"+ATMNAME+"'";
		String s = App.getDatabase().selectStatement(sql);

		return s != null;
	}
	
	
	//INSERT INTO `atm` (`id`, `name`) VALUES (NULL, 'BOOPS');
	void addAtm(String ATMNAME) {
		String sql = "INSERT INTO `atm` (`id`, `name`) VALUES (NULL, '"+ATMNAME+"');";
		System.out.println("Added new ATM: " + ATMNAME);

		App.getDatabase().createStatement(sql);
	}
	
	//UPDATE `atm` SET `lastping`= CURRENT_TIMESTAMP WHERE `name` LIKE 'THOMAS1'
	
	void updateLastping(String ATMNAME) {
		String sql = "UPDATE `atm` SET `lastping`= CURRENT_TIMESTAMP WHERE `name` LIKE '"+ATMNAME+"'";
		
		if (!atmExists(ATMNAME))
			addAtm(ATMNAME);

		App.getDatabase().createStatement(sql);
	}
}