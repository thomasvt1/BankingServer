package me.thomasvt.bankingserver;

import java.sql.SQLException;
import java.util.TimerTask;

public class TokenTimer extends TimerTask {

	// DELETE from `token` WHERE time - interval 5 minute > now()

	// SELECT * FROM `token` WHERE `time` <= CURRENT_TIMESTAMP - INTERVAL 5
	// MINUTE

	@Override
	public void run() {
		String sql = "DELETE FROM `token` WHERE `time` <= CURRENT_TIMESTAMP - INTERVAL 5 MINUTE";

		try {
			App.getDatabase().getStmt().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}