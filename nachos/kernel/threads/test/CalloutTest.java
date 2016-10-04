package nachos.kernel.threads.test;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.Machine;
import nachos.machine.NachosThread;

public class CalloutTest {
    public static void start() {
	
	
	Debug.println('t', "Starting Callout Test");
	NachosThread thread1 = new NachosThread("thread1 " ,new Runnable() {
	    @Override 
	    public void run() {
		
		
		//Debug.printf('+',"[producer] published event : %s \n", event);
				
		Nachos.scheduler.sleepThread(300);
		
		Debug.println('t', "_____________FINSHING sleepthread test");
		Nachos.scheduler.finishThread();
	    }
	});
	
	
	
	NachosThread thread2 = new NachosThread("thread2 " ,new Runnable() {
	    @Override 
	    public void run() {
		
		
		//Debug.printf('+',"[producer] published event : %s \n", event);
				
		Nachos.scheduler.sleepThread(100);
		Nachos.scheduler.finishThread();
	    }
	});
	
	Nachos.scheduler.readyToRun(thread1);
	//Nachos.scheduler.readyToRun(thread2);
    }
}
