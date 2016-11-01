package nachos.kernel.userprog;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import nachos.kernel.threads.Callout.CustomComparator;
import nachos.util.FIFOQueue;
import nachos.util.Queue;
import nachos.util.ReadyList;

public class SPN<T> extends java.util.LinkedList<T> implements ReadyList<T>{
    private LinkedList<UserThread> queue;
    
    public SPN() {
	queue = new LinkedList<UserThread>();
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
    
    
}
