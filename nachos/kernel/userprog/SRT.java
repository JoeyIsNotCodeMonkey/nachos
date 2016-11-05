package nachos.kernel.userprog;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import nachos.kernel.Nachos;
import nachos.kernel.userprog.HRRN.CustomComparator;
import nachos.machine.NachosThread;
import nachos.util.FIFOQueue;
import nachos.util.Queue;

public class SRT<T> extends java.util.LinkedList<T> implements Queue<T> {
    private LinkedList<T> queue = new LinkedList<T>();


    @Override
    public boolean offer(T e) {
	if (e instanceof UserThread) {
	    UserThread currentThread = (UserThread) NachosThread.currentThread();
	    if(((UserThread)e).getRemainingTime() < currentThread.getRemainingTime()) {
		queue.addFirst(e);
		Nachos.scheduler.yieldThread();
	    }
	    
	    //sort
	    queue.offer(e);
	    Collections.sort((LinkedList<UserThread>)queue, new CustomComparator());
	    return true;
	}

	queue.offer(e);

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
