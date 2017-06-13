package me.thomasvt.bankingserver;

import java.util.Map.Entry;

import me.thomasvt.bankingserver.Bank.BankObject;

public class ExternalCard {
	
	public static void main(String[] args) {
		String carduuid = "9D47F835";
		
		ExternalCard ec = new ExternalCard();
		App.connectDatabase();
		
		long start1 = System.currentTimeMillis();
		
		System.out.println("Is local card: " + ec.isLocalCard(carduuid));
		
		long start2 = System.currentTimeMillis();
		
		BankObject b = ec.isRemoteCard(carduuid);
		
		System.out.println("Is remote card: " + b.getName());
		
		long time0 = start2 - start1;						//local
		long time1 = System.currentTimeMillis() - start1;	//
		long time2 = System.currentTimeMillis() - start2;	//remote
		
		System.out.print("Lookup took: " + time0 + "/" + time1 + "/" + time2 + "ms");
		System.out.println(" - on " + new Bank().getBankList().size() + " hosts");
	}
	
	boolean isLocalCard(String carduuid) {
		return new UserManagement().cardExists(carduuid);
	}
	
	BankObject isRemoteCard(String carduuid) {
		Bank bank = new Bank();
		
		ExternalConnect ex = new ExternalConnect();

		for (Entry<String, BankObject> entry : bank.getBankList().entrySet()) {
			if (ex.cardExists(entry.getValue(), carduuid))
				return entry.getValue();
		}
		return null;
	}
}
