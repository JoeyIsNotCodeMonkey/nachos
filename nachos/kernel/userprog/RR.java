package nachos.kernel.userprog;

import java.util.LinkedList;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.CPU;
import nachos.machine.InterruptHandler;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Timer;
import nachos.util.FIFOQueue;
import nachos.util.Queue;
import nachos.util.ReadyList;

public class RR<T> extends java.util.LinkedList<T> implements ReadyList<T>{
    private static int quantum;
    private Queue<T> queue;


    private Timer timer;
    private TimerInterruptHandler interruptHandler;
    private static RR rr;
    private static UserThread lastThread;
    
    private static int ticks =0;
    
    
    public RR() {
	
	queue = new FIFOQueue<T>();
	
	
	timer = Machine.getTimer(0);
	interruptHandler = new TimerInterruptHandler(timer);
	timer.setHandler(interruptHandler);
	//timer.start();
    }
    
    public static RR getInstance(){
	if(rr==null){
	    rr=new RR();	   
	}
	return rr;
    }
    
    public Queue<T> getQueue() {
        return queue;
    }


    
    
    private static class TimerInterruptHandler implements InterruptHandler {

	/** The Timer device this is a handler for. */
	private final Timer timer;

	/**
	 * Initialize an interrupt handler for a specified Timer device.
	 * 
	 * @param timer
	 *            The device this handler is going to handle.
	 */
	public TimerInterruptHandler(Timer timer) {
	    this.timer = timer;
	}

	public void handleInterrupt() {
	    Debug.println('i', "Timer interrupt: " + timer.name);
	    UserThread currentThread = (UserThread)NachosThread.currentThread();
	    
	   currentThread.setQuantum(currentThread.getQuantum()-100);
	    
	    if(currentThread.getQuantum()==0 ) {
		//throw it to the end of queue
//		thisThread = (UserThread) RR.getInstance().getQueue().poll();
//		RR.getInstance().offer(thisThread);	
		Debug.println('+', "shift: ");
		
		
		
		
		currentThread.setQuantum(1000);
		
		
		Nachos.scheduler.yieldThread();
	    }
	    
	    ticks+=100;
	    
	  //  lastThread = thisThread;
	    
	    
	}

    }
    
}

