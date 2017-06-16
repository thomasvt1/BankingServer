package me.thomasvt.bankingserver;

import java.sql.ResultSet;
import java.sql.SQLException;

class UserManagement {

	/*
	 * Method to add new user to the database
	 */
	int createUser(User user) {
		try {
			// String hashedPin = new Hashing().hashString(pin);

			int newUserID = getNewUserID();
			// INSERT INTO `user` (`userid`, `firstname`, `lastname`,
			// `birthdate`, `zipcode`, `housenumber`, `residence`, `mobnumber`)
			// VALUES (NULL, 'Bob', 'Dillan', '2017-04-01', '6666XD', '69',
			// 'Hell', '062353454');
			String sql = "INSERT INTO `user` (`userid`, `firstname`, `lastname`, `birthdate`, `zipcode`, `housenumber`, `residence`, `mobnumber`) VALUES (NULL, 'FIRSTNAME', 'LASTNAME', 'BIRTHDATE', 'ZIPCODE', 'HOUSENUMBER', 'RESIDENCE', 'MOBNUMBER');";

			sql = sql.replace("NULL", newUserID + "");
			sql = sql.replace("FIRSTNAME", user.getFirstname());
			sql = sql.replace("LASTNAME", user.getLastname());
			sql = sql.replace("BIRTHDATE", user.getBirthdate());
			sql = sql.replace("ZIPCODE", user.getZipcode());
			sql = sql.replace("HOUSENUMBER", user.getHousenumber());
			sql = sql.replace("RESIDENCE", user.getResidence());
			sql = sql.replace("MOBNUMBER", user.getMobnumber());

			App.getDatabase().createStatement(sql);

			return newUserID;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	boolean useridExists(int userid) {
		String sql = "SELECT userid FROM `user` WHERE `userid` LIKE '" + userid + "'";
		String s = App.getDatabase().selectStatement(sql);

		return s != null;
	}

	void addAccount(int userid, double amount) {
		String sql = "INSERT INTO `account` (`accountid`, `userid`, `balance`) VALUES (NULL, '" + userid + "', '"
				+ amount + "');";

		App.getDatabase().createStatement(sql);
	}

	int getAccountId(String carduuid) {
		String sql = "SELECT `accountid` FROM `card` WHERE `carduuid` LIKE '" + carduuid + "'";

		String response = App.getDatabase().selectStatement(sql);
		return Integer.parseInt(response);
	}

	int getUserId(int accountid) {
		String sql = "SELECT `userid` FROM `account` WHERE `accountid` = `ACCOUNTID`;";
		sql = sql.replace("ACCOUNTID", accountid + "");

		String response = App.getDatabase().selectStatement(sql);
		return Integer.parseInt(response);
	}

	int getUserId(String carduuid) {
		String sql = "SELECT `userid` FROM `account` WHERE `accountid` = (SELECT `accountid` FROM `card` where `carduuid` = 'CARDUUID');";
		sql = sql.replace("CARDUUID", carduuid);

		String response = App.getDatabase().selectStatement(sql);
		return Integer.parseInt(response);
	}

	User getUser(String carduuid) {
		if (!cardExists(carduuid))
			return null;

		int id = getUserId(carduuid);

		String sql = "SELECT * FROM `user` WHERE `userid` = " + id + ";";

		try {
			ResultSet rs = App.getDatabase().getStmt().executeQuery(sql);
			while (rs.next()) {
				String firstname = rs.getString("firstname");
				String lastname = rs.getString("lastname");
				String birthdate = rs.getString("birthdate");
				String zipcode = rs.getString("zipcode");
				String housenumber = rs.getString("housenumber");
				String residence = rs.getString("residence");
				String mobnumber = rs.getString("mobnumber");
				return new User(firstname, lastname, zipcode, housenumber, residence, mobnumber, birthdate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	void addMoney(String cardid, int money) {
		String sql = "UPDATE `account` SET `balance` = balance + {m} WHERE `account`.`accountid` = (SELECT accountid FROM card WHERE carduuid = '{uuid}')";
		sql = sql.replace("{m}", money + "").replace("{uuid}", cardid);

		App.getDatabase().createStatement(sql);
		log(0, cardid, "DEPOSIT", money);
	}

	void removeMoney(String cardid, double money) {
		String sql = "UPDATE `account` SET `balance` = balance - {m} WHERE `account`.`accountid` = (SELECT accountid FROM card WHERE carduuid = '{uuid}')";
		sql = sql.replace("{m}", money + "").replace("{uuid}", cardid);

		App.getDatabase().createStatement(sql);
		log(0, cardid, "WITHDRAW", money);
	}

	int getTries(String cardid) {
		String sql = "SELECT `tries` FROM `card` WHERE `carduuid` = '" + cardid + "'";
		
		return Integer.parseInt(App.getDatabase().selectStatement(sql));
	}

	boolean cardBlocked(String cardid) {
		int tries = getTries(cardid);

		if (tries > 2)
			return true;
		else
			return false;
	}

	void increaseTries(String cardid) {
		String sql = "UPDATE `card` SET `tries` = `tries` + 1 WHERE `card`.`carduuid` = '" + cardid + "'";
		log(0, cardid, "WRONGPIN", 0);

		App.getDatabase().createStatement(sql);
	}

	void resetTries(String cardid) {
		String sql = "UPDATE `card` SET `tries` = '0' WHERE `card`.`carduuid` = '" + cardid + "'";

		App.getDatabase().createStatement(sql);
	}

	boolean cardExists(String carduuid) {
		String sql = "SELECT accountid FROM `card` WHERE `carduuid` LIKE '" + carduuid + "'";
		String s = App.getDatabase().selectStatement(sql);

		return s != null;
	}

	private int getNewUserID() {
		String sql = "SELECT MAX(`userid`) FROM user;";
		String s = App.getDatabase().selectStatement(sql);

		if (s == null)
			return 1;

		try {
			return Integer.parseInt(s) + 1;
		} catch (Exception e) {
			return 1;
		}
	}

	double getBalance(String cardid) {
		String sql = "SELECT balance FROM card, account WHERE card.accountid=account.accountid AND carduuid='" + cardid
				+ "'";
		String s = App.getDatabase().selectStatement(sql);

		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return 0;
		}
	}

	String getFirstName(int user) {
		String sql = "SELECT `firstname` FROM `userinfo` WHERE `userid` = " + user + "";
		return App.getDatabase().selectStatement(sql);
	}

	String getLastName(int user) {
		String sql = "SELECT `lastname` FROM `userinfo` WHERE `userid` = " + user + "";
		return App.getDatabase().selectStatement(sql);
	}

	String getPinHash(String cardid) {
		String sql = "SELECT `pinhash` FROM `card` WHERE `carduuid` = '" + cardid + "'";
		return App.getDatabase().selectStatement(sql);
	}

	private void log(int userid, String carduuid, String action, double amount) {
		if (userid == 0)
			userid = getAccountId(carduuid);

		String sql = "INSERT INTO `log` VALUES (NULL, 'USERID', 'CARDUUID', 'ACTION', AMOUNT, CURRENT_TIMESTAMP)";

		sql = sql.replace("USERID", userid + "");
		sql = sql.replace("CARDUUID", carduuid);
		sql = sql.replace("ACTION", action);
		if (amount == 0)
			sql = sql.replace("AMOUNT", "NULL");
		else
			sql = sql.replace("AMOUNT", "'" + amount + "'");

		App.getDatabase().createStatement(sql);
	}

	void setPin(User user, String carduuid, String pin) {
		try {
			String hashedPin = new Tools().getSaltedPin(user, carduuid, pin);
			String sql = "UPDATE `card` SET `pinhash` = '" + hashedPin + "' WHERE `card`.`carduuid` = '" + carduuid
					+ "';";

			App.getDatabase().createStatement(sql);

			log(0, carduuid, "SETPIN", 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}