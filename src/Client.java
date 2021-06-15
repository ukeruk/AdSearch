import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


class Client {

    private static ObjectOutputStream toServer;
    private static ObjectInputStream fromServer;

    private Socket socket;

    public Client() {

        try {
            // Create a socket to connect to the server
            socket = new Socket("localhost", 8000);
            // Create an output stream to send data to the server
            toServer = new ObjectOutputStream(socket.getOutputStream());

            // Create an input stream to receive data
            // from the server
            fromServer = new ObjectInputStream(socket.getInputStream());

        } catch (IOException ex) {
        }
    }

    public void writeToServer(WordSend s) {
        try {
            toServer.writeObject(s);
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Keyword> readFromServer() {

        try {
            return (List<Keyword>) fromServer.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
