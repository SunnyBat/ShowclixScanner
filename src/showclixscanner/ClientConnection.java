/*
 * ==========PACKET LIST==========
 * 
 * OUTPUT:
 * 2 = ClientConnection successful packet (send first)
 * 12 = ClientConnection accepted
 * 13 = ClientConnection rejected
 * 23 = Kill connection, we're done here
 * 72 = Sending cookie (which sends two Strings at a time, with the String lengths being sent first)
 * 103 = Cookies all sent, save them to Firefox
 * 
 * INPUT:
 * 23 = Kill connection, we're done here
 */
package showclixscanner;

import java.io.*;
import java.net.*;
import showclixscanner.gui.*;
import java.util.*;

/**
 *
 * @author SunnyBat
 */
public class ClientConnection {

  private ConnectionPanel myPanel;
  private String address;
  private Socket mySocket;
  private InputStream myInput;
  private OutputStream myOutput;
  private long lastConnectionTime;
  private int failCount;

  public ClientConnection(Socket sock, ConnectionPanel p) {
    try {
      myPanel = p;
      myPanel.setConnection(this);
      mySocket = sock;
      mySocket.setSoTimeout(5000);
      address = mySocket.getInetAddress().getHostAddress();
      myInput = mySocket.getInputStream();
      myOutput = mySocket.getOutputStream();
      println("Stream loaded -- connection to client successful");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void start() {
    ShowclixScanner.startBackgroundThread(new Runnable() {
      @Override
      public void run() {
        println("Listening for input");
        while (!mySocket.isClosed()) {
          listen();
        }
      }
    }, "Thread for " + mySocket.getInetAddress().getHostAddress());
  }

  public ConnectionPanel getConnectionPanel() {
    return myPanel;
  }

  public ClientConnection(String adrs, Socket sock, InputStream in, OutputStream out) {
    myPanel = new ConnectionPanel();
    address = adrs;
    mySocket = sock;
    myInput = in;
    myOutput = out;
  }

  public void listen() {
    try {
      int code = myInput.read();
      if (code == 23) {
        println("Kill packet found.");
        NetworkHandler.killConnection(this);
      }else if (code == -1) {
        println("Connection lost.");
        NetworkHandler.killConnection(this);
      }
    } catch (IOException e) {
      if (System.currentTimeMillis() - lastConnectionTime < 4000) {
        if (++failCount >= 100) {
          println("Connection to client has been lost.");
          NetworkHandler.killConnection(this);
        }
      } else {
        failCount = 0;
        println("No input found.");
      }
      lastConnectionTime = System.currentTimeMillis();
    } catch (Exception e) {
      System.out.println("ERROR WITH STREAM");
      e.printStackTrace();
    }
  }
  
  public void sendWebsiteAddress(String address) {
    try {
      myOutput.write(86);
      writeString(address);
    } catch (Exception e) {
      
    }
  }

  public void writeHttpCookie(HttpCookie cookie) {
    if (myOutput == null) {
      println("================NO OUTPUT TO ADDRESS " + address + "================");
      return;
    }
    try {
      myOutput.write(72);
      myOutput.write(cookie.getName().getBytes().length);
      myOutput.write(cookie.getValue().getBytes().length);
      myOutput.write(cookie.getName().getBytes());
      myOutput.write(cookie.getValue().getBytes());
      myOutput.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void endCookies() {
    if (myOutput == null) {
      println("================NO OUTPUT TO ADDRESS " + address + "================");
      return;
    }
    try {
      myOutput.write(103);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void accept() {
    if (myOutput == null) {
      println("================NO OUTPUT TO ADDRESS " + address + "================");
      return;
    }
    try {
      myOutput.write(12);
    } catch (Exception e) {
      e.printStackTrace();
    }
    start();
  }

  public void reject() {
    if (myOutput == null) {
      println("================NO OUTPUT TO ADDRESS " + address + "================");
      return;
    }
    try {
      myOutput.write(13);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void killConnection() {
    try {
      if (myOutput != null) {
        try {
          myOutput.write(23);
        } catch (Exception e) {
          println("Unable to send kill packet.");
        }
      }
    } catch (Exception iOException) {
      println("Unable to close streams.");
      iOException.printStackTrace();
    }
    closeStreams();
  }

  private void closeStreams() {
    if (mySocket != null) {
      try {
        mySocket.close();
      } catch (IOException iOException) {
        System.out.println("Unable to close socket!");
      }
    }
  }

  public void writeString(String str) {
    if (myOutput == null) {
      println("================NO OUTPUT TO ADDRESS " + address + "================");
    } else {
      try {
        //myOutput.write(65);
        myOutput.write(str.length());
        myOutput.write(str.getBytes());
      } catch (IOException iOException) {
        iOException.printStackTrace();
      }
    }
  }

  public String getAddress() {
    return address;
  }

  public void println(String msg) {
    System.out.println(msg);
    myPanel.println(msg);
    NetworkHandler.println(mySocket.getInetAddress().getHostAddress(), msg);
  }
}
