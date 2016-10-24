package nachos.kernel.devices;


import nachos.kernel.Nachos;
import nachos.kernel.threads.Semaphore;
import nachos.machine.Machine;


public class ConsoleManager {

    private static ConsoleManager cm;
    
    private ConsoleDriver[] consolePool;
    
    private Semaphore poolLock = new Semaphore("poolLock", 1);
    
    public ConsoleManager(){
	consolePool = new ConsoleDriver[Nachos.options.NUM_CONSOLES];
    }
    
    
    public ConsoleManager getInstance(){
	if(cm==null){
	    cm=new ConsoleManager();	   
	}
	return cm;
	
    }
    
    
    
    public ConsoleDriver getConsole(){
	poolLock.P();
	
	for(int i =1; i<consolePool.length;i++){
	    
	    if(consolePool[i]==null){
		
		consolePool[i] = new ConsoleDriver(Machine.getConsole(i),i);
		
		return consolePool[i];
	    }
	    
	}
	poolLock.V();
	
	return null;
	
	
	
    }
    public void freeConsole(ConsoleDriver driver){
	poolLock.P();
	
	consolePool[driver.getDriverID()] = null;
	
	poolLock.V();
	
    }
    
    
    
    
}
