package nachos.kernel.userprog;

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
    Map<int[], Integer> map;
    
    
    
    public BackingStore(DiskDriver disk){
	this.diskDriver = disk;
	numDiskSectors = diskDriver.getNumSectors();
	sectors = new boolean[numDiskSectors];
	map = new  HashMap <int[],  Integer>();
    }
    
    
    public void writeBack(int spaceID, int VPN, byte[]data){
	 
	int sectorToWrite = 1;
	
	while(sectors[sectorToWrite]==true){
	    sectorToWrite++;
	}
	
	sectors[sectorToWrite] = true;
	
	int [] keys = new int[2];
	keys[0] = spaceID;
	keys[1] = VPN;
	
	map.put(keys, sectorToWrite);
	
	diskDriver.writeSector(sectorToWrite, data, 0);
	
	
    }
    
    
    public byte[] readData(int spaceID, int VPN){
	
	Debug.println('+', "adfasdfsdf");
	
	byte[] data = new byte[Machine.PageSize];
	
	int [] keys = new int[2];
	keys[0] = spaceID;
	keys[1] = VPN;
	
	Integer sector = map.get(keys);
	
	if(sector!=null){
	    diskDriver.writeSector(sector, data, 0);
	}
	
	return data;
	
    } 
    
    public boolean checkForBackup(int spaceID, int VPN){
	boolean result = false;
	int [] keys = new int[2];
	
	keys[0] = spaceID;
	keys[1] = VPN;
	
	
	if(map.containsKey(keys)){
	    Debug.println('+', "tryr");
	    result = true;
	}

	
	return result;
    }
    
    
    
    
    
}
