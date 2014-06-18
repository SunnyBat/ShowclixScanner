package showclixscanner;

import java.net.*;
import java.io.*;
import java.util.*;
import showclixscanner.gui.*;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author SunnyBat
 */
public class NetworkHandler {

  private static String address;
  private static ServerSocket mySock;
  private static final int port = 9243;
  private static List<HttpCookie> cookies = new ArrayList();
  private static List<ClientConnection> connections = new ArrayList();
  private static Connections connectionWindow;
  private static ApproveForm approve;

  public static void listenForConnections() {
    try {
      connectionWindow = new Connections();
      connectionWindow.setVisible(true);
      mySock = new ServerSocket(port);
      while (mySock.isBound()) {
        System.out.println("Listening on port " + port);
        Socket mySocket = mySock.accept();
        System.out.println("Client found at: " + mySocket.getInetAddress().getHostAddress());
        ConnectionPanel mP = new ConnectionPanel(mySocket.getInetAddress().getHostAddress());
        ClientConnection con = new ClientConnection(mySocket, mP);
        final Socket socketCopy = mySocket;
        final CountDownLatch countdown = new CountDownLatch(1);
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
          @Override
          public void run() {
            approve = new ApproveForm(countdown);
            approve.setHeader("Connection from " + socketCopy.getInetAddress().getHostAddress());
            approve.setDescription("A connection has been found. Please approve or disapprove it.");
            approve.setVisible(true);
          }
        });
        countdown.await();
        if (!approve.isApproved()) {
          System.out.println("Connection DISAPPROVED, killing connection...");
          con.reject();
          con.killConnection();
          continue;
        } else {
          System.out.println("Connection approved.");
          con.accept();
        }
        connectionWindow.addConnection(mP, mySocket.getInetAddress().getHostAddress());
        if (!cookies.isEmpty()) {
          System.out.println("Cookies found... Sending cookies over now.");
          Iterator<HttpCookie> myIt = cookies.iterator();
          while (myIt.hasNext()) {
            con.writeHttpCookie(myIt.next());
          }
          con.endCookies();
          System.out.println("Cookies sent!");
        }
        registerConnection(con);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void registerConnection(ClientConnection conn) {
    connections.add(conn);
  }

  public static void writeHttpCookie(HttpCookie cookie) {
    cookies.add(cookie);
    try {
      Iterator<ClientConnection> myIt = connections.iterator();
      ClientConnection tempConnection;
      while (myIt.hasNext()) {
        tempConnection = myIt.next();
        tempConnection.writeHttpCookie(cookie);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void endCookies() {
    try {
      Iterator<ClientConnection> myIt = connections.iterator();
      ClientConnection tempConnection;
      while (myIt.hasNext()) {
        tempConnection = myIt.next();
        tempConnection.endCookies();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void killConnection(ClientConnection c) {
    c.killConnection();
    connectionWindow.removeConnection(c.getConnectionPanel());
    connections.remove(c);
  }

  public static void killAllConnections() {
    try {
      Iterator<ClientConnection> myIt = connections.iterator();
      ClientConnection tempConnection;
      while (myIt.hasNext()) {
        tempConnection = myIt.next();
        tempConnection.killConnection();
        connections.remove(tempConnection);
      }
    } catch (Exception e) {
      System.out.println("Unable to close streams.");
    }
  }

  public static void writeString(String str) {
  }

  public static String getAddress() {
    return address;
  }

  public static boolean validIPAddress(String ipaddress) {
    try {
      String[] parse = ipaddress.split(".");
      if (parse.length != 4) {
        return false;
      }
      for (int a = 0; a < parse.length; a++) {
        if (Integer.parseInt(parse[a]) < 0 || Integer.parseInt(parse[a]) > 255) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {
      System.out.println("Unable to verify valip IP address: " + ipaddress);
      e.printStackTrace();
      return false;
    }
  }

  public static void println(String msg) {
    connectionWindow.println(msg);
  }

  public static void println(String connectionName, String msg) {
    println("[" + connectionName + "] " + msg);
  }
//  public static void listenForConnections() {
//    try {
//      mySock = new ServerSocket(port);
//      Socket inSocket = mySock.accept();
//      CipherInputStream inStream = new CipherInputStream(inSocket.getInputStream(), Cipher.getInstance(""));
//      outStream = mySocket.getOutputStream();
//      println("Streams loaded.");
//      outSocketStream = new Packets(new byte[Packets.bufferSize]);
//      inSocketStream = new Packets(new byte[Packets.bufferSize]);
//    } catch (Exception e) {
//    }
//  }
}
