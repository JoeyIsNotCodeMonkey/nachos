package nachos.kernel.userprog;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.NachosThread;
import nachos.util.FIFOQueue;
import nachos.util.Queue;
import nachos.util.ReadyList;

public class SPN<T> extends java.util.LinkedList<T> implements ReadyList<T> {
    /**
     * 
     */

    private static SPN spn;

    LinkedList<T> list = new LinkedList<T>();

    @Override
    public T poll() {

	// this.poll
	//

	Collections.sort((LinkedList<NachosThread>) this,
		new CustomComparator());
	return this.pollFirst();

    }
    // @Override
    // public boolean offer(T e) {
    // //UserThread thread = (UserThread) e;
    //
    // if(queue.isEmpty()) {
    // queue.offer(e)
    // } else {
    // for( int i =0; i<queue.size(); i++) {
    // if(((UserThread) queue.get(i)).getBurstLen() >
    // ((UserThread)e).getBurstLen() ) {
    // queue.add(i, e);
    // break;
    // }
    // if(i==queue.size()-1){
    // queue.offer(e);
    // break;
    // }
    //
    // }
    //
    //
    // }
    //
    // //queue.add((UserThread) e);
    // return true;
    // }

    public void update() {

	Collections.sort((LinkedList<NachosThread>) this,
		new CustomComparator());
	UserThread t = (UserThread) NachosThread.currentThread();

	if (!this.isEmpty()
		&& t.getBurstLen() > ((UserThread) this.peek()).getBurstLen()) {
	    // for(T ut : this){
	    // Debug.println('+', "List: "+((UserThread)ut).getBurstLen());
	    // }
	    Nachos.scheduler.yieldThread();
	}

    }

    public static class CustomComparator implements Comparator<NachosThread> {
	@Override
	public int compare(NachosThread arg0, NachosThread arg1) {
	    if (arg0 instanceof UserThread && arg1 instanceof UserThread) {
		UserThread o1 = (UserThread) arg0;
		UserThread o2 = (UserThread) arg1;
		
		if (o1.getBurstLen() > o2.getBurstLen()) {
		    return 1;
		}

		if (o1.getBurstLen() == o2.getBurstLen()) {
		    return 0;
		}

		
	    }
	    
	    return -1;

	}
    }

    public LinkedList<T> getList() {
	return list;
    }

}
