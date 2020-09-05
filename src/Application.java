public class Application {
    public static void main(String[]args){
        PrintAsync print = new PrintAsync();
        Loader loader = new Loader(print);

        print.start();
        loader.start();
    }
}

class PrintAsync extends Thread {
    @Override
    public void run() {
        try {
            sleep(5000);

            System.out.println("\nOla");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Loader extends Thread {
    private final Thread thread;

    public Loader(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        try {
            System.out.print("Loading");
            while(true) {
                sleep(400);
                if(!thread.isAlive())
                    break;

                System.out.print(".");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
