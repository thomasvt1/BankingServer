package me.thomasvt.bankingserver;

import me.thomasvt.bankingserver.Bank.BankObject;

public class TokenTimeout extends Thread {
	
	BankObject bank;
	int delay;
	
	public TokenTimeout(int delay, BankObject bank) {
		this.delay = delay;
		this.bank = bank;
	}
	
	@Override
	public void start() {
		System.out.println("Starting TokenTimeout for: " + delay);
		try {
			sleep(delay);
			App.tokens.remove(bank);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}