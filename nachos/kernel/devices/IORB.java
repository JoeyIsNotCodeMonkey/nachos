package nachos.kernel.devices;

import nachos.kernel.threads.Semaphore;

public class IORB {
    private int sectorNumber;
    private int flag; // 0->read, 1->write
    private byte[] data;
    private int index;
    private Semaphore semaphore;
    
    public IORB(int sectorNumber, int flag, byte[] data, int index,
	    Semaphore semaphore) {
	this.sectorNumber = sectorNumber;
	this.flag = flag;
	this.data = data;
	this.index = index;
	this.semaphore = semaphore;
    }

    public int getSectorNumber() {
        return sectorNumber;
    }

    public void setSectorNumber(int sectorNumber) {
        this.sectorNumber = sectorNumber;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }
    
    
}
