import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;
import static java.lang.System.*;
import java.text.SimpleDateFormat;

/**
 * ClientGUI.java
 */
public class ClientGUI extends JFrame implements ActionListener, WindowListener {

    private JFrame frame; // for error messages
    public JLabel label, labelServer, labelPort, labelUsername; //labels
    public JTextField textFieldMessage, textFieldServer, textFieldfPort, textFieldfUsername; // to hold message, server, port and username input
    private JButton login, logout, whoisin, clearChat, loadChat; // buttons
    public JTextArea textAreaChat, textAreaHistoric; // text areas for Historic messages and Chat Room
    private boolean loggedin; // logged in boolean
    private Client thisClient; //client
    private int preSetPortnr; // default port number
    private String preSetLocalhost, preSetUsername; // to hold default host and username 

    // Colors for GUI
    public Color PALE_BLUE;
    public Color DARK_BLUE;
    public Color DEEP_BLUE;


    /**
     * myClientGUI constructor
     */
    ClientGUI(String localhost, int port, String username) {
        //client GUI
        super("Client GUI");

        preSetLocalhost = localhost;
        preSetPortnr = port;
        preSetUsername = username;
        setSize(600, 750); //GUI size

        // colors created 
        PALE_BLUE = new Color(238, 244, 249);
        DARK_BLUE = new Color(21, 20, 43);
        DEEP_BLUE = new Color(35, 56, 125);

        //north panels
        JPanel northTop = new JPanel(new GridLayout(1, 5, 1, 3)); //for text fields,labels for server, host and username
        JPanel north = new JPanel(new GridLayout(3, 1)); //for the label and message input 
        north.setBackground(DEEP_BLUE); //colour for for the north panels
        textFieldServer = new JTextField(localhost); // JTextField with default values pre-set for server
        textFieldfPort = new JTextField("" + port); // JTextField with default values pre-set for port
        textFieldfUsername = new JTextField(username); // JTextField with default values pre-set for username
        textFieldfPort.setHorizontalAlignment(SwingConstants.RIGHT);

        //labels and styling for top north panel- font, color, size, background
        labelServer = new JLabel("Server: ", SwingConstants.RIGHT);
        labelServer.setForeground(DARK_BLUE);
        labelServer.setFont(new Font("Arial", Font.BOLD, 14));
        northTop.add(labelServer);
        northTop.add(textFieldServer);
        labelPort = new JLabel("Port Nr: ", SwingConstants.RIGHT);
        labelPort.setForeground(DARK_BLUE);
        labelPort.setFont(new Font("Arial", Font.BOLD, 14));
        northTop.add(labelPort);
        northTop.add(textFieldfPort);
        labelUsername = new JLabel("Username: ", SwingConstants.RIGHT);
        labelUsername.setForeground(DARK_BLUE);
        labelUsername.setFont(new Font("Arial", Font.BOLD, 14));
        northTop.add(labelUsername);
        northTop.add(textFieldfUsername);
        northTop.setBackground(PALE_BLUE);
        northTop.add(new JLabel(""));
        north.add(northTop);

        //labels and styling for north panel- font, color, size, background
        label = new JLabel("Please log in to join the chatroom", SwingConstants.CENTER);
        label.setForeground(Color.WHITE); // sets the color of text
        label.setFont(new Font("Arial", Font.BOLD, 20));
        north.add(label);

        //Text field for message input in  north panel
        textFieldMessage = new JTextField("");
        textFieldMessage.setVisible(false);
        textFieldMessage.setBackground(Color.WHITE);
        textFieldMessage.setForeground(Color.BLACK);
        textFieldMessage.setFont(new Font("Arial", Font.PLAIN, 19));
        north.add(textFieldMessage);
        add(north, BorderLayout.NORTH);


        //text area for Chat
        textAreaChat = new JTextArea(80, 80);
        textAreaChat.setForeground(Color.BLACK);
        textAreaChat.setFont(new Font("Arial", Font.PLAIN, 16));

        //text area for Historic Chat messages loaded
        textAreaHistoric = new JTextArea(80, 80);
        textAreaHistoric.setForeground(Color.GRAY);
        textAreaHistoric.setFont(new Font("Arial", Font.ITALIC, 16));

        //center panel components to hold historic chat and chat room text areas
        JPanel center = new JPanel(new GridLayout(2, 1));
        textAreaHistoric.setEditable(false);
        textAreaHistoric.setBackground(Color.WHITE);
        appendHistoricLog("Historic log.\n");
        center.add(new JScrollPane(textAreaHistoric));
        textAreaChat.setEditable(false);
        textAreaChat.setBackground(PALE_BLUE);
        appendChatRoom("Chat room.\n");
        center.add(new JScrollPane(textAreaChat));
        add(center);

        // buttons with default states for south panel
        login = new JButton("Login");
        login.addActionListener(this);
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setEnabled(false);
        whoisin = new JButton("Who is in");
        whoisin.addActionListener(this);
        whoisin.setEnabled(false);
        clearChat = new JButton("Clear Chat");
        clearChat.addActionListener(this);
        clearChat.setEnabled(false);
        loadChat = new JButton("Load Chat");
        loadChat.addActionListener(this);
        loadChat.setEnabled(false);

        //south panel
        JPanel south = new JPanel();
        south.add(login);
        south.add(logout);
        south.add(whoisin);
        south.add(clearChat);
        south.add(loadChat);
        add(south, BorderLayout.SOUTH);
        south.setBackground(DEEP_BLUE);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        textFieldMessage.requestFocus();

    }


    /**
     * Appends text in the chat room Text area
     */
    public void appendChatRoom(String str) {
        textAreaChat.append(str);
        textAreaChat.setCaretPosition(textAreaChat.getText().length() - 1);

    }

    /**
     * Appends text in the historic log Text area
     */
    public void appendHistoricLog(String str) {
        this.textAreaHistoric.append(str);
        this.textAreaHistoric.setCaretPosition(textAreaHistoric.getText().length() - 1);
    }
    /**
     * When the connection unexpectedly fails, for example, server diconnects or port nr doesn't match
     */
    public void disconnectGUI() {
        //button resets
        login.setEnabled(true);
        logout.setEnabled(false);
        whoisin.setEnabled(false);
        clearChat.setEnabled(false);
        loadChat.setEnabled(false);

        //label resets to login message
        textFieldMessage.setEditable(false);
        label.setText("Please log in to join the chatroom");

        //message input field cleared
        textFieldMessage.setText(""); // clears message input field
        textFieldfUsername.setText(""); // clears username field
        textFieldfPort.setText("" + preSetPortnr); // reset port number to default
        textFieldServer.setText(preSetLocalhost); // reset host  to default

        //allows fields to be editable
        textFieldServer.setEditable(true);
        textFieldfPort.setEditable(true);
        textFieldfUsername.setEditable(true); // set username fiels editable
        textFieldMessage.removeActionListener(this); /// removes action listenter from message input field
        loggedin = false; // disconnects the client
    }

    /**
     * To clear chat area and historic log
     */
    public void clear() {
        textAreaChat.setText("");
        textAreaHistoric.setText("");
        return;
    }

    /**
     * Message dialogue for server connection error
     */
    public void ShowConnectionError() {
        frame = new JFrame("Error");
        JOptionPane.showMessageDialog(frame,
            "Please make sure that server is running",
            "Error connectiong to server",
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Message  dialogue for Port connection error
     */
    public void ShowConnectionErrorPort() {
        frame = new JFrame("Error");
        JOptionPane.showMessageDialog(frame,
            "Please enter a valid port number!",
            "Error connectiong to server",
            JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Message dialogue for chat load error
     */
    public void ShowChatLoadError() {
        frame = new JFrame("Load Error");
        JOptionPane.showMessageDialog(frame,
            "You have no saved chat messages to load",
            "No historic messages found!",
            JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Actions captured and executed when a Jtextfield or a button is clicked
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logout) {
            thisClient.sendMessage("LOGOUT");
            appendChatRoom("\n");
            thisClient.disconnect(); // disconnects the user and saves the chat
            clear(); // clears chat messages in text area window

            //resets JTextFields, buttons and labels to default state
            textFieldServer.setEditable(true);
            textFieldfPort.setEditable(true);
            textFieldfUsername.setText("");
            textFieldfUsername.setEditable(true);
            textFieldMessage.setVisible(false);
            textAreaChat.setBackground(Color.WHITE);
            textAreaChat.setBackground(PALE_BLUE);
            textAreaHistoric.setText("Historic log");
            textAreaChat.setText("Chat room");
            return;
        }
        if (e.getSource() == clearChat) {
            //clears Chat room and Historic log Jtextarea
            clear();
            return;
        }

        if (e.getSource() == loadChat) {
            //manually loads clients chat log into Historic window
            thisClient.load();
            return;
        }

        if (e.getSource() == whoisin) {
            //returns a list of all connected users
            thisClient.sendMessage("WHOISIN");
            return;
        }

        if (loggedin) {
            //gets text from message input field and sends it to server
            thisClient.sendMessage(new String(textFieldMessage.getText()));
            textFieldMessage.setText("");
            return;
        }

        if (e.getSource() == login) {
            //sets username after login button is clicked   
            String username = textFieldfUsername.getText().trim();
            //returns if username field is left blank
            if (username.isEmpty())
                return;

            //sets host after login button is clicked 
            String host = textFieldServer.getText().trim();
            // if server is blank, do not proceed with logging in 
            if (host.isEmpty())
                return;

            //sets port nr after login button is clicked 
            int port;
            try {
                port = Integer.parseInt(textFieldfPort.getText().trim());
            } catch (Exception er) {
                // if port nr is blank, do not proceed with logging in the user and display error dialogue
                ShowConnectionErrorPort();
                return;
            }
            // initiate a new client
            thisClient = new Client(host, port, username, this);
            // do not proceed if the client can not establish a connection
            if (thisClient.run() == false)
                return;

            // if the user has been logged in successfully make GUI do the following actions
            loggedin = true;
            textFieldMessage.setText("");
            label.setText("Enter your message below");
            login.setEnabled(false); // disable login button
            textFieldMessage.setVisible(true); //set text input for chat visible
            textFieldMessage.setEditable(true); //set text input for chat editable
            textAreaChat.setBackground(PALE_BLUE); //Color for Chat Room Text area
            textAreaHistoric.setText(""); //clear text area in Historic Log
            textAreaChat.setText(""); //clear text area in Chat Room
            thisClient.load(); //load saved client chat record

            // display the following message to the user in Chat window once logged in
            String date;
            date = new Date().toString() + "\n";
            appendChatRoom("Logged in as " + username + " on " + date);

            logout.setEnabled(true); // enable logout button
            whoisin.setEnabled(true); // enable whoisin button
            clearChat.setEnabled(true); // enable clearChat button
            loadChat.setEnabled(true); // enable loadChat button
            textFieldServer.setEditable(false); // disable Server input JTextField
            textFieldfPort.setEditable(false); // disable Port input JTextField
            textFieldMessage.addActionListener(this); // Action listener for when the user enter a message
        }

    }


    /**
     * WindowListener methods
     */
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    public void windowClosing(WindowEvent e) {

        if (thisClient != null) {
            try {
                thisClient.disconnect(); // close connection
            } catch (Exception eClose) {}
            thisClient = null;
        }

        dispose();
        System.exit(0);
    }
}
