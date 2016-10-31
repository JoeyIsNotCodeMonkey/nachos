package nachos.kernel.userprog;

import java.util.LinkedList;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.CPU;
import nachos.machine.InterruptHandler;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Timer;
import nachos.util.ReadyList;

public class RR<T> extends java.util.LinkedList<T> implements ReadyList<T>{
    private static int quantum;
    private LinkedList<T> queue;
    private Timer timer;
    private TimerInterruptHandler interruptHandler;
    private static RR rr;
    private static UserThread lastThread;
    
    
    
    public RR() {
	quantum = 1000;
	queue = new LinkedList<T>();
	lastThread = (UserThread) NachosThread.currentThread();
	
	timer = Machine.getTimer(0);
	interruptHandler = new TimerInterruptHandler(timer);
	timer.setHandler(interruptHandler);
	timer.start();
    }
    
    public static RR getInstance(){
	if(rr==null){
	    rr=new RR();	   
	}
	return rr;
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
	    UserThread thisThread = (UserThread) RR.getInstance().peek();	    	    
	    
	    quantum -= 100;
	    
	    if(quantum == 0 && thisThread == lastThread) {
		//throw it to the end of queue
		thisThread = (UserThread) RR.getInstance().pop();
		RR.getInstance().push(thisThread);	
		Debug.println('+', "shift");
		
	    }
	    
	    else if(quantum > 0 && thisThread != lastThread) {
		quantum = 1000;
	    }
	    
	    lastThread = thisThread;
	    
	    
	}

    }
    
}

