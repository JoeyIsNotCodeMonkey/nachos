package nachos.kernel.userprog;

import java.util.LinkedList;

import nachos.Debug;
import nachos.util.Queue;

public class Feedback<T> extends java.util.LinkedList<T> implements Queue<T> {

    private LinkedList<T> queue1 = new LinkedList<T>();
    private LinkedList<T> queue2 = new LinkedList<T>();
    private LinkedList<T> queue3 = new LinkedList<T>();
    private LinkedList<T> queue4 = new LinkedList<T>();
    private LinkedList<T> queue5 = new LinkedList<T>();
    
    private LinkedList<LinkedList> queueManager ;
    
   public Feedback(){

       queueManager = new LinkedList<LinkedList>();
       queueManager.add(queue1);		//1 has the highest priority 
       queueManager.add(queue2);
       queueManager.add(queue3);
       queueManager.add(queue4);
       queueManager.add(queue5);		//5 has the lowest priority
       
   }
   @Override
   public boolean offer(T e){
       
       if(e instanceof UserThread){
	   
	   queueManager.get(((UserThread)e).getPriority()).offer(e);	
	   
	   Debug.println('+', ((UserThread)e).space.getSpaceID() + "__________________" + ((UserThread)e).getPriority() );
	   
	   if(((UserThread)e).getPriority()<4){
	       ((UserThread)e).setPriority(((UserThread)e).getPriority()+1);
	       
	   }
	  	   
       }else{
	   queueManager.get(0).offer(e);
       }
       
       
       
       return true;
   }
   
   
   @Override
   public T poll(){
       
      for(int i =0; i<5;i++){
	 if( queueManager.get(i).peek()!=null){
	     return (T)queueManager.get(i).poll();
	 }
      }
    return null;
       
   }
   
   
   @Override
   public boolean isEmpty(){
       for(LinkedList l : queueManager){
	   if(!l.isEmpty()){
	       return false;
	   }
       }
       return true;
   }
   
   @Override
   public int size(){
       int count = 0;
       for(LinkedList l : queueManager){
	   count += l.size();
       }
       return count;
   }
   
   
   
    
}
