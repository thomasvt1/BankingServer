package me.thomasvt.bankingserver;

import java.util.Timer;
import java.util.TimerTask;

public class TokenTimer extends TimerTask {
	
	//DELETE from `token` WHERE time - interval 5 minute > now() 
	
	public static void main(String[] args){
		  Timer timer = new Timer();
		  
		  TimerTask task = new TimerTask() {
		  @Override
		   public void run() {
		    System.out.println("Inside Timer Task" + System.currentTimeMillis());
		       }
		  };
		  
		  System.out.println("Current time" + System.currentTimeMillis());
		  timer.schedule(task, 10000,1000);
		  System.out.println("Current time" + System.currentTimeMillis());
	}

	@Override
	public void run() {
		System.out.println("TokenTimer executed");
		String sql = "DELETE from `token` WHERE time - interval 5 minute > now()";
    	
    	App.getDatabase().createStatement(sql);
    	
    	System.out.println("TokenTimer executed");
		
	}
}