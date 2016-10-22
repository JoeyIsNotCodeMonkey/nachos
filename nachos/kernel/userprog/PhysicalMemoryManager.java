package nachos.kernel.userprog;

import java.util.ArrayList;
import java.util.HashMap;

import nachos.Debug;
import nachos.kernel.threads.Semaphore;
import nachos.machine.Machine;

public class PhysicalMemoryManager {

    //available->false; unavailable->true
    private static int physicalPages[];
    private static Semaphore physicalPage_lock;
    private static Semaphore spaceID_lock;
    private int spaceID=0;
    private  HashMap<Integer, AddrSpace> processTable;


    private static PhysicalMemoryManager pmm;
    
    public PhysicalMemoryManager(){
	physicalPages = new int[Machine.NumPhysPages];
	spaceID_lock = new Semaphore("spaceID_lock", 1);
	physicalPage_lock = new Semaphore("physicalPage_lock", 1);
	processTable = new HashMap<Integer, AddrSpace>();
    }
    
    public static PhysicalMemoryManager getInstance(){
	if(pmm==null){
	    pmm=new PhysicalMemoryManager();	   
	}
	return pmm;
    }
    
    
    public int getPhysicalPage(int virtualPage) {
	
	int index = 0;
	while(physicalPages[index]>0) {
	    index++;
	}
	
	physicalPage_lock.P();
	physicalPages[index] ++;
	physicalPage_lock.V();
	Debug.println('+', "AllocateMemory PhysicAddress:" + index);
	return index;
    }
    
    public void decreaseCounter(int index) {
	physicalPage_lock.P();
	physicalPages[index] --;
	physicalPage_lock.V();	
    }
    
    public void increaseCounter(int index) {
	physicalPage_lock.P();
	physicalPages[index] ++;
	physicalPage_lock.V();	
    }
    
    public int[] getPhysicalPages(){
	return physicalPages;
    }

    public int registerSpace(AddrSpace addr) {
	spaceID_lock.P();
	spaceID++;
	processTable.put(spaceID, addr);
	spaceID_lock.V();
	return spaceID;
    }
    
    public AddrSpace getSpaceByID(int id){
	return processTable.get(id);
    }
    
    public HashMap<Integer, AddrSpace> getProcessTable() {
        return processTable;
    }
    

}
