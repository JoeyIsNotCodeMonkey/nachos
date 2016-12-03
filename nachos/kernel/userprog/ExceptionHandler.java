// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.CPU;
import nachos.machine.MIPS;
import nachos.machine.Machine;
import nachos.machine.MachineException;
import nachos.machine.NachosThread;
import nachos.machine.TranslationEntry;
import nachos.kernel.Nachos;
import nachos.kernel.userprog.Syscall;

/**
 * An ExceptionHandler object provides an entry point to the operating system
 * kernel, which can be called by the machine when an exception occurs during
 * execution in user mode. Examples of such exceptions are system call
 * exceptions, in which the user program requests service from the OS, and page
 * fault exceptions, which occur when the user program attempts to access a
 * portion of its address space that currently has no valid virtual-to-physical
 * address mapping defined. The operating system must register an exception
 * handler with the machine before attempting to execute programs in user mode.
 */
public class ExceptionHandler implements nachos.machine.ExceptionHandler {

    /**
     * Entry point into the Nachos kernel. Called when a user program is
     * executing, and either does a syscall, or generates an addressing or
     * arithmetic exception.
     *
     * For system calls, the following is the calling convention:
     *
     * system call code -- r2, arg1 -- r4, arg2 -- r5, arg3 -- r6, arg4 -- r7.
     *
     * The result of the system call, if any, must be put back into r2.
     *
     * And don't forget to increment the pc before returning. (Or else you'll
     * loop making the same system call forever!)
     *
     * @param which
     *            The kind of exception. The list of possible exceptions is in
     *            CPU.java.
     *
     * @author Thomas Anderson (UC Berkeley), original C++ version
     * @author Peter Druschel (Rice University), Java translation
     * @author Eugene W. Stark (Stony Brook University)
     */
    public void handleException(int which) {
	int type = CPU.readRegister(2);
	
	
	if(which == MachineException.AddressErrorException){
	    
	    
	    
	    int VA = CPU.readRegister(nachos.machine.MIPS.BadVAddrReg);
	    int  VPN = ((VA>>7) & 0x1ffffff);
	    
	    Debug.println('+',"____________________Address Error " + VPN);
	    
	   UserThread errorThread = (UserThread)NachosThread.currentThread();
	   
	   TranslationEntry oldTable[] = errorThread.space.getPageTable();
	    
	   int pageNeeded = VPN+1;
	   //Create new larger page table
	 
	   
	   
	    TranslationEntry pageTable[] = new TranslationEntry[pageNeeded];
		for (int i = 0; i < pageNeeded; i++) {
		    
		    if(i<oldTable.length){
			pageTable[i] = oldTable[i];	
	
		    }else{
			  pageTable[i] = new TranslationEntry();
			    pageTable[i].virtualPage = i;

			    pageTable[i].physicalPage = PhysicalMemoryManager.getInstance()
				    .getPhysicalPage(pageTable[i].virtualPage);

			    pageTable[i].valid = false;
			    pageTable[i].use = false;
			    pageTable[i].dirty = false;
			    pageTable[i].readOnly = false; // if code and data segments live on
							   // separate pages, we could set code
							   // pages to be read-only
		    }
		    
		  
		}

	    //errorThread.space.deAllocateOldPageTable();
	    errorThread.space.setPageTable(pageTable);
	    
	    
	//    errorThread.space.initRegisters();		// set the initial register values
	    errorThread.space.restoreState();		// load page table register
	
	
	    CPU.runUserCode();		// jump to the user progam
	   
	    
	  //  Nachos.scheduler.sleepThread(100000);
	}
	
	
	
	if(which == MachineException.PageFaultException){
	    Debug.println('+',"____________________Page Fault Error ");
	    
	    UserThread errorThread = (UserThread)NachosThread.currentThread();
		   
	    TranslationEntry pageTable[] = errorThread.space.getPageTable();
	    int index=0;
	    
	    while(index<pageTable.length){
		if(pageTable[index].valid==false){
		    
		    int start = pageTable[index].physicalPage * 128;
			int end = start + 128;
			for (int z = start; z < end; z++) {
			    Machine.mainMemory[z] = (byte) 0;
			}
		    
		    
		    pageTable[index].valid = true;
		    break;
		}
		
		index++;
	    }
	    
	    
//	    errorThread.space.initRegisters();		// set the initial register values
//	    errorThread.space.restoreState();		// load page table register
//	    Nachos.scheduler.sleepThread(100000);
	
	    CPU.runUserCode();		// jump to the user progam
	   
	    
	    
	    
	   
	    
	}
	
	if (which == MachineException.SyscallException) {

	    switch (type) {
	    case Syscall.SC_Halt:
		Syscall.halt();
		break;
	    case Syscall.SC_Exit:
		Syscall.exit(CPU.readRegister(4));
		break;
	    
	    case Syscall.SC_Yield:
		Syscall.yield();
		break;
		

	    case Syscall.SC_Exec:

		StringBuffer stringBuffer = new StringBuffer();

		AddrSpace currentAddrSpace = ((UserThread) NachosThread
			.currentThread()).space;
		int va = CPU.readRegister(4);		
		int index = currentAddrSpace.translateAddr(va, currentAddrSpace);

		if (index > Machine.mainMemory.length) {
		    CPU.writeRegister(2, -1);
		    return;
		}

		while (true) {

		    byte temp = Machine.mainMemory[index];
		    if (temp == 0) {
			break;
		    }
		    stringBuffer.append((char) temp);
		    index++;

		}

		int id = Syscall.exec(stringBuffer.toString());
		CPU.writeRegister(2, id);

		break;
	
	    case Syscall.SC_Fork:
		int func = CPU.readRegister(4);
		Syscall.fork(func);
		break;
		
	    case Syscall.SC_Join:
		int processID = CPU.readRegister(4);
		int status = Syscall.join(processID);
		CPU.writeRegister(2, status);
		break;	
		
	    case Syscall.SC_Read:
		int readPtr = CPU.readRegister(4);
		int readLen = CPU.readRegister(5);
		byte readBuf[] = new byte[readLen];
		
		//translate
		AddrSpace currentAddrSpaceRead = ((UserThread) NachosThread
			.currentThread()).space;
		int pa = currentAddrSpaceRead.translateAddr(readPtr, currentAddrSpaceRead);
		
		int size = Syscall.read(readBuf, readLen, CPU.readRegister(6));
		
		System.arraycopy(readBuf, 0, Machine.mainMemory, pa, size);
		
		break;
		
	    case Syscall.SC_Write:
		int ptr = CPU.readRegister(4);
		int len = CPU.readRegister(5);
		
		
		
		AddrSpace as = ((UserThread) NachosThread.currentThread()).space;
		ptr = as.translateAddr(ptr, as);
		byte buf[] = new byte[len];
		
		System.arraycopy(Machine.mainMemory, ptr, buf, 0, len);
		Syscall.write(buf, len, CPU.readRegister(6));
		break;
		
	    case Syscall.SC_PredictCPU:
		int burstLen = CPU.readRegister(4);
		Syscall.PredictCPU(burstLen);
		break;
	    }

	    // Update the program counter to point to the next instruction
	    // after the SYSCALL instruction.
	    CPU.writeRegister(MIPS.PrevPCReg, CPU.readRegister(MIPS.PCReg));
	    CPU.writeRegister(MIPS.PCReg, CPU.readRegister(MIPS.NextPCReg));
	    CPU.writeRegister(MIPS.NextPCReg,
		    CPU.readRegister(MIPS.NextPCReg) + 4);
	    return;
	}

	System.out.println(
		"Unexpected user mode exception " + which + ", " + type);
	//Debug.ASSERT(false);

    }
}
