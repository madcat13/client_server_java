/**
   * Server GUI class
   */
  import javax.swing.*;
  import java.awt.*;
  import java.awt.event.*;

  public class server_GUI extends JFrame implements ActionListener, WindowListener {
      private static final long serialVersionUID = 1L;
      // the stop and start buttons
      private JButton stopStart;
      // JTextArea for the chat room and the events
      public JTextArea chat, event;
      // The port number
      private JTextField tPortNumber;
      // server
      private Server server;

      /**
       * Server constructor that receives the port to listen to for connection as a parameter
       */
      server_GUI(int port) {
          super("Chat Server");
          server = null;
          // in the NorthPanel the PortNumber the Start and Stop buttons
          JPanel north = new JPanel();
          north.add(new JLabel("Port number: "));
          tPortNumber = new JTextField("  " + port);
          north.add(tPortNumber);
          // to stop or start the server
          stopStart = new JButton("Start");
          stopStart.addActionListener(this);
          north.add(stopStart);
          add(north, BorderLayout.NORTH);

          // the event and chat room
          JPanel center = new JPanel(new GridLayout(2, 1));
          chat = new JTextArea(80, 80);
          chat.setEditable(false);
          appendRoom("Chat room.\n");

          center.add(new JScrollPane(chat));
          event = new JTextArea(80, 80);
          event.setEditable(false);
          appendEvent("Events log.\n");
          center.add(new JScrollPane(event));
          add(center);

          // when the user clicks the close button on the frame
          addWindowListener(this);
          setSize(400, 600);
          setVisible(true);
      }

      /**
       * append message to the JTextArea
       * position at the end
       */
      void appendRoom(String str) {
          chat.append(str);
          chat.setCaretPosition(chat.getText().length() - 1);
      }
      /**
       * append event to the JTextArea
       * position at the end
       */
      void appendEvent(String str) {
          event.append(str);
          event.setCaretPosition(chat.getText().length() - 1);
      }

      /**
       * start or stop when clicked
       */
      public void actionPerformed(ActionEvent e) {
          String msg;
          // to stop the server
          if (server != null) {
              // broadcast message to let the users know that server has stopped
              server.broadcast("SERVER STOPPED!");
              // save server events in a log file
              server.serverChatSave();
              //save client chat in client log file
              server.clientChatsave();
              // clear text in event and chat areas
              event.setText("Events log.\n");
              chat.setText("Chat room.\n");
              //stop the server
              server.stop();
              server = null;
              //allow to edit port nr
              tPortNumber.setEditable(true);
              //reset stopStart button 
              stopStart.setText("Start");
              return;
          }
          // to start the server  
          int port;
          try {
              port = Integer.parseInt(tPortNumber.getText().trim());
          } catch (Exception er) {
              appendEvent("Invalid port number.\n");
              return;
          }
          // ceate a new Server
          server = new Server(port, this);
          // start it as a thread
          new ServerRunning().start();
          stopStart.setText("Stop");
          tPortNumber.setEditable(false);
      }

      /*
       * If the user clicks the X button to close the application,
       * close the connection with server to free the port
       */

      public void windowClosing(WindowEvent e) {
          // if Server exists
          if (server != null) {
              try {
                  server.stop(); // ask the server to close the conection
              } catch (Exception eClose) {
                  // do nothing
              }
              server = null;
          }
          // dispose of the frame
          dispose();
          System.exit(0);
      }

      // Ignore the other WindowListener methods
      public void windowClosed(WindowEvent e) {}
      public void windowOpened(WindowEvent e) {}
      public void windowIconified(WindowEvent e) {}
      public void windowDeiconified(WindowEvent e) {}
      public void windowActivated(WindowEvent e) {}
      public void windowDeactivated(WindowEvent e) {}

      /*
       * A thread to run the Server
       */

      class ServerRunning extends Thread {
          public void run() {
              // will execute until it fails

              try {
                  server.start();
                  // the server failed
                  stopStart.setText("Start");
                  tPortNumber.setEditable(true);
              } catch (Exception er) {
                  server.displayEvent("Server crashed \n");
              }
              server = null;
          }
      }
  }
