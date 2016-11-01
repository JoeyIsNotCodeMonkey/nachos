package nachos.kernel.userprog;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.kernel.filesys.OpenFile;
import nachos.machine.CPU;
import nachos.machine.NachosThread;

public class Task implements Runnable{
    private String execName;
    private UserThread parentThread;
    
    public Task(String name, UserThread parentThread) {
	execName = name;
	this.parentThread = parentThread;
    }

    @Override
    public void run() {
	// TODO Auto-generated method stub
	OpenFile executable;
	AddrSpace space = ((UserThread)NachosThread.currentThread()).space;

	if((executable = Nachos.fileSystem.open(execName)) == null) {
	    Debug.println('+', "Unable to open executable file: " + execName);
	    PhysicalMemoryManager.getInstance().getSpaceByID(space.getSpaceID()).join_lock.V();
	    Nachos.scheduler.finishThread();
	    return;
	}

	
	if(space.exec(executable) == -1) {
	    Debug.println('+', "Unable to read executable file: " + execName);
	    PhysicalMemoryManager.getInstance().getSpaceByID(space.getSpaceID()).join_lock.V();
	    Nachos.scheduler.finishThread();
	    return;
	}

	space.initRegisters();		// set the initial register values
	space.restoreState();		// load page table register

	CPU.runUserCode();		// jump to the user progam
	Debug.ASSERT(false);		// machine->Run never returns;
    }

    public UserThread getParentThread() {
        return parentThread;
    }
}
