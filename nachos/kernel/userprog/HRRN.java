package nachos.kernel.userprog;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Timer;
import nachos.util.FIFOQueue;
import nachos.util.Queue;
import nachos.util.ReadyList;

public class HRRN<T> extends java.util.LinkedList<T> implements Queue<T> {



    //private static HRRN hrrn;

//    public static HRRN getInstance() {
//	if (hrrn == null) {
//	    hrrn = new HRRN();
//	}
//	return hrrn;
//    }

    public static class CustomComparator implements Comparator<NachosThread> {

	@Override
	public int compare(NachosThread arg0, NachosThread arg1) {

	    UserThread o1 = (UserThread) arg0;
	    UserThread o2 = (UserThread) arg1;

	    if (o1.getResponseRatio() < o2.getResponseRatio()) {
		return 1;
	    }

	    if (o1.getResponseRatio() == o2.getResponseRatio()) {
		return 0;
	    }

	    return -1;
	}

    }

    public void iterate() {
	UserThread currentThread = (UserThread) this.peek();

	if (!this.isEmpty()&&currentThread.getBurstLen() == 0) {
	    Nachos.scheduler.yieldThread();
	}

    }



    @SuppressWarnings("unchecked")
    @Override

    public T poll() {

	if (initPredictCPU()) {

	    for (T t : this) {
		
		if(t instanceof UserThread){
		    
		    
		UserThread indexThread = (UserThread) t;
		UserThread currentThread = (UserThread) NachosThread
			.currentThread();
		indexThread.setWaitingTime(currentThread.getBurstLen()
			+ indexThread.getWaitingTime());
		indexThread.updateResponseRatio();
		}

	    }

	    Collections.sort((LinkedList<NachosThread>) this, new CustomComparator());
	    
	    for (T t : this) {
		Debug.println('+', "Ratio: "+((UserThread)t).getResponseRatio());
	    }
	}

	return this.pollFirst();

    }




    // make sure every process has called predictCPU, then we can sort the list
    // by ratio
    private boolean initPredictCPU() {

	for (T t : this) {
	    if (t instanceof UserThread) {
		UserThread indexThread = (UserThread) t;
		if (indexThread.getBurstLen() == 0) {
		    return false;
		}
	    }
	}

	return true;
    }

}
