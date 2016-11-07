package nachos.kernel.userprog;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.kernel.userprog.HRRN.CustomComparator;
import nachos.machine.NachosThread;
import nachos.util.FIFOQueue;
import nachos.util.Queue;

public class SRT<T> extends java.util.LinkedList<T> implements Queue<T> {
    private LinkedList<T> queue = new LinkedList<T>();


    @Override
    public boolean offer(T e) {
	
	
	
	
	
	if (e instanceof UserThread && !this.isEmpty()) {
	    UserThread currentThread = (UserThread) NachosThread.currentThread();
	    
	    Debug.println('+', "***************Remaining Time for newly created Thread: "+((UserThread)e).getRemainingTime() ); 
		Debug.println('+', "***************Remaining Time for current Thread: "+currentThread.getRemainingTime() ); 
	
	    
	    if(((UserThread)e).getRemainingTime() < currentThread.getRemainingTime()) {
		
		
		
			
		
		
		queue.addFirst(e);
		Nachos.scheduler.yieldThread();
		
		
		
		
		
	    }
	    
	    //sort
	    this.add(e);
	    Collections.sort((LinkedList<UserThread>)this, new CustomComparator());
	    return true;
	}

	
	
	
	this.add(e);

	return true;
    }
    
    public static class CustomComparator implements Comparator<UserThread> {	  
	    @Override
	    public int compare(UserThread o1, UserThread o2) {
		
		if(o1.getRemainingTime() > o2.getRemainingTime()) {
		    return 1;
		}
		
		if(o1.getRemainingTime() == o2.getRemainingTime()) {
		    return 0;
		}
		
		return -1;
	    }
}
}
