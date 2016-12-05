package nachos.kernel.userprog;

public class pageStatus {
    private int addressSpace;
    private boolean extendRegion;
    
    public pageStatus(){
	addressSpace = -1;
	extendRegion = false;
	
    }

    public int getAddressSpace() {
        return addressSpace;
    }

    public void setAddressSpace(int addressSpace) {
        this.addressSpace = addressSpace;
    }

    public boolean isExtendRegion() {
        return extendRegion;
    }

    public void setExtendRegion(boolean extendRegion) {
        this.extendRegion = extendRegion;
    }
}
