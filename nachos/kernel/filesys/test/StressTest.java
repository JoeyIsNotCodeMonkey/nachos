package nachos.kernel.filesys.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.kernel.filesys.OpenFile;
import nachos.machine.NachosThread;
import nachos.machine.Simulation;

public class StressTest implements Runnable{
    /** Transfer data in small chunks, just to be difficult. */
    private static final int TransferSize = 10;

    /**
     * Copy the contents of the host file "from" to the Nachos file "to"
     *
     * @param from
     *            The name of the file to be copied from the host filesystem.
     * @param to
     *            The name of the file to create on the Nachos filesystem.
     */
    private void copy(String from, String to) {
	File fp;
	FileInputStream fs;
	OpenFile openFile;
	int amountRead;
	long fileLength;
	byte buffer[];

	// Open UNIX file
	fp = new File(from);
	if (!fp.exists()) {
	    Debug.printf('+', "Copy: couldn't open input file %s\n", from);
	    return;
	}

	// Figure out length of UNIX file
	fileLength = fp.length();

	// Create a Nachos file of the same length
	Debug.printf('f', "Copying file %s, size %d, to file %s\n", from,
		new Long(fileLength), to);
	if (!Nachos.fileSystem.create(to, (int) fileLength)) {
	    // Create Nachos file
	    Debug.printf('+', "Copy: couldn't create output file %s\n", to);
	    return;
	}

	openFile = Nachos.fileSystem.open(to);
	Debug.ASSERT(openFile != null);

	// Copy the data in TransferSize chunks
	buffer = new byte[TransferSize];
	try {
	    fs = new FileInputStream(fp);
	    while ((amountRead = fs.read(buffer)) > 0)
		openFile.write(buffer, 0, amountRead);
	} catch (IOException e) {
	    Debug.print('+', "Copy: data copy failed\n");
	    return;
	}
	// Close the UNIX and the Nachos files
	// delete openFile;
	try {
	    fs.close();
	} catch (IOException e) {
	}
    }

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
    private static final String FileName = "TestFile";
    private static final String FileName2 = "TestFile2";
    /** Test data to be written to the file in the performance test. */
    private static final String ContentString = "1234567890";

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
	Debug.print('+', "Starting file system performance test:\n");
	Simulation.stats.print();
	fileWrite();
	Debug.print('+', "Writingggg:\n");
	fileRead();
	Debug.print('+', "Readinging:\n");
	if (!Nachos.fileSystem.remove(FileName)) {
	    Debug.printf('+', "Perf test: unable to remove %s\n", FileName);
	    return;
	}
	Simulation.stats.print();
    }
    




    /**
     * Write the test file for the performance test.
     */
    private void fileWrite() {
	OpenFile openFile;
	int i, numBytes;

	Debug.printf('+',
		"Sequential write of %d byte file, in %d byte chunks\n",
		new Integer(FileSize), new Integer(ContentSize));
	if (!Nachos.fileSystem.create(FileName, FileSize)) {
	    Debug.printf('+', "Perf test: can't create %s\n", FileName);
	    return;
	}
	openFile = Nachos.fileSystem.open(FileName);
	if (openFile == null) {
	    Debug.printf('+', "Perf test: unable to open %s\n", FileName);
	    return;
	}
	for (i = 0; i < FileSize; i += ContentSize) {
	    numBytes = openFile.write(Contents, 0, ContentSize);
	    if (numBytes < 10) {
		Debug.printf('+', "Perf test: unable to write %s\n", FileName);
		return;
	    }
	}
    }

    /**
     * Read and verify the file for the performance test.
     */
    private void fileRead() {
	OpenFile openFile;
	byte buffer[] = new byte[ContentSize];
	int i, numBytes;

	Debug.printf('+',
		"Sequential read of %d byte file, in %d byte chunks\n",
		new Integer(FileSize), new Integer(ContentSize));

	if ((openFile = Nachos.fileSystem.open(FileName)) == null) {
	    Debug.printf('+', "Perf test: unable to open file %s\n", FileName);
	    return;
	}
	for (i = 0; i < FileSize; i += ContentSize) {
	    numBytes = openFile.read(buffer, 0, ContentSize);
	    if ((numBytes < 10) || !byteCmp(buffer, Contents, ContentSize)) {
		Debug.printf('+', "Perf test: unable to read %s\n", FileName);
		return;
	    }
	}
    }
    
    
    private void performanceTest2() {
	Debug.print('+', "Starting file system performance test2:\n");
	Simulation.stats.print();
	fileWrite2();
	fileRead2();
	if (!Nachos.fileSystem.remove(FileName2)) {
	    Debug.printf('+', "Perf test: unable to remove %s\n", FileName2);
	    return;
	}
	Simulation.stats.print();
    }
    




    /**
     * Write the test file for the performance test.
     */
    private void fileWrite2() {
	OpenFile openFile;
	int i, numBytes;

	Debug.printf('+',
		"Sequential write of %d byte file, in %d byte chunks\n",
		new Integer(FileSize), new Integer(ContentSize));
	if (!Nachos.fileSystem.create(FileName2, FileSize)) {
	    Debug.printf('+', "Perf test: can't create %s\n", FileName2);
	    return;
	}
	openFile = Nachos.fileSystem.open(FileName2);
	if (openFile == null) {
	    Debug.printf('+', "Perf test: unable to open %s\n", FileName2);
	    return;
	}
	for (i = 0; i < FileSize; i += ContentSize) {
	    numBytes = openFile.write(Contents, 0, ContentSize);
	    if (numBytes < 10) {
		Debug.printf('+', "Perf test: unable to write %s\n", FileName2);
		return;
	    }
	}
    }

    /**
     * Read and verify the file for the performance test.
     */
    private void fileRead2() {
	OpenFile openFile;
	byte buffer[] = new byte[ContentSize];
	int i, numBytes;

	Debug.printf('+',
		"Sequential read of %d byte file, in %d byte chunks\n",
		new Integer(FileSize), new Integer(ContentSize));

	if ((openFile = Nachos.fileSystem.open(FileName2)) == null) {
	    Debug.printf('+', "Perf test: unable to open file %s\n", FileName2);
	    return;
	}
	for (i = 0; i < FileSize; i += ContentSize) {
	    numBytes = openFile.read(buffer, 0, ContentSize);
	    if ((numBytes < 10) || !byteCmp(buffer, Contents, ContentSize)) {
		Debug.printf('+', "Perf test: unable to read %s\n", FileName2);
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
	if(NachosThread.currentThread().name.equalsIgnoreCase("Stress test1")){
	    performanceTest();
	}else{
	    performanceTest2();
	}
	Nachos.scheduler.finishThread();
	
    }
    
    public static void start() {
	NachosThread thread1 = new NachosThread("Stress test1",
		new StressTest());	
	NachosThread thread2 = new NachosThread("Stress test2",
		new StressTest());
//	NachosThread thread3 = new NachosThread("Stress test3",
//		new StressTest());
//	NachosThread thread4 = new NachosThread("Stress test4",
//		new StressTest());
//	
	Nachos.scheduler.readyToRun(thread1);
	Nachos.scheduler.readyToRun(thread2);
//	Nachos.scheduler.readyToRun(thread3);
//	Nachos.scheduler.readyToRun(thread4);
    }

}
