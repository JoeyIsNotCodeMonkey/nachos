package nachos.kernel.userprog;

import java.util.Collections;
import java.util.Comparator;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.kernel.userprog.SPN.CustomComparator;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Timer;
import nachos.util.FIFOQueue;
import nachos.util.Queue;
import nachos.util.ReadyList;

public class HRRN<T>   extends java.util.LinkedList<T>  implements Queue<T>{
    
    private  Queue<T> queue;
    private static HRRN<NachosThread> hrrn;
    
    
    
    
    public HRRN(){
	queue =  new FIFOQueue<T>(); 
    }
    public static HRRN<NachosThread> getInstance(){
	if(hrrn==null){
	    hrrn=new HRRN<NachosThread>();	   
	}
	return hrrn;
    }
    
    
    
    public static class CustomComparator implements Comparator<NachosThread> {	  


	    @Override
	    public int compare(NachosThread arg0, NachosThread arg1) {
		
		UserThread o1 = (UserThread)arg0;
		UserThread o2 = (UserThread)arg1;
		
		if(o1.getResponseRatio() < o2.getResponseRatio()) {
		    return 1;
		}
		
		if(o1.getResponseRatio() == o2.getResponseRatio()) {
		    return 0;
		}
		
		return -1;
	    }
    }

    public void update() {
	UserThread currentThread = (UserThread) queue.peek();
	
	
	if(currentThread.getBurstLen() == 0) {
	    Nachos.scheduler.yieldThread();
	}
	
    }

   
    @Override
    public T poll(){
	
	
	
	
	Debug.println('+', "sdfklas;kdljflaksdjf;");
		
	
//	for(int i=0; i<queue.; i++) {
//	    
//		UserThread t = (UserThread)queue.poll();
//		t.updateResponseRatio();		
//		queue.offer((T)t);
//	    
//	}
	
//	Collections.sort((FIFOQueue<NachosThread>)queue, new CustomComparator());

	
	
	NachosThread t = (NachosThread)queue.peek();
	
	return queue.poll();
	
	
    }
    
    
    
    
    
    
    
    
    public Queue<T> getQueue() {
        return queue;
    }
    public void setQueue(Queue<T> queue) {
        this.queue = queue;
    }



}
