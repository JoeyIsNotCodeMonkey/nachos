package nachos.kernel.userprog;

import java.util.ArrayList;

import nachos.kernel.threads.Semaphore;
import nachos.machine.Machine;

public class PhysicalMemoryManager {

    //available->false; unavailable->true
    static boolean physicalPages[] = new boolean[Machine.NumPhysPages];
    static Semaphore physicalPage_lock = new Semaphore("physicalPage_lock", 1);
    
    public PhysicalMemoryManager(){
	
	
    }
    
    
    public int getPhysicalPage(int virtualPage) {
	
	int index = 0;
	while(physicalPages[index]) {
	    index++;
	}
	
	physicalPage_lock.P();
	physicalPages[index] = true;
	physicalPage_lock.V();
	
	return index;
    }
    
    public void freePage(int index) {
	physicalPage_lock.P();
	physicalPages[index] = false;
	physicalPage_lock.V();	
    }

}
