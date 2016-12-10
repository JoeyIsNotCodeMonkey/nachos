package nachos.kernel.userprog;

import java.util.ArrayList;
import java.util.HashMap;

import nachos.Debug;
import nachos.kernel.threads.Semaphore;
import nachos.machine.Machine;
import nachos.machine.NachosThread;

public class PhysicalMemoryManager {

    //available->false; unavailable->true
    private static int physicalPages[];
    private static pageStatus coreMap[];
    private static Semaphore physicalPage_lock;
    private static Semaphore spaceID_lock;
    private static Semaphore joinList_lock;
    
    
    
    private static int spaceID=0;
    private HashMap<Integer, AddrSpace> processTable;
    private HashMap<Integer, Integer> parentTable;
    private static ArrayList<Object[]> joinList;
    private static ArrayList<Integer> FIFO;





    private static PhysicalMemoryManager pmm;
    
    public PhysicalMemoryManager(){
	
	coreMap = new pageStatus[Machine.NumPhysPages];
	physicalPages = new int[Machine.NumPhysPages];
	spaceID_lock = new Semaphore("spaceID_lock", 1);
	joinList_lock  = new Semaphore("joinList_lock", 1);
	physicalPage_lock = new Semaphore("physicalPage_lock", 1);
	processTable = new HashMap<Integer, AddrSpace>();
	parentTable = new HashMap<Integer, Integer>();
	joinList = new ArrayList<Object[]>();
	FIFO = new ArrayList<Integer>();
	
	
	for(int i =0 ; i<Machine.NumPhysPages;i++){
	    coreMap[i] = new pageStatus();
	    FIFO.add(i);
	}
	
    }
    
    
    public ArrayList<Object[]> getJoinList() {
        return joinList;
    }
    
    
    
    
    public void awakeThread(int spaceID){
	
	 joinList_lock.P();
	if(!joinList.isEmpty()){
	    for(int i =0 ; i<joinList.size();i++){
		Object[] o = joinList.get(i);
		
		if(((AddrSpace)o[1]).getSpaceID()==spaceID){		   
		    ((AddrSpace)o[0]).join_lock.V();	
		   
		    joinList.remove(i);
		   
		}
	    
	    
	    }
	    
	}
	joinList_lock.V();
    }
    
    
    
    public static PhysicalMemoryManager getInstance(){
	if(pmm==null){
	    pmm=new PhysicalMemoryManager();	   
	}
	return pmm;
    }
    
    
    public HashMap<Integer, Integer> getParentTable() {
        return parentTable;
    }
    
    public void addJoinList(AddrSpace s1,AddrSpace s2){
	// s1 is joining s2
	joinList_lock.P();
	Object [] temp = new Object[2];
	temp[0] = s1;				
	temp[1] = s2;
	
	joinList.add(temp);
	joinList_lock.V();
    }
    
    
    public int getPhysicalPage(int virtualPage) {
	physicalPage_lock.P();
	
	
	int index = 0;
	while(physicalPages[index] > 0 ) {
	    index++;
	    if(index == 128){
		physicalPage_lock.V();
		return -1;
	    }
		
	}

	
	physicalPages[index] ++;
	
	//FIFO.add(index);
//	coreMap[index].setAddressSpace(((UserThread)NachosThread.currentThread()).space.getSpaceID());
	
	
	//Debug.println('+', "AllocateMemory PhysicAddress:" + index);
	
	
	physicalPage_lock.V();
	return index;
    }
    
    
    public void setPageStatus(int pageNumber,int addressSpace,int vpn,boolean extend){
	pageStatus ps = new pageStatus();
	ps.setAddressSpace(addressSpace);
	ps.setVPN(vpn);
	ps.setExtendRegion(extend);
	coreMap[pageNumber] = ps;
    }
    
    public boolean checkFullMemory(){
	boolean result = true;
	for(pageStatus p : coreMap){
	    if(p.getAddressSpace()==-1)
		return false;
	    
	}
	
	return result;
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
    
    public void registerParent(int spaceID, int parentID) {
	spaceID_lock.P();
	if(spaceID == 1) {
	    parentTable.put(1, 1);
	} else {
	    parentTable.put(spaceID, parentID);
	}
	spaceID_lock.V();
    }
    
    public AddrSpace getSpaceByID(int id){
	return processTable.get(id);
    }
    
    public HashMap<Integer, AddrSpace> getProcessTable() {
        return processTable;
    }
    
    public int pageLeft(){
	int count =0;
	
	for(int i : physicalPages){
	    if(i==0)
		count++;
	}
	return count;
    }


    public boolean isEmpty() {
	if(pageLeft()==128)
	    return true;
	else
	    return false;
    }


    public static pageStatus[] getCoreMap() {
        return coreMap;
    }


    public static ArrayList<Integer> getFIFO() {
        return FIFO;
    }


    public static void setFIFO(ArrayList<Integer> fIFO) {
        FIFO = fIFO;
    }
    

}
