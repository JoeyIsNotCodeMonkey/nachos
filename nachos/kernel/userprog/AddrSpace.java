// AddrSpace.java
//	Class to manage address spaces (executing user programs).
//
//	In order to run a user program, you must:
//
//	1. link with the -N -T 0 option 
//	2. run coff2noff to convert the object file to Nachos format
//		(Nachos object code format is essentially just a simpler
//		version of the UNIX executable object code format)
//	3. load the NOFF file into the Nachos file system
//		(if you haven't implemented the file system yet, you
//		don't need to do this last step)
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.CPU;
import nachos.machine.MIPS;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.TranslationEntry;
import nachos.noff.NoffHeader;
import nachos.kernel.filesys.OpenFile;
import nachos.kernel.threads.Semaphore;

/**
 * This class manages "address spaces", which are the contexts in which user
 * programs execute. For now, an address space contains a "segment descriptor",
 * which describes the the virtual-to-physical address mapping that is to be
 * used when the user program is executing. As you implement more of Nachos, it
 * will probably be necessary to add other fields to this class to keep track of
 * things like open files, network connections, etc., in use by a user program.
 *
 * NOTE: Most of what is in currently this class assumes that just one user
 * program at a time will be executing. You will have to rewrite this code so
 * that it is suitable for multiprogramming.
 * 
 * @author Thomas Anderson (UC Berkeley), original C++ version
 * @author Peter Druschel (Rice University), Java translation
 * @author Eugene W. Stark (Stony Brook University)
 */
public class AddrSpace {

    /** Page table that describes a virtual-to-physical address mapping. */
    private TranslationEntry pageTable[];

    /** Default size of the user stack area -- increase this as necessary! */
    private static final int UserStackSize = 1024;    
    
    private PhysicalMemoryManager pmm;
    
    private long size;
   
    private int numPages;
    
    private int spaceID;
    
    Semaphore join_lock;
    
    private Semaphore lock;
    


    /**
     * Create a new address space.
     */
    public AddrSpace() {
	
	pmm = PhysicalMemoryManager.getInstance();
	
	spaceID = pmm.registerSpace(this);
	
	join_lock = new Semaphore("join_lock for process "+spaceID, 0);
	
	lock = new Semaphore("lock"+ spaceID, 1);
	
    }

    
    public long getSize() {
        return size;
    }
    
    public int calculateStackIndex(){
	
	return (int)((size-UserStackSize)/Machine.PageSize);
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public int getNumPages() {
        return numPages;
    }
    
    public TranslationEntry[] getPageTable() {
	
	return pageTable;
    }
    
    public void deAllocateAndZeroOut(AddrSpace addrSpace) {
	
	
	TranslationEntry[] te = addrSpace.getPageTable();
	
	for(int i=0;i<te.length;i++){
	    
	    pmm.decreaseCounter(te[i].physicalPage);
	    Debug.println('+', "DeAllocateMemory PhysicAddress:" + i);
	    
	    
	    if(pmm.getPhysicalPages()[i]==0){
			
		    int start = te[i].physicalPage * 128;
		    int end = start + 128;		    
	    	    for (int z = start; z < end; z++) {
	    	      Machine.mainMemory[z] = (byte) 0;
	    	    }
	    	    
	     Debug.println('+', "ZeroOut PhysicAddress:" + i);	    
	    }
	   
	}
	
	pmm.getProcessTable().remove(addrSpace.getSpaceID());
	
    }

    /**
     * Load the program from a file "executable", and set everything up so that
     * we can start executing user instructions.
     *
     * Assumes that the object code file is in NOFF format.
     *
     * First, set up the translation from program memory to physical memory. For
     * now, this is really simple (1:1), since we are only uniprogramming.
     *
     * @param executable
     *            The file containing the object code to load into memory
     * @return -1 if an error occurs while reading the object file, otherwise 0.
     */
    public int exec(OpenFile executable) {

	
	lock.P();
	
	NoffHeader noffH;

	if ((noffH = NoffHeader.readHeader(executable)) == null)
	    return (-1);

	// how big is address space?
	size = roundToPage(noffH.code.size)
		+ roundToPage(noffH.initData.size + noffH.uninitData.size)
		+ UserStackSize; // we need to increase the size
				 // to leave room for the stack
	numPages = (int) (size / Machine.PageSize);

	Debug.ASSERT((numPages <= Machine.NumPhysPages), // check we're not
							 // trying
		"AddrSpace constructor: Not enough memory!");
	// to run anything too big --
	// at least until we have
	// virtual memory

	Debug.println('+', "Initializing address space, numPages=" + numPages
		+ ", size=" + size);

	// first, set up the translation
	pageTable = new TranslationEntry[numPages];
	for (int i = 0; i < numPages; i++) {
	    pageTable[i] = new TranslationEntry();
	    pageTable[i].virtualPage = i; 
	    pageTable[i].physicalPage = pmm.getPhysicalPage(pageTable[i].virtualPage);	 
	    pageTable[i].valid = true;
	    pageTable[i].use = false;
	    pageTable[i].dirty = false;
	    pageTable[i].readOnly = false; // if code and data segments live on
					   // separate pages, we could set code
					   // pages to be read-only
	}

	// Zero out the entire address space, to zero the uninitialized data
	// segment and the stack segment.
	if(this.getSpaceID()==1){
	    for (int i = 0; i < size; i++)
		    Machine.mainMemory[i] = (byte) 0;
	}
	

	 
	
	// then, copy in the code and data segments into memory
	if (noffH.code.size > 0) {
	    Debug.println('a', "Initializing code segment, at "
		    + noffH.code.virtualAddr + ", size " + noffH.code.size);

	    executable.seek(noffH.code.inFileAddr);	
	    int pageNum  = noffH.code.virtualAddr/Machine.PageSize;
	    int startPoint = pageTable[pageNum].physicalPage* Machine.PageSize + noffH.code.virtualAddr%Machine.PageSize;
	
	    
	    executable.read(Machine.mainMemory, startPoint,
		    noffH.code.size);
	}

	if (noffH.initData.size > 0) {
	    Debug.println('a',
		    "Initializing data segment, at "
			    + noffH.initData.virtualAddr + ", size "
			    + noffH.initData.size);

	    executable.seek(noffH.initData.inFileAddr);	    
	    int pageNum  = noffH.initData.virtualAddr/Machine.PageSize;
	    int startPoint = pageTable[pageNum].physicalPage* Machine.PageSize + noffH.initData.virtualAddr%Machine.PageSize;
	    executable.read(Machine.mainMemory, startPoint,
		    noffH.initData.size);
	}
	
	
	lock.V();
	
	
	return (0);
    }

    
    public void allocateFork(AddrSpace parentSpace, AddrSpace newSpace){
	// how big is address space?
	long size = parentSpace.getSize();
	int numPages = (int) (size / Machine.PageSize);
	int stackIndex = parentSpace.calculateStackIndex();
	
	Debug.ASSERT((numPages <= Machine.NumPhysPages), // check we're not
							 // trying
		"AddrSpace constructor: Not enough memory!");
	// to run anything too big --
	// at least until we have
	// virtual memory

	Debug.println('a', "Initializing address space, numPages=" + numPages
		+ ", size=" + size);


	
	
	TranslationEntry[] pageTable = new TranslationEntry[numPages];
	PhysicalMemoryManager pmm = newSpace.getPmm();
	for (int i = 0; i < numPages; i++) {
	    
	    
	    pageTable[i] = new TranslationEntry();
	    pageTable[i].virtualPage = i; 
	    
	    if(i<stackIndex){
		 pageTable[i].physicalPage = parentSpace.getPageTable()[i].physicalPage;
		 parentSpace.getPmm().increaseCounter(parentSpace.getPageTable()[i].physicalPage);
		 
	    }else{
		 pageTable[i].physicalPage = pmm.getPhysicalPage(pageTable[i].virtualPage);
	    }
	   
	  
	    pageTable[i].valid = true;
	    pageTable[i].use = false;
	    pageTable[i].dirty = false;
	    pageTable[i].readOnly = false; // if code and data segments live on
					   // separate pages, we could set code
					   // pages to be read-only
	}
	
	
	newSpace.setPageTable(pageTable);
    }
    
    
    /**
     * Initialize the user-level register set to values appropriate for starting
     * execution of a user program loaded in this address space.
     *
     * We write these directly into the "machine" registers, so that we can
     * immediately jump to user code.
     */
    public void initRegisters() {
	int i;

	for (i = 0; i < MIPS.NumTotalRegs; i++)
	    CPU.writeRegister(i, 0);

	// Initial program counter -- must be location of "Start"
	CPU.writeRegister(MIPS.PCReg, 0);

	// Need to also tell MIPS where next instruction is, because
	// of branch delay possibility
	CPU.writeRegister(MIPS.NextPCReg, 4);

	// Set the stack register to the end of the segment.
	// NOTE: Nachos traditionally subtracted 16 bytes here,
	// but that turns out to be to accomodate compiler convention that
	// assumes space in the current frame to save four argument registers.
	// That code rightly belongs in start.s and has been moved there.
	int sp = pageTable.length * Machine.PageSize;
	CPU.writeRegister(MIPS.StackReg, sp);
	Debug.println('a', "Initializing stack register to " + sp);
    }

    /**
     * On a context switch, save any machine state, specific to this address
     * space, that needs saving.
     *
     * For now, nothing!
     */
    public void saveState() {
    }
    
    
    
    
    /**
     * On a context switch, restore any machine state specific to this address
     * space.
     *
     * For now, just tell the machine where to find the page table.
     */
    public void restoreState() {
	CPU.setPageTable(pageTable);
    }

    /**
     * Utility method for rounding up to a multiple of CPU.PageSize;
     */
    private long roundToPage(long size) {
	return (Machine.PageSize
		* ((size + (Machine.PageSize - 1)) / Machine.PageSize));
    }

    public int getSpaceID() {
	// TODO Auto-generated method stub
	return spaceID;
    }

    public void setPageTable(TranslationEntry[] pageTable) {
        this.pageTable = pageTable;
    }

    public PhysicalMemoryManager getPmm() {
	// TODO Auto-generated method stub
	return pmm;
    }
    
    public int translateAddr(int va, AddrSpace currentAddrSpace) {
	int vpn = ((va >> 7) & 0x1ffffff);
	int off = (va & 0x7f);
	TranslationEntry[] pagetable = currentAddrSpace.getPageTable();
	int ppn = pagetable[vpn].physicalPage;
	int pa = (((ppn << 7) | off));
	return pa;
    }
    
}
