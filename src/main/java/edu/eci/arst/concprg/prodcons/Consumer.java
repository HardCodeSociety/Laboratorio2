/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private Queue<Integer> queue;
    long stockLimit;
    
    public Consumer(Queue<Integer> queue,long stockLimit){
        this.queue=queue;        
        this.stockLimit=stockLimit;
    }
    
    @Override
    public void run() {
        while (true) {
                if (queue.size() > 0) {
                    int elem=queue.poll();
                    if(queue.size()== stockLimit-1){
                        synchronized(queue){
                            queue.notify();
                        }
                    }
                    
                    System.out.println("Consumer consumes "+elem);                                
                }else{
                    synchronized(queue){
                        try {
                            queue.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}

