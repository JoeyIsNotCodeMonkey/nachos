package nachos.kernel.userprog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.kernel.devices.DiskDriver;
import nachos.machine.Machine;

public class BackingStore {

    private final DiskDriver diskDriver;

    /** Number of sectors on the disk. */
    public final int numDiskSectors;
    
    public final boolean [] sectors;
    ArrayList<Object[]> map;
    
    
    
    public BackingStore(DiskDriver disk){
	this.diskDriver = disk;
	numDiskSectors = diskDriver.getNumSectors();
	sectors = new boolean[numDiskSectors];
	map = new  ArrayList<Object[]>();
    }
    
    
    public void writeBack(int spaceID, int VPN, byte[]data){
	 
	int sectorToWrite = 1;
	
	while(sectors[sectorToWrite]==true){
	    sectorToWrite++;
	}
	
	sectors[sectorToWrite] = true;
	
	Object [] entry = new Object[3];
	entry[0] = spaceID;
	entry[1] = VPN;
	entry[2] = sectorToWrite;
	
	
	map.add(entry);
	
	diskDriver.writeSector(sectorToWrite, data, 0);
	
	
    }
    
    
    public byte[] readData(int spaceID, int VPN){
	
	
	
	byte[] data = new byte[Machine.PageSize];
	int sectorToRead = -1;
	
	for(Object[]e:map){
	    if((int)e[0]==spaceID && (int)e[1] ==VPN){
		sectorToRead = (int)e[2];
	    }
	}
	
	if(sectorToRead!=-1)
	    diskDriver.writeSector(sectorToRead, data, 0);
	
	
	return data;
	
    } 
    
    public boolean checkForBackup(int spaceID, int VPN){
	
	for(Object[] e:map){
	    
	    if((int)e[0]==spaceID && (int)e[1] ==VPN){
		return true;
	    }
	    	    
	}
	

	
	return false;
    }
    
    
    
    
    
}
