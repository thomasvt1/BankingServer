package me.thomasvt.bankingserver;

import java.sql.SQLException;
import java.util.Timer;

public class App {
	
	private static Database db = null;
	private static boolean serverEnable = true;
	static int databaseCalled = 0;
	public static final int port = 9010;
	
	public static void main(String[] args) {
		System.out.println("Starting...");
		db = new Database();
		db.startConnection();
		db.printStatusOfConnection();
		
		new AppNew().main(args);
		
		Timer timer = new Timer();
		timer.schedule(new TokenTimer(), 1000, 60000);
		
		new Commands().keepServiceOn();
	}

	protected static Database getDatabase() {
		if (db == null)
			db = new Database();
		try {
			if (db.getConn().isClosed())
				db.startConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return db;
	}
	
	protected static void shutdownServer() {
		serverEnable = false;
		System.exit(1);
	}
	
	protected static boolean getServerShutdown() {
		return serverEnable;
	}
}