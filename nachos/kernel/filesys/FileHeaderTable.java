package nachos.kernel.filesys;

import java.util.HashMap;

import nachos.kernel.threads.Lock;
import nachos.kernel.threads.Semaphore;

public class FileHeaderTable {
    private HashMap<Integer, FileHeader> fileHeaderTable;
    protected Semaphore lock;
    
    public HashMap<Integer, FileHeader> getFileHeaderTable() {
        return fileHeaderTable;
    }

    public void setFileHeaderTable(HashMap<Integer, FileHeader> fileHeaderTable) {
        this.fileHeaderTable = fileHeaderTable;
    }

    public FileHeaderTable() {
	fileHeaderTable = new HashMap<Integer, FileHeader>();
	lock = new Semaphore("file operation lock",1);
    }
    
    public void add(int sector, FileHeader fileHeader) {
	
	
	
	fileHeader.setSem(new Semaphore("file header sem "+sector, 1));
//	if(sector!=0&&sector!=1){
//	    fileHeader.getSem().P();
//	}
	   
	fileHeaderTable.put(sector, fileHeader);

    }
    
    public void remove(int sector) {
	
	lock.P();
	fileHeaderTable.remove(sector);	
	lock.V();
    }
    
    public boolean contains(int sector) {
	return fileHeaderTable.containsKey(sector);
    }
    
    public FileHeader get(int sector) {
	
//	if(sector == 0 || sector ==1){
//	    return fileHeaderTable.get(sector);
//	}
//	    fileHeaderTable.get(sector).getSem().P();
//	    fileHeaderTable.get(sector).getSem().V();
	return fileHeaderTable.get(sector);
    }
    
    public void release(int sector) {
	fileHeaderTable.get(sector).getSem().V();	
    }
    
    
    public void lock(int sector) {
	fileHeaderTable.get(sector).getSem().P();	
    }
    

    
    public int size() {
	return fileHeaderTable.size();
    }


}
