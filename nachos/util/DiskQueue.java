package nachos.util;

public class DiskQueue<T> extends java.util.LinkedList<T> implements Queue<T> {

    @Override
    public boolean offer(T e) {
	synchronized (this) {
            this.addLast(e);
        }
	
	return true;
    }
    
    
    @Override
    public T peek(){
	synchronized (this) {
            return this.getFirst();
        }
    }
    
    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * 
     * @return  the head of this queue, or null if this queue is empty.
     */
    @Override
    public T poll(){
	synchronized (this) {
	    return this.pollFirst();
	}
    }
    
   
}
