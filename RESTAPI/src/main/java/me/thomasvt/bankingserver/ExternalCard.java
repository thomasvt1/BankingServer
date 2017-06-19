package me.thomasvt.bankingserver;

import java.util.HashMap;
import java.util.Map.Entry;

import me.thomasvt.bankingserver.Bank.BankObject;

public class ExternalCard {
	
	private HashMap<String, BankObject> map = new HashMap<String, BankObject>();
	
	public static void main(String[] args) {
		String carduuid = "9D47F835";
		
		ExternalCard ec = new ExternalCard();
		App.connectDatabase();
		
		long local_start = System.currentTimeMillis();
		
		System.out.println("Is local card: " + ec.isLocalCard(carduuid));
		
		long midpoint = System.currentTimeMillis();
		
		BankObject b = ec.isRemoteCard(carduuid);

		long remote_end = System.currentTimeMillis();
		
		b = ec.isRemoteCard(carduuid);
		
		long cache_end = System.currentTimeMillis();
		
		System.out.print("Is remote card: ");
				
		if (b != null)
			System.out.println(b.getName());
		else
			System.out.println(false);
		
		long time0 = midpoint - local_start;						//local
		long time1 = remote_end - midpoint;							//remote
		long time2 = cache_end - remote_end;						//cache
		long time3 = System.currentTimeMillis() - local_start;		//total
		
		System.out.print("Lookup took: " + time0 + "/" + time1 + "/" + time2 + "/"+ time3 + " ms");
		System.out.println(" - on " + new Bank().getBankList().size() + " hosts");
	}
	
	boolean isLocalCard(String carduuid) {
		return new UserManagement().cardExists(carduuid);
	}
	
	BankObject isRemoteCard(String carduuid) {
		Bank bank = new Bank();
		
		if (map.containsKey(carduuid))
			return map.get(carduuid);
		
		ExternalConnect ex = new ExternalConnect();

		for (Entry<String, BankObject> entry : bank.getBankList().entrySet()) {
			if (ex.cardExists(entry.getValue(), carduuid)) {
				map.put(carduuid, entry.getValue());
				return entry.getValue();
			}
		}
		return null;
	}
}
