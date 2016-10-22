package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.CPU;
import nachos.machine.MIPS;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.TranslationEntry;

public class ForkTask implements Runnable {
    private int func;

    private UserThread parentThread;

    public ForkTask(int func, UserThread parentThread) {
	this.func = func;
	this.parentThread = parentThread;
    }

    @Override
    public void run() {
	// TODO Auto-generated method stub
	// copy size and numPages

	AddrSpace space = ((UserThread) NachosThread.currentThread()).space;
	// how big is address space?
	long size = parentThread.space.getSize();
	int numPages = (int) (size / Machine.PageSize);

	Debug.ASSERT((numPages <= Machine.NumPhysPages), // check we're not
							 // trying
		"AddrSpace constructor: Not enough memory!");
	// to run anything too big --
	// at least until we have
	// virtual memory

	Debug.println('a', "Initializing address space, numPages=" + numPages
		+ ", size=" + size);

	// first, set up the translation
	TranslationEntry[] pageTable = new TranslationEntry[numPages];
	PhysicalMemoryManager pmm = space.getPmm();
	for (int i = 0; i < numPages; i++) {
	    pageTable[i] = new TranslationEntry();
	    pageTable[i].virtualPage = i; // for now, virtual page# = phys page#
	    pageTable[i].physicalPage = pmm
		    .getPhysicalPage(pageTable[i].virtualPage);
	    // pageTable[i].physicalPage = i;
	    pageTable[i].valid = true;
	    pageTable[i].use = false;
	    pageTable[i].dirty = false;
	    pageTable[i].readOnly = false; // if code and data segments live on
					   // separate pages, we could set code
					   // pages to be read-only
	}
	
	space.setPageTable(pageTable);
	
	initRegistersFork();		// set the initial register values
	space.restoreState();		// load page table register

	CPU.runUserCode();			// jump to the user progam
	Debug.ASSERT(false);		// machine->Run never returns;

    }
    
    private void initRegistersFork() {
	 
	 
	 for (int i = 0; i < MIPS.NumTotalRegs; i++) 
	     CPU.writeRegister(i, 0);
	     //
	     // // Initial program counter -- must be location of "Start"
	     CPU.writeRegister(MIPS.PCReg, func);
	     //
	     // // Need to also tell MIPS where next instruction is, because
	     // // of branch delay possibility
	     CPU.writeRegister(MIPS.NextPCReg, func + 4);
	     //
	     AddrSpace space = ((UserThread) NachosThread.currentThread()).space;
	     int sp = space.getPageTable().length * Machine.PageSize;
	            
	            
	     CPU.writeRegister(MIPS.StackReg, sp);
	     // Debug.println('a', "Initializing stack register to " + sp);
	 
	    }
}
