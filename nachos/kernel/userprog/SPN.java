package nachos.kernel.userprog;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import nachos.kernel.threads.Callout.CustomComparator;
import nachos.util.FIFOQueue;
import nachos.util.Queue;
import nachos.util.ReadyList;

public class SPN<T> extends java.util.LinkedList<T> implements ReadyList<T>{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private LinkedList<UserThread> queue;
    private static SPN<?> spn;
    
    public LinkedList<UserThread> getQueue() {
        return queue;
    }



    public void setQueue(LinkedList<UserThread> queue) {
        this.queue = queue;
    }



    public SPN() {
	queue = new LinkedList<UserThread>();
    }
    
    public static SPN<?> getInstance() {
	if(spn == null) spn = new SPN();
	return spn;
    }
   
    
    @Override
    public T poll(){
	

	if(queue.isEmpty())
	    return null;
	
	return (T)queue.removeFirst();
	
	
    }
    @Override
    public boolean offer(T e) {
	UserThread thread = (UserThread) e;
	
	if(queue.isEmpty()) {
	    queue.add(thread);
	} else {
	    for( int i =0; i<queue.size(); i++) {
		if(queue.get(i).getBurstLen() > thread.getBurstLen() ) {
		    queue.add(i, thread);
		    break;
		}
		if(i==queue.size()-1){
			queue.addLast(thread);
			break;
		  }
	
	    }
	    
	    
	}
	
	//queue.add((UserThread) e);
	return true;
    }
    
    public void update() {
	Collections.sort(queue, new CustomComparator());
    }
    
    public static class CustomComparator implements Comparator<UserThread> {	  
	    @Override
	    public int compare(UserThread o1, UserThread o2) {
		
		if(o1.getBurstLen() > o2.getBurstLen()) {
		    return 1;
		}
		
		if(o1.getBurstLen() == o2.getBurstLen()) {
		    return 0;
		}
		
		return -1;
	    }
}
    
}
