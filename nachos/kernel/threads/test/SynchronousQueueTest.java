package nachos.kernel.threads.test;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.NachosThread;
import nachos.util.SynchronousQueue;

public class SynchronousQueueTest   {
    /** Integer identifier that indicates which thread we are. */

    
    

    public SynchronousQueueTest() {

    }



    /**
     * Entry point for the test.
     */
    public static void start() {
	
	
	SynchronousQueue<String> testQueue = new SynchronousQueue<String>();
	
	Debug.println('t', "Entering SynchronousQueue Test");
	
	NachosThread producer = new NachosThread("producer thread " ,new Runnable() {
	    @Override 
	    public void run() {
		
		String event = "FIRST_SYNCHRONOUS_EVENT";
		
		Nachos.scheduler.sleepThread(300);
		testQueue.offer(event);
		//testQueue.offer(event);
		Debug.printf('t',"---------------------[producer] published event : %s \n", event);
				
		Nachos.scheduler.finishThread();
	    }
	});
	
	
	NachosThread producer2 = new NachosThread("producer2 thread " ,new Runnable() {
	    @Override 
	    public void run() {
		
		String event = "SECOND_SYNCHRONOUS_EVENT2";

		testQueue.put(event);
		Debug.printf('t',"---------------------[producer2] published event : %s \n", event);
	
		Nachos.scheduler.finishThread();
	    }
	});
	
	NachosThread producer3 = new NachosThread("producer3 thread " ,new Runnable() {
	    @Override 
	    public void run() {
		
		String event = "THIRD_SYNCHRONOUS_EVENT3";

		testQueue.put(event);
		Debug.printf('t',"---------------------[producer3] published event : %s \n", event);
	
		Nachos.scheduler.finishThread();
	    }
	});
	
	

	NachosThread consumer = new NachosThread("consumer thread " ,new Runnable() {
	    @Override 
	    public void run() {
		
		//Nachos.scheduler.sleepThread(500);
	        // thread will block here
	        Debug.printf('t',"---------------------[conusmer] consumed event : %s \n", testQueue.poll(200));


		 Nachos.scheduler.finishThread();
	    }
	});
	
	NachosThread consumer2 = new NachosThread("consumer2 thread " ,new Runnable() {
	    @Override 
	    public void run() {
		

	        // thread will block here
	        Debug.printf('t',"---------------------[conusmer2] consumed event : %s \n", testQueue.poll(500));


		 Nachos.scheduler.finishThread();
	    }
	});
	NachosThread consumer3 = new NachosThread("consumer3 thread " ,new Runnable() {
	    @Override 
	    public void run() {
		

	        // thread will block here
		//Nachos.scheduler.sleepThread(200);
	        Debug.printf('t',"---------------------[conusmer3] consumed event : %s \n", testQueue.take());


		 Nachos.scheduler.finishThread();
	    }
	});
	
	Nachos.scheduler.readyToRun(producer);
	//Nachos.scheduler.readyToRun(producer2);
	//Nachos.scheduler.readyToRun(producer3);
	
	
	Nachos.scheduler.readyToRun(consumer);

	//Nachos.scheduler.readyToRun(consumer2);
	//Nachos.scheduler.readyToRun(consumer3);
	
    }
}
