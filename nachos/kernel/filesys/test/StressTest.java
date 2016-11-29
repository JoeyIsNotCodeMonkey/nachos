package nachos.kernel.filesys.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.kernel.filesys.OpenFile;
import nachos.kernel.threads.Semaphore;
import nachos.machine.NachosThread;
import nachos.machine.Simulation;

public class StressTest implements Runnable {
    private static final int numOfThread = 10;

    private static int count = numOfThread;

    /** Transfer data in small chunks, just to be difficult. */
    private static final int TransferSize = 10;
    
    
    private Semaphore sem = new Semaphore("lcokdsdf",1);
    
    private static int[] nums = new int[10];
    /**
     * Print the contents of the Nachos file "name".
     *
     * @param name
     *            The name of the file to print.
     */
    private void print(String name) {
	OpenFile openFile;
	int i, amountRead;
	byte buffer[];

	if ((openFile = Nachos.fileSystem.open(name)) == null) {
	    Debug.printf('+', "Print: unable to open file %s\n", name);
	    return;
	}

	buffer = new byte[TransferSize];
	while ((amountRead = openFile.read(buffer, 0, TransferSize)) > 0)
	    for (i = 0; i < amountRead; i++)
		Debug.printf('+', "%c", new Byte(buffer[i]));

	return;
    }

    /** Name of the file to create for the performance test. */
    private static String FileName;

    // private static final String FileName2 = "TestFile2";
    /** Test data to be written to the file in the performance test. */
    private static final String ContentString = "1234567890";

    private String[] names = { "os1", "os2", "os3", "os4", "os5", "os6", "os7",
	    "os8", "os9", "os10" };

    /** Length of the test data. */
    private static final int ContentSize = ContentString.length();

    /** Bytes in the test data. */
    private static final byte Contents[] = ContentString.getBytes();

    /** Total size of the test file. */
    private static final int FileSize = ContentSize * 300;

    /**
     * Stress the Nachos file system by creating a large file, writing it out a
     * bit at a time, reading it back a bit at a time, and then deleting the
     * file.
     *
     * Implemented as three separate routines: FileWrite -- write the file;
     * FileRead -- read the file; PerformanceTest -- overall control, and print
     * out performance #'s.
     */
    private void performanceTest() {

	//Debug.print('+', "Starting file system performance test:\n");
	// Simulation.stats.print();
	Random random = new Random();
	int rand = random.nextInt(9);
	// Debug.println('+', "Rand:" + rand);
	nums[rand]++;

	String fileName = names[rand];
	fileWrite(fileName);

	fileRead(fileName);

	/**
	 * Here is the little trick to ensure that we only remove the file that was not created repeatedly. Since we have trouble handling
	 * the case when a file is trying to read or write to the file that has already been removed by other thread.
	 */
	if (nums[rand] == 1) {
	    if (!Nachos.fileSystem.remove(fileName) && rand <5) {
		Debug.printf('+', "Perf test: unable to remove %s\n", fileName);
		// return;
	    }
	}



    }

    /**
     * Write the test file for the performance test.
     */
    private void fileWrite(String fileName) {
	OpenFile openFile;
	int i, numBytes;

//	Debug.printf('+',
//		"Sequential write of %d byte file, in %d byte chunks\n",
//		new Integer(FileSize), new Integer(ContentSize));
	if (!Nachos.fileSystem.create(fileName, FileSize)) {
	    Debug.printf('+', "Perf test: can't create %s\n", fileName);
	    return;
	}
	openFile = Nachos.fileSystem.open(fileName);
	if (openFile == null) {
	    Debug.printf('+', "Perf test: unable to open %s\n", fileName);
	    return;
	}
	for (i = 0; i < FileSize; i += ContentSize) {
	    numBytes = openFile.write(Contents, 0, ContentSize);
	    if (numBytes < 10) {
		Debug.printf('+', "Perf test: unable to write %s\n", fileName);
		return;
	    }
	}
    }

    /**
     * Read and verify the file for the performance test.
     */
    private void fileRead(String fileName) {
	OpenFile openFile;
	byte buffer[] = new byte[ContentSize];
	int i, numBytes;

//	Debug.printf('+',
//		"Sequential read of %d byte file, in %d byte chunks\n",
//		new Integer(FileSize), new Integer(ContentSize));

	if ((openFile = Nachos.fileSystem.open(fileName)) == null) {
	    Debug.printf('+', "Perf test: unable to open file %s\n", fileName);
	    return;
	}
	for (i = 0; i < FileSize; i += ContentSize) {
	    numBytes = openFile.read(buffer, 0, ContentSize);
	    if ((numBytes < 10) || !byteCmp(buffer, Contents, ContentSize)) {
		Debug.printf('+', "Perf test: unable to read %s\n", fileName);
		return;
	    }
	}
    }

    /**
     * Compare two byte arrays to see if they agree up to a specified length.
     *
     * @param a
     *            The first byte array.
     * @param b
     *            The second byte array.
     * @param len
     *            The number of bytes to compare.
     * @return true if the arrays agree up to the specified number of bytes,
     *         false otherwise.
     */
    private static boolean byteCmp(byte a[], byte b[], int len) {
	for (int i = 0; i < len; i++)
	    if (a[i] != b[i])
		return false;
	return true;
    }

    @Override
    public void run() {
	// TODO Auto-generated method stub

	performanceTest();

	
	count --;
	//sem.V();
	
	
	
	if (count == 0) {
	    
	   
	    Nachos.fileSystem.checkConsistency();
	    Debug.print('+', "End test:\n");
	}
	
	     Nachos.scheduler.finishThread();
	

	
    }

    public static void start() {
	Nachos.options.FORMAT_DISK = true;
	Nachos.fileSystem.init(Nachos.diskDriver);
	
	for(int i=0; i<10; i++) {
	    nums[i] = 0;
	}
	
	
	// Nachos.fileSystem.checkConsistency();
	Debug.print('+', "Starting stress test:\n");
	if(Nachos.options.CSCAN){
	    Debug.println('+', "--------Using CSCAN");
	}else{
	    Debug.println('+', "--------Using FCFS");
	}
	
	
	for (int i = 1; i <= numOfThread; i++) {

	    NachosThread thread = new NachosThread("Stress test" + i,
		    new StressTest());

	    Nachos.scheduler.readyToRun(thread);
	}

    }

}
