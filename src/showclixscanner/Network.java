package showclixscanner;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author SunnyBat
 */
public class Network {

  private static String address;
  private static ServerSocket mySock;
  private static final int port = 9243;
  private static InputStream myInput;
  private static OutputStream myOutput;
  private static List<HttpCookie> cookies = new ArrayList();

  public static void initializeConnection() {
    try {
      mySock = new ServerSocket(port);
      while (mySock.isBound()) {
        System.out.println("Listening on port " + port);
        Socket mySocket = mySock.accept();
        System.out.println("Client found at: " + mySocket.getInetAddress().getHostAddress());
        myInput = mySocket.getInputStream();
        myOutput = mySocket.getOutputStream();
        System.out.println("Stream loaded -- connection to client successful");
        if (!cookies.isEmpty()) {
          System.out.println("Cookies found... Sending cookies over now.");
          Iterator<HttpCookie> myIt = cookies.iterator();
          while (myIt.hasNext()) {
            writeHttpCookie(myIt.next());
          }
          endCookies();
          System.out.println("Cookies sent!");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void writeHttpCookie(HttpCookie cookie) {
    if (myOutput == null) {
      System.out.println("================NO OUTPUT3222222222222222222222222222222");
      cookies.add(cookie);
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

  public static void endCookies() {
    if (myOutput == null) {
      System.out.println("================NO OUTPUT3222222222222222222222222222222");
      return;
    }
    try {
      myOutput.write(103);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void killConnection() {
    try {
      myInput.close();
      myInput = null;
      myOutput.close();
      myOutput = null;
    } catch (IOException iOException) {
      System.out.println("Unable to close streams.");
    }
  }

  public static void writeString(String str) {
  }

  public static void setAddress(String add) {
    if (validIPAddress(add)) {
      address = add;
      try {
        mySock = new ServerSocket(port);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
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
//  public static void initializeConnection() {
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
