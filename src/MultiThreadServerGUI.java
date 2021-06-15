import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;


public class MultiThreadServerGUI extends JFrame {

    private JTextArea jta = new JTextArea();
    private ServerSocket serverSocket;
    Socket socket;
    Keyword kw;
    ArrayList<Thread> clients;
    static ArrayList<HandleAClient> queue;

    public static void main(String[] args) throws IOException {
        new MultiThreadServerGUI();

    }

    public MultiThreadServerGUI() throws IOException {
        clients = new ArrayList<>();
        // Place text area on the frame
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);
        setTitle("MultiThreadServer");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!

        jta.append("MultiThreadServer started at " + new Date() + '\n');
        kw = new Keyword();
        queue = new ArrayList<>();

        int clientNo = 1;
        try {
            serverSocket = new ServerSocket(8000);
            serverSocket.setSoTimeout(500);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (true) {
            // Listen for a new connection request
            try {
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        queue.sort(new PrioritySorter());
                        synchronized (queue.get(0)) {
                            queue.remove(0).notify();
                        }
                        for (HandleAClient hac : queue) {
                            hac.str.hadToWait();
                            if (hac.str.getPriority() > 2)
                                hac.str.setPriority(hac.str.getPriority() + 1);
                        }
                    }
                }
                socket = serverSocket.accept();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (socket != null) {
                // Display the client number
                jta.append("Starting thread for client " + clientNo + " at " + new Date() + '\n');

                // Find the client's host name, and IP address
                InetAddress inetAddress = socket.getInetAddress();
                jta.append("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
                jta.append("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
                Thread task;
                // Create a new task for the connection
                clients.add(task = new Thread(new HandleAClient(socket, kw, queue)));
                task.start();
                clientNo++;
                socket = null;
            }
        }
    }
}
