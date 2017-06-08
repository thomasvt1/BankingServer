package me.thomasvt.bankingserver;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Commands {

	static Scanner in = new Scanner(System.in);
	boolean noException = true;

	void keepServiceOn() {
		while (App.getServerShutdown() && noException) {
			try {
				listener();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void listener() {
		System.out.println("Please input next command");
		String input;
		try {
			input = in.nextLine();
		} catch (NoSuchElementException e) {
			noException = false;
			System.out.println("COMMANDS DISABLED - RUNNING INSIDE CONTAINER?");
			return;
		}

		if (input.equalsIgnoreCase("adduser")) {
			System.out.println("Please input first name:");
			String firstname = in.nextLine();

			System.out.println("Please input last name:");
			String lastname = in.nextLine();

			System.out.println("Please input zipcode:");
			String zipcode = in.nextLine();

			System.out.println("Please input housenumber:");
			String housenumber = in.nextLine();

			System.out.println("Please input residence:");
			String residence = in.nextLine();

			System.out.println("Please input mobile number:");
			String mobnumber = in.nextLine();

			System.out.println("Please input DOB like: 2017-04-03");
			String birthdate = in.nextLine();

			User user = new User(firstname, lastname, zipcode, housenumber, residence, mobnumber, birthdate);

			int i = new UserManagement().createUser(user);
			System.out.println("Account created. Unique ID: " + i);
		} else if (input.equalsIgnoreCase("addaccount")) {
			System.out.println("Please input account id:");
			int userid = in.nextInt();

			if (!new UserManagement().useridExists(userid)) {
				System.out.println("That userid does not exist");
				return;
			}

			System.out.println("Please input balance:");
			double balance = in.nextDouble();

			new UserManagement().addAccount(userid, balance);
		} else if (input.equalsIgnoreCase("getname")) {
			System.out.println("Please input caruuid:");
			String carduuid = in.nextLine();

			if (!new UserManagement().cardExists(carduuid)) {
				System.out.println("That caruuid does not exist");
				return;
			}
			User user = new UserManagement().getUser(carduuid);

			System.out.println("First name: " + user.getFirstname());
			System.out.println("Last name: " + user.getLastname());
		} else if (input.equalsIgnoreCase("unblock")) {
			System.out.println("Please input user:");
			String cardid = in.nextLine();

			if (!new UserManagement().cardExists(cardid)) {
				System.out.println("That card does not exist");
				return;
			}

			new UserManagement().resetTries(cardid);
			System.out.println("Card unblocked");

		} else if (input.equalsIgnoreCase("addmoney")) {
			System.out.println("Please input user:");
			String user = in.nextLine();

			System.out.println("Please input money:");
			int money = Integer.parseInt(in.nextLine());

			new UserManagement().addMoney(user, money);

		} else if (input.equalsIgnoreCase("getpinhash")) {
			System.out.println("Please input user:");
			String cardid = in.nextLine();

			System.out.println(new UserManagement().getPinHash(cardid));
		} else if (input.equalsIgnoreCase("setpin")) {
			System.out.println("Please input carduuid:");
			String carduuid = in.nextLine();

			if (!new UserManagement().cardExists(carduuid)) {
				System.out.println("User does not exist ");
				return;
			}

			User user = new UserManagement().getUser(carduuid);

			System.out.println("Please input new pin:");
			String pin = in.nextLine();

			new UserManagement().setPin(user, carduuid, pin);

		} else if (input.equalsIgnoreCase("getmoney")) {
			System.out.println("Please input user:");
			String cardid = in.nextLine();

			System.out.println(new UserManagement().getBalance(cardid));

		} else if (input.equalsIgnoreCase("encryptw")) {
			System.out.println("Please enter input:");
			String s = in.nextLine();

			System.out.println("Please enter key:");
			String key = in.nextLine();

			try {
				System.out.println(new Encryption(key).encrypt(s));
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (input.equalsIgnoreCase("status")) {
			System.out.println("Connection status;");
			App.getDatabase().printStatusOfConnection();

		} else if (input.equalsIgnoreCase("removemoney")) {
			System.out.println("Please input user:");
			String cardid = in.nextLine();

			System.out.println("Please input money:");
			int money = Integer.parseInt(in.nextLine());

			new UserManagement().removeMoney(cardid, money);
		} else if (input.equalsIgnoreCase("stop")) {
			System.out.println("Stopping server...");
			App.shutdownServer();
		}
	}
}