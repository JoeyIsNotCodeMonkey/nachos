package nachos.kernel.threads;

import java.util.ArrayList;
import java.util.PriorityQueue;

import nachos.Debug;
import nachos.machine.CPU;
import nachos.machine.InterruptHandler;
import nachos.machine.Timer;

/**
 * This class implements a "callout" facility, which provides threads
 * the ability of scheduling a call-out to specified code at a specified
 * time in the future.
 * 
 * @author Eugene W. Stark
 * @version 20140301
 */

public class Callout {
    
    /** The queue of scheduled callouts. */
    private final PriorityQueue<CalloutEntry> callouts;
    
    /**
     * The number of ticks that have passed since initialization,
     * assuming that the timer interrupts at a regular interval.
     * Actually, it cannot be guaranteed that an interrupt will
     * occur at an exactly specified time, so the value maintained
     * here will tend to lag "real" time.
     */
    private long timeOfDay;
    
    /**
     * Spinlock needed to synchronize the callout queue in a
     * multiprocessor setting.
     */
    private final SpinLock spinLock;
    
    /** Timer being used as a source of periodic interrupts. */
    private final Timer timer;

    /**
     * Initialize a callout facility.
     */
    public Callout(Timer timer) {
	this.timer = timer;
	this.spinLock = new SpinLock("Callout spinlock");
	this.callouts = new PriorityQueue<CalloutEntry>();
	timeOfDay = 0;
	InterruptHandler handler =
		new InterruptHandler() {
	    		public void handleInterrupt() {
	    		    onTick();
	    		}
		};
	timer.setHandler(handler);
    }
    
    /**
     * Schedule a callout to occur at a specified number of 
     * ticks in the future. 
     *
     * @param runnable A Runnable to be invoked when the specified
     * time arrives. 
     * @param ticksFromNow The number of ticks in the future at
     * which the callout is to occur.
     */
    public void schedule(Runnable runnable, int ticksFromNow) {
	int oldLevel = CPU.setLevel(CPU.IntOff);
	spinLock.acquire();
	
	if(callouts.isEmpty()) {
	    timeOfDay = 0;
	    timer.start();
	}
		
	CalloutEntry entry = new CalloutEntry(runnable, ticksFromNow);
	callouts.offer(entry);
	
	spinLock.release();
	CPU.setLevel(oldLevel);
    }
    
    /**
     * Method called on a timer interrupt.  Finds entries that are
     * currently due and invokes them.
     */
    private void onTick() {
	ArrayList<CalloutEntry> toCall = new ArrayList<CalloutEntry>();
	spinLock.acquire();
	
	timeOfDay += Timer.DefaultInterval;
	CalloutEntry entry;
	while((entry = callouts.peek()) != null) {
	    if(entry.atTime > timeOfDay)
		break;
	    callouts.remove();
	    toCall.add(entry);
	}
	if(callouts.isEmpty())
	    timer.stop();
	
	spinLock.release();

	// Invoke the callouts outside of the critical section, so that
	// it is possible for a callout to schedule a new callout without
	// deadlocking.  Interrupts are still disabled, though.
	for(CalloutEntry e : toCall)
	    e.runnable.run();
    }
    
    /**
     * Class used to bundle up a Runnable with the time at which it
     * is supposed to be called.  Implements Comparable so that the
     * objects can be placed in a priority queue.
     */
    private class CalloutEntry implements Comparable<CalloutEntry> {
	
	/** The Runnable to be called when it is time. */
	public final Runnable runnable;
	
	/** The time at which the Runnable is to be called. */
	public final long atTime;
	
	/**
	 * Initialize a CalloutEntry, given a Runnable and the number of
	 * ticks from the current time that the Runnable should be called.
	 */
	public CalloutEntry(Runnable runnable, int ticks) {
	    this.runnable = runnable;
	    this.atTime = timeOfDay + ticks;
	}

	@Override
	public int compareTo(CalloutEntry o) {
	    return Long.compare(atTime, o.atTime);
	}
    }

}
