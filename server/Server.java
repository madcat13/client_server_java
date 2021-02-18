/**
   * Server class
   * @version April 2020
   */
  import java.io.*;
  import java.net.*;
  import java.text.SimpleDateFormat;
  import java.util.*;
  import java.lang.Object;
  import java.text.Format;
  import java.text.DateFormat;

  public class Server {
      // a unique ID for each connection
      private static int uniqueId;
      // an ArrayList to keep the list of the Client
      private ArrayList < ClientThread > al;
      // to display time
      private SimpleDateFormat sdf;
      // the port number to listen for connection
      private int port;
      // the boolean that will be turned off to stop the server
      private boolean keepGoing;
      //  GUI
      private server_GUI sg;
      // chat list to store chat events
      private ArrayList < String > chatList;

      /*
       *  server constructor that receives the port to listen to for connection
       *  as parameter in console
       */

      public Server(int port, server_GUI sg) {
          // the port
          this.port = port;
          this.sg = sg;
          // to display hh:mm:ss
          sdf = new SimpleDateFormat("HH:mm:ss");
          // ArrayList for the Client list
          al = new ArrayList < ClientThread > ();
          //chat list
          chatList = new ArrayList < String > ();
      }

      public void start() {
          keepGoing = true;
          // create socket server and wait for connection requests
          try {
              // the socket used by the server
              ServerSocket serverSocket = new ServerSocket(port);

              // infinite loop to wait for connections
              while (keepGoing) {
                  // default message saying the server is waiting for clients
                  displayEvent("Server waiting for Clients on port " + port + ".");
                  Socket clientsocket = serverSocket.accept(); // accept connection
                  // to stop
                  if (!keepGoing)
                      break;
                  ClientThread t = new ClientThread(clientsocket); // make a thread of it
                  al.add(t); // save it in the ArrayList
                  t.start();
              }
              // close connection
              try {
                  serverSocket.close();
                  for (int i = 0; i < al.size(); ++i) {
                      ClientThread tc = al.get(i);
                      try {
                          tc.sInput.close();
                          tc.sOutput.close();
                          tc.socket.close();
                      } catch (IOException ioE) {
                          //catch exceptions
                      }
                  }
              } catch (Exception e) {
                  displayEvent("Exception closing the server and clients: " + e);
              }
          }
         
          catch (IOException e) {
              String msg = sdf.format(new Date()) + " Exception on new Server Socket: " +
                  e + "\n";
             
          }
      }

      /*
       * For the GUI to stop the server
       */

      protected void stop() {
          keepGoing = false;
          // connect as a Client to exit
          try {
              new Socket("localhost", port);
          } catch (Exception e) {
            
          }
      }

      /*
       * Display an event
       */
      public void displayEvent(String msg) {
          String dateMsg;
          dateMsg = new Date().toString() + " " + msg;
          //add to chatlist
          chatList.add(dateMsg);
          //display events in GUI
          sg.appendEvent(dateMsg + "\n");

      }

      /*
       * Display a message
       */

      private void displayMsg(String msg) {
          String dateMsg;
          dateMsg = new Date().toString() + "\n" + msg;
          sg.appendRoom(dateMsg + "\n");

      }
      //saves events from the text window
      public void serverChatSave() {
          try {
              PrintStream print =
                  new PrintStream(new FileOutputStream("Server_Events_log.txt", true));
              for (String str: chatList)
                  print.println(str + "\n" + "SERVER STOPPED");
              print.close();
          } catch (IOException e) {
             
          }
      }

      //saves client chat room messages in a single predifined file
      public void clientChatsave() {
          String chat;
          String fileName = ("Client_chat_log.txt");
          File file = new File(fileName);
          try {
              //appends existing file 
              FileWriter filew = new FileWriter(fileName, true);
              PrintWriter printw = new PrintWriter(filew);
              BufferedWriter buffw = new BufferedWriter(filew);
              // gets chat chat messages from chat text area
              chat = sg.chat.getText();
              //appends existing file
              buffw.write(chat);
              buffw.newLine();
              //  flushes and closes buffered writer and printwriter
              printw.flush();
              buffw.flush();
              buffw.close();
              printw.close();
          } catch (IOException eo) {
              displayEvent("Error writing to file  " + fileName);
          }
      }



      /*
       *  to broadcast a message to all Clients
       */

      public synchronized void broadcast(String message) {
          // add HH:mm:ss and \n to the message
          String time = sdf.format(new Date());
          String messageLf = time + " " + message + "\n";
          // display message on GUI
          displayMsg(messageLf);

          // loop in reverse order
          // (in case a Client needs to be removed because it has disconnected)
        
          for (int i = al.size(); --i >= 0;) {
              ClientThread ct = al.get(i);
              // write to Client if it fails to remove it from the list
              if (!ct.writeMsg(messageLf)) {
                  al.remove(i);
                  displayEvent("Disconnected Client " + ct.username +
                      " removed from list.");
              }
          }
      }

      // when client loggs out using the LOGOUT message
      synchronized void remove(int id) {
          // scan the array list until we found the Id
          for (int i = 0; i < al.size(); ++i) {
              ClientThread ct = al.get(i);
              // found it
              if (ct.id == id) {
                  al.remove(i);
                  return;
              }
          }
      }



      // One instance of this thread will run for each client
      class ClientThread extends Thread {
          // the socket where to listen/talk
          Socket socket;
          ObjectInputStream sInput;
          ObjectOutputStream sOutput;
          //unique id 
          int id;
          // the Username of the Client
          String username;
          // the only type of message sent
          String msg;
          // the date of connection
          String date;

          // Constructor
          ClientThread(Socket socket) {
              // a unique id
              id = ++uniqueId;
              this.socket = socket;
              /* Creating Data Streams */
              try {
                  // create output first
                  sOutput = new ObjectOutputStream(socket.getOutputStream());
                  sInput = new ObjectInputStream(socket.getInputStream());
                  // read the username
                  username = (String) sInput.readObject();
                  displayEvent(username + " just connected.");
                  displayMsg(username + " CONNECTED.");

              } catch (IOException e) {
                  displayEvent("Exception creating new Input/output Streams: " + e);
                  return;
              }
              // catch ClassNotFoundException
          
              catch (ClassNotFoundException e) {
                  // do nothing
              }
              date = new Date().toString() + "\n";
          }

          // infinite loop
          public void run() {
              // to loop until LOGOUT
              boolean keepGoing = true;
              String combo;
              while (keepGoing) {
                  // read a String (which is an object)
                  try {
                      msg = (String) sInput.readObject();
                  } catch (IOException e) {
                      displayEvent(username + " Exception reading Streams: " + e);
                      break;
                  } catch (ClassNotFoundException e2) {
                      break;
                  }

                  // Switch on the type of message receive
                  if (msg.equals("LOGOUT")) {
                      displayEvent(username + " disconnected with a LOGOUT message.");
                      displayMsg(username + " LOGGED OUT");
                      keepGoing = false;
                  } else if (msg.equals("WHOISIN")) {
                      writeMsg("List of the users connected at " + sdf.format(new Date()) +
                          "\n");
                      // scan all connected users
                      for (int i = 0; i < al.size(); ++i) {
                          ClientThread ct = al.get(i);
                          writeMsg((i + 1) + ") " + ct.username + " since " + ct.date);
                      }
                  } else {
                      broadcast(username + ": " + msg);
                  }
              }

              // remove myself from the arrayList containing the list of the
              // connected Clients
              remove(id);
              close();
          }

        
          private void close() {
              // try to close the connection
              try {
                  if (sOutput != null)
                      sOutput.close();

              } catch (Exception e) {
                
              }
              try {
                  if (sInput != null)
                      sInput.close();
              } catch (Exception e) {
                
              }
              try {
                  if (socket != null)
                      socket.close();
              } catch (Exception e) {
                 
              }

          }

          /*
           * Write a String to the Client output stream
           */

          private boolean writeMsg(String msg) {
              // if Client is still connected send the message to it
              if (!socket.isConnected()) {
                  close();
                  return false;
              }
              // write the message to the stream
              try {
                  sOutput.writeObject(msg);
              }
              // if an error occurs, do not abort just inform the user
              catch (IOException e) {
                  displayEvent("Error sending message to " + username);
                  displayEvent(e.toString());
              }
              return true;
          }
      }
  }