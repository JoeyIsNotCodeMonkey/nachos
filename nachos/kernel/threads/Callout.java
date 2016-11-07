package nachos.kernel.threads;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.CPU;
import nachos.machine.InterruptHandler;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Timer;

public class Callout {
    /**
     * Schedule a callout to occur at a specified number of
     * ticks in the future.
     *
     * @param runnable  A Runnable to be invoked when the specified
     * time arrives.
     * @param ticksFromNow  The number of ticks in the future at
     * which the callout is to occur.
     */
    
    private Runnable runnable;
    private static int ticksFromNow;
    private Timer timer;
    private HandleInterrupt handleInterrupt;
    private static ArrayList<Object[]> calloutList;
    private Object[] arrayElement;
    private final SpinLock spinLock;
    
    
    public Callout(){
	calloutList = new ArrayList<Object[]>();
	spinLock = new SpinLock("spin lock");
	//During system initialization, the Callout class should use Machine.getTimer(0) to access the general-purpose timer device
	timer = Machine.getTimer(0);
	handleInterrupt = new HandleInterrupt(timer);
	timer.setHandler(handleInterrupt);
	timer.start();
    }
    
    public ArrayList<Object[]> getCalloutList(){
	return calloutList;
    }
    
    public void setFinishedStatus( String threadName, boolean state){
	for(Object[] element : calloutList){
	    NachosThread temp =  (NachosThread)element[2];
	    String s=temp.name;
	    if(threadName.equals(s)){
		element[3]= state;
	    }
	    
	    
	}
    }
    
    
    
    public void schedule(Runnable runnable, int ticksFromNow){
	//you will need to disable interrupts within the schedule method while you are accessing the list of scheduled callouts
	arrayElement = new Object[4];
	arrayElement[0] = ticksFromNow;
	arrayElement[1] = runnable;
	arrayElement[2] = NachosThread.currentThread();
	arrayElement[3] = false;
//	Semaphore sem = new Semaphore("mutex", 1);
//	sem.P();
	int oldLevel = CPU.setLevel(CPU.IntOff);	// disable interrupts
	spinLock.acquire();				// exclude other CPUs
	calloutList.add(arrayElement);	
	Collections.sort(calloutList, new CustomComparator());
	//sem.V();
	spinLock.release();				// release exclusion
	CPU.setLevel(oldLevel);				// restore interrupts
	//Debug.println('+', calloutList.get(0)[0].toString());
    }
    
    
    
    private static class HandleInterrupt implements InterruptHandler {

	/** The Timer device this is a handler for. */
	private final Timer timer;
	private int timePass;
	private NachosThread t ;
	private Semaphore lock;

	/**
	 * Initialize an interrupt handler for a specified Timer device.
	 * 
	 * @param timer  The device this handler is going to handle.
	 */
	public HandleInterrupt(Timer timer) {
	    lock = new Semaphore("lock", 1);
	    this.timer = timer;
	    timePass = 0;
	}

	public void handleInterrupt() {
	   // Debug.println('+', "________________________Timer interrupt: " + timer.name);
	    
	    timePass += 100;
	    
	    if(timePass > 1000000) {
		timer.stop();
	    }
	    
	    //ticksFromNow = ticksFromNow + 100;
	    //search callout list
	    for (Object[] element : calloutList) {
		int tmp = (int) element[0];
		tmp -= 100;
		element[0] = tmp;
	    }
	    
	    
	    for (int i =0; i<calloutList.size();i++) {
		
		Object[] element = calloutList.get(i);
		int tmp = (int) element[0];
		
		if(tmp<=0){    
		    
		    t = new NachosThread("runnable thread " ,(Runnable) element[1]);
		  
		    
//		    NachosThread t = new NachosThread("Testtest thread ", (Runnable) element[1]);
//		    Nachos.scheduler.readyToRun(t);
		    
		    if(!(boolean)element[3]){
			    Nachos.scheduler.readyToRun(t);			    	  
		    
		    }
		    
		    calloutList.remove(i);
		    i--;
		    
		  //  Nachos.scheduler.finishThread();	
		     
		    
		}
		
		    
	    }
	}
	

    }
    
    public static class CustomComparator implements Comparator<Object[]> {
	   
	    @Override
	    public int compare(Object[] o1, Object[] o2) {
		// TODO Auto-generated method stub
		if((int)o1[0] < (int)o2[0])
		    return 1;
		else
		    return 0;
	    }
    }
}



