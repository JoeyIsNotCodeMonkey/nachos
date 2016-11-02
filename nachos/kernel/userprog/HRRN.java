package nachos.kernel.userprog;

import java.util.Collections;
import java.util.Comparator;

import nachos.Debug;
import nachos.kernel.userprog.SPN.CustomComparator;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Timer;
import nachos.util.FIFOQueue;
import nachos.util.Queue;
import nachos.util.ReadyList;

public class HRRN<T> extends java.util.LinkedList<T> implements ReadyList<T>{
    
    private Queue<T> queue;
    private static HRRN<NachosThread> hrrn;
    private Timer timer;
    
    public HRRN() {
	queue = new FIFOQueue<T>();
	
    }
    
    public static HRRN<NachosThread> getInstance() {
	if(hrrn == null) 
	    hrrn = new HRRN<NachosThread>();
	return hrrn;
    }

    public Queue<T> getQueue() {
	return queue;
    }

    public void setQueue(Queue<T> queue) {
	this.queue = queue;
    }
    
    @Override
    public T poll(){
	

	
	Collections.sort((FIFOQueue<UserThread>)queue, new CustomComparator());
	
	if(queue.isEmpty())
	    return null;
	
	return (T)queue.poll();
	
	
    }
    
    public static class CustomComparator implements Comparator<UserThread> {	  
	    @Override
	    public int compare(UserThread o1, UserThread o2) {
		
		if(o1.getResponseRatio() < o2.getResponseRatio()) {
		    return 1;
		}
		
		if(o1.getResponseRatio() == o2.getResponseRatio()) {
		    return 0;
		}
		
		return -1;
	    }
    }

}
