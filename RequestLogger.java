import java.util.ArrayList;

public class RequestLogger implements Runnable {

    static final ArrayList<String> logs = new ArrayList<>();

    @Override
    public void run() {
        while (true) {
            try {
                for (int i = 0; i < logs.size(); i++)
                    System.out.println(logs.get(i));

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addRequest(String requestLog) {
        logs.add(requestLog);
    }
}
