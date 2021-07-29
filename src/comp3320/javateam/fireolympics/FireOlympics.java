package comp3320.javateam.fireolympics;

public class FireOlympics implements Runnable{

    boolean running;
    private Thread thread;

    public static void main(String [] args){
        new FireOlympics();
    }

    public FireOlympics(){
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }
    public void run(){
        while (running){

        }
    }

}
