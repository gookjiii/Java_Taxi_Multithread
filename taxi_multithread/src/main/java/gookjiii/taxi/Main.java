package main.java.gookjiii.taxi;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Dispatcher dispatcher = new DispatcherImpl(5, 20);
        dispatcher.run();
    }
}