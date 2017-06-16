package me.thomasvt.bankingserver;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

/*
 * The AtmManager will keep track of all ATM activity.
 * For example, but not limited to: bills in the ATM, last ping to server.
 */

public class AtmManager {
	JSONObject getMoneyStatus(String ATMNAME) {
		String sql = "SELECT * FROM `atm` WHERE `name` LIKE '"+ATMNAME+"'";
		
		String amnt10 = null, amnt20 = null, amnt50 = null; // 10, 20, 50

		try {
			ResultSet rs = App.getDatabase().getStmt().executeQuery(sql);
			while (rs.next()) {
				amnt10 = rs.getString("ten");
				amnt20 = rs.getString("twenty");
				amnt50 = rs.getString("fifty");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		JSONObject json = new JSONObject();
		json.put("ten", amnt10);
		json.put("twenty", amnt20);
		json.put("fifty", amnt50);
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
	
	private String fillBillSql(boolean add, String ATMNAME, int ten, int twenty, int fifty) {
		String sql = "UPDATE `atm` SET `ten` = `ten` + '{10}', `twenty` = `twenty` + '{20}', `fifty` = `fifty` + '{50}' WHERE `atm`.`name` = {ID};";

		sql = sql.replace("{ID}", ATMNAME);

		sql = sql.replace("{10}", String.valueOf(ten));
		sql = sql.replace("{20}", String.valueOf(twenty));
		sql = sql.replace("{50}", String.valueOf(fifty));
		
		if (!add)
			sql = sql.replaceAll("\\+", "-");
		return sql;
	}
	
	//UPDATE `atm` SET `ten` = `ten` + '20', `twenty` = `twenty` + '20', `fifty` = `fifty` + '20' WHERE `atm`.`id` = 13;
	void addMoney(String ATMNAME, int ten, int twenty, int fifty) {
		String sql = fillBillSql(true, ATMNAME, ten, twenty, fifty);
		
		App.getDatabase().createStatement(sql);
	}
	
	//UPDATE `atm` SET `ten` = `ten` + '20', `twenty` = `twenty` + '20', `fifty` = `fifty` + '20' WHERE `atm`.`id` = 13;
	void removeMoney(String ATMNAME, int ten, int twenty, int fifty) {
		String sql = fillBillSql(false, ATMNAME, ten, twenty, fifty);
		
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