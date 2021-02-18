//libraries
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import static java.lang.System.*;
import java.text.SimpleDateFormat;
/**
 * Client.java
 */

public class Client {

    public String server, username; // server, username
    private int port; //port nr
    private Socket clientsocket; //client socket
    private ObjectInputStream sInput; // to read data from the socket
    private ObjectOutputStream sOutput; // to write on the socket
    private SimpleDateFormat sdf; // date and time
    private ClientGUI cg; // client GUI
    private String fileName; //filename to load chat messages from
    private Messages messages; // messages class 
    /**
     * Client constructor
     */
    Client(String server, int port, String username, ClientGUI cg) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.cg = cg;
        this.fileName = username + "_chat_log.txt"; // to load client's saved chat messages
        messages = new Messages();

    }
    /**
     * Displays messages in Client Chat window GUI
     */
    public void displayEvent(String message) {
        messages.add(message); //message class add method to add messages to arraylist
        cg.appendChatRoom(message + "\n");

    }
    /**
     * Displays chat log in Historic log window
     */
    public void displayEventHistoric(String message) {
        cg.appendHistoricLog(message + "\n");
    }


    /**
     * Loads text from file
     *
     * @param fileName - the file used to write chat (fileName =username+"_chat_log.txt";)
     * @catch Error message pops up if no pre-saved file exists.
     * @see cg.ShowChatLoadError();
     * @see displayEventHistoric(); displays file contents in Historic GUI text area
     * 
     */
    public void load() {

        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String s;
            while ((s = br.readLine()) !=
                null)
                displayEventHistoric(s);
            br.close();

        } catch (IOException e) {
            //displays action dialogue error if pre-existing chat log doesn't exist
            cg.ShowChatLoadError();
            return;
        }
    }
    /**
     * Creates and writes client log file
     */

    public void save() {
        File file = new File(fileName);
        try {
            //appends existing file 
            FileWriter filew = new FileWriter(fileName, true);
            PrintWriter printw = new PrintWriter(filew);
            BufferedWriter buffw = new BufferedWriter(filew);
            // gets all chat messages 
            String chat = messages.getMessages();
            String date;
            String saveMsg;
            date = new Date().toString() + "\n";
            saveMsg = "\n" + "MESSAGES FROM " + date;
            //appends existing file
            buffw.write(saveMsg + chat);
            buffw.newLine();
            // flushes and closes buffered writer and printwriter
            printw.flush();
            buffw.flush();
            buffw.close();
            printw.close();
        } catch (IOException eo) {
            displayEvent("Error writing to file  " + fileName);
        }
    }
    /**
     * To start the connection
     */
    public boolean run() {
        // try to connect to the server
        try {
            clientsocket = new Socket(server, port);
        }
        // Error handling
        catch (Exception ec) {
            cg.ShowConnectionError(); //displays connection error dialogue
            cg.disconnectGUI();
            return false;
        }

        // Creating Data Streams
        try {
            sInput = new ObjectInputStream(clientsocket.getInputStream());
            sOutput = new ObjectOutputStream(clientsocket.getOutputStream());
        } catch (IOException eIO) {
            displayEvent("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server 
        new RunClientThread().start();
        // Sends username to the server
        try {
            sOutput.writeObject(username);
        } catch (IOException eIO) {
            displayEvent("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // informs that connection was successful
        return true;
    }


    /**
     * A class that waits for the message from the server and appends JTextArea
     */
    class RunClientThread extends Thread {

        public void run() {
            while (true) {
                try {

                    String message = (String) sInput.readObject();
                    //displays messages in chat room
                    cg.appendChatRoom(message);
                    //adds messages to arraylist in messages class
                    messages.add(message);
                } catch (IOException e) {

                    if (cg != null)

                        break;
                } catch (ClassNotFoundException e2) {
                    displayEvent("Unable to establish a connection");
                }
            }
        }
    }

    /**
     * Method to send a message to the server
     */
    void sendMessage(String message) {

        try {
            sOutput.writeObject(message);
        } catch (IOException e) {
            cg.ShowConnectionError();
        }
    }
    /**
     * Method to close the Input/Output streams and disconnect
     */
    public void disconnect() {

        try {
            if (sInput != null)
                save();
            sInput.close();
        } catch (Exception e) {} 
        try {
            if (sOutput != null)
                sOutput.close();
        } catch (Exception e) {} 
        try {
            if (clientsocket != null)
                clientsocket.close();
        } catch (Exception e) {} 

        // closes GUI 
        try {
            if (cg != null)
                cg.disconnectGUI();
        } catch (Exception e) {}
    }

}
