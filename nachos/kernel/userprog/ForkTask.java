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
    AddrSpace space;

    public ForkTask(int func, UserThread parentThread) {
	this.func = func;
	this.parentThread = parentThread;
	space = ((UserThread) NachosThread.currentThread()).space;
    }

    @Override
    public void run() {
	// TODO Auto-generated method stub
	// copy size and numPages

	

	initRegistersFork();		// set the initial register values
	space.restoreState();		// load page table register

	CPU.runUserCode();		// jump to the user progam
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
