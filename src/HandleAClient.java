import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class HandleAClient implements Runnable {
    private Socket socket; // A connected socket

    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;
    private Keyword keyword;
    private static List<Keyword> kwList;
    private ArrayList<HandleAClient> queue;
    public WordSend str;

    
    public HandleAClient(Socket socket, Keyword kw, ArrayList<HandleAClient> queue) throws IOException {
        keyword = kw;
        this.queue = queue;
        this.socket = socket;
        kwList = new LinkedList<>();
    }


    public void run() {
        try {
            outputToClient = new ObjectOutputStream(socket.getOutputStream());
            inputFromClient = new ObjectInputStream(socket.getInputStream());

            while (true) {
                str = (WordSend) inputFromClient.readObject();

                if (str != null) {
                    synchronized (queue) {
                        queue.add(this);
                    }

                    synchronized (this) {
                        this.wait();
                    }
                    synchronized (keyword) {
                        kwList = keyword.guessFromString(str.getText());
                        outputToClient.writeObject(kwList);
                    }
                    str = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
