package nachos.kernel.userprog;

import java.util.LinkedList;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.machine.CPU;
import nachos.machine.InterruptHandler;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.machine.Timer;
import nachos.util.FIFOQueue;
import nachos.util.Queue;
import nachos.util.ReadyList;

public class RR<T> extends java.util.LinkedList<T> implements ReadyList<T>{
   
   
    
    private Queue<T> queue = new FIFOQueue<T>();
    
    
    
    public Queue<T> getQueue() {
        return queue;
    }


    
    
  

    
    
}

