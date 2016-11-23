package nachos.kernel.filesys;

import java.util.HashMap;

import nachos.kernel.threads.Semaphore;

public class FileHeaderTable {
    private HashMap<Integer, FileHeader> fileHeaderTable;
    
    public HashMap<Integer, FileHeader> getFileHeaderTable() {
        return fileHeaderTable;
    }

    public void setFileHeaderTable(HashMap<Integer, FileHeader> fileHeaderTable) {
        this.fileHeaderTable = fileHeaderTable;
    }

    public FileHeaderTable() {
	fileHeaderTable = new HashMap<Integer, FileHeader>();
    }
    
    public void add(int sector, FileHeader fileHeader) {
	fileHeader.setSem(new Semaphore("file header sem "+sector, 1));
	fileHeader.getSem().P();
	fileHeaderTable.put(sector, fileHeader);
    }
    
    public void remove(int sector) {
	fileHeaderTable.get(sector).getSem().V();
	fileHeaderTable.remove(sector);	
    }
    
    public boolean contains(int sector) {
	return fileHeaderTable.containsKey(sector);
    }
    
    public FileHeader get(int sector) {
	//fileHeaderTable.get(sector).getSem().P();
	return fileHeaderTable.get(sector);
    }
    
    public int size() {
	return fileHeaderTable.size();
    }
}
