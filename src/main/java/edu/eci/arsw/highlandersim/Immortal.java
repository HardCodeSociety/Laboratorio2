package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Immortal extends Thread {
    private final static Object tieLock =new Object();

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean pause ;
    
    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.pause=false;
    }

    public void run() {

        while (true) {
            Immortal im;
             if(pause){
                synchronized(this){
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Immortal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }
            im = immortalsPopulation.get(nextFighterIndex);
           
            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

   public void fight(Immortal i2) {
        class Helper{
            public void fightSafe(){
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
            
        }
        int thisHash =System.identityHashCode(this);
        int enemyHash = System.identityHashCode(i2);
        
        if(thisHash<enemyHash){
            synchronized (this){
                synchronized(i2){
                    new Helper().fightSafe();
                }
            }
        }else if (thisHash>enemyHash){
            synchronized (i2){
                synchronized(this){
                    new Helper().fightSafe();
                }
            }
        }else{
            synchronized(tieLock){
                synchronized (this){
                    synchronized(i2){
                        new Helper().fightSafe();
                    }
                }
            }
        }
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    
    public void pause(){
        pause=true;
    }
    
    public void resumeI(){
        pause=false;
        synchronized(this){
            this.notifyAll();
        }
    }

}
