package showclixscanner;

import java.net.*;
import java.util.*;
import showclixscanner.gui.*;
import java.util.concurrent.CountDownLatch;
import org.bitlet.weupnp.*;

/**
 *
 * @author SunnyBat
 */
public class NetworkHandler {

  private static ServerSocket serverSocket;
  private static final int port = 9243;
  private static int incomingConnectionMode = 1;
  private static volatile boolean usingUPnP;
  private static final List<HttpCookie> cookies = new ArrayList();
  private static volatile List<ClientConnection> connections = new ArrayList();
  private static Connections connectionWindow;
  private static ApproveForm approve;

  public static void listenForConnections() {
    try {
      connectionWindow = new Connections();
      connectionWindow.setVisible(true);
      serverSocket = new ServerSocket(port);
      ShowclixScanner.startNewThread(new Runnable() {
        @Override
        public void run() {
          try {
            enableUPnP();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }, "UPnP Port Mapping Thread");
      while (!serverSocket.isClosed()) {
        println("Listening on port " + port);
        Socket mySocket = null;
        try {
          mySocket = serverSocket.accept();
        } catch (SocketException se) {
          continue;
        }
        println("Client found at: " + mySocket.getInetAddress().getHostAddress());
        ConnectionPanel mP = new ConnectionPanel(mySocket.getInetAddress().getHostAddress());
        ClientConnection con = new ClientConnection(mySocket, mP);
        int mode = getIncomingConnectionMode();
        if (mode == 1) {
          final String address = mySocket.getInetAddress().getHostAddress();
          final CountDownLatch countdown = new CountDownLatch(1);
          javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
              approve = new ApproveForm(countdown);
              approve.setHeader("Connection from " + address);
              approve.setDescription("A connection has been found. Please approve or disapprove it.");
              approve.setVisible(true);
            }
          });
          println("Waiting for connection approval...");
          countdown.await(60, java.util.concurrent.TimeUnit.SECONDS);
          approve.dispose();
          if (!approve.isApproved()) {
            println("Connection DISAPPROVED, killing connection...");
            con.reject();
            con.killConnection();
            continue;
          } else {
            println("Connection approved.");
            con.accept();
          }
        } else {
          if (mode == 2) {
            println("Connection approved.");
            con.accept();
          } else {
            println("Connection DISAPPROVED, killing connection...");
            con.reject();
            con.killConnection();
            continue;
          }
        }
        connectionWindow.addConnection(mP, mySocket.getInetAddress().getHostAddress());
        con.sendWebsiteAddress(Browser.getShowclixLink());
        if (!cookies.isEmpty()) {
          println("Cookies found... Sending cookies over now.");
          Iterator<HttpCookie> myIt = cookies.iterator();
          while (myIt.hasNext()) {
            con.writeHttpCookie(myIt.next());
          }
          con.endCookies();
          println("Cookies sent!");
        }
        registerConnection(con);
      }
      if (!ShowclixScanner.programRunning()) {
        connectionWindow.dispose();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void closeConnectionWindow() {
    if (connectionWindow != null) {
      connectionWindow.dispose();
    }
  }

  /**
   * Checks to see whether connections are available. This checks for if the ServerSocket is open (listening for new connections) or if there are any
   * active connections.
   *
   * @return True if connections are available, false if not
   */
  public static boolean connectionsAvailable() {
    if (serverSocket == null) {
      return false;
    }
    if (!connections.isEmpty()) {
      return true;
    } else {
      return !serverSocket.isClosed();
    }
  }

  public static void enableUPnP() throws Exception {
    if (serverSocket == null) {
      println("ERROR enabling UPnP: serverSocket not initialized!");
      return;
    }
    if (usingUPnP) {
      println("ERROR enabling UPnP: UPnP is already in use!");
      return;
    }
    usingUPnP = true;
    connectionWindow.setRetryUPnPButtonEnabled(false);
    connectionWindow.setRetryUPnPButtonText("Opening Port...");
    println("Starting weupnp");
    GatewayDiscover gatewayDiscover = new GatewayDiscover();
    println("Looking for Gateway Devices...");
    Map<InetAddress, GatewayDevice> gateways = gatewayDiscover.discover();
    if (gateways.isEmpty()) {
      println("No gateways found");
      println("Stopping weupnp");
      connectionWindow.setRetryUPnPButtonEnabled(true);
      connectionWindow.setRetryUPnPButtonText("Retry UPnP");
      usingUPnP = false;
      return;
    }
    println(gateways.size() + " gateway(s) found\n");
    int counter = 0;
    for (GatewayDevice gw : gateways.values()) {
      counter++;
      println("Listing gateway details of device #" + counter
          + "\n\tFriendly name: " + gw.getFriendlyName()
          + "\n\tPresentation URL: " + gw.getPresentationURL()
          + "\n\tModel name: " + gw.getModelName()
          + "\n\tModel number: " + gw.getModelNumber()
          + "\n\tLocal interface address: " + gw.getLocalAddress().getHostAddress() + "\n");
    }
    // choose the first active gateway for the tests
    GatewayDevice activeGW = gatewayDiscover.getValidGateway();
    if (null != activeGW) {
      println("Using gateway:" + activeGW.getFriendlyName());
    } else {
      println("No active gateway device found");
      println("Stopping weupnp");
      connectionWindow.setRetryUPnPButtonEnabled(true);
      connectionWindow.setRetryUPnPButtonText("Retry UPnP");
      usingUPnP = false;
      return;
    }
    // testing PortMappingNumberOfEntries
    Integer portMapCount = activeGW.getPortMappingNumberOfEntries();
    println("GetPortMappingNumberOfEntries=" + (portMapCount != null ? portMapCount.toString() : "(unsupported)"));
    // testing getGenericPortMappingEntry
    PortMappingEntry portMapping0 = new PortMappingEntry();
    if (activeGW.getGenericPortMappingEntry(0, portMapping0)) {
      println("Portmapping #0 successfully retrieved (" + portMapping0.getPortMappingDescription() + ":" + portMapping0.getExternalPort() + ")");
    } else {
      println("Portmapping #0 retrival failed");
    }
    InetAddress localAddress = activeGW.getLocalAddress();
    println("Using local address: " + localAddress.getHostAddress());
    String externalIPAddress = activeGW.getExternalIPAddress();
    println("External address: " + externalIPAddress);
    println("Querying device to see if a port mapping already exists for port " + port);
    PortMappingEntry portMapping = new PortMappingEntry();
    if (activeGW.getSpecificPortMappingEntry(port, "TCP", portMapping)) {
      println("Port " + port + " is already mapped. Aborting test.");
      connectionWindow.setRetryUPnPButtonText("Retry UPnP");
      connectionWindow.setRetryUPnPButtonEnabled(true);
      usingUPnP = false;
      return;
    } else {
      println("Mapping free. Sending port mapping request for port " + port);
      // enableUPnP static lease duration mapping
      if (activeGW.addPortMapping(port, port, localAddress.getHostAddress(), "TCP", "ShowclixScanner UPnP")) {
        println("Mapping SUCCESSFUL!");
        connectionWindow.setRetryUPnPButtonEnabled(true);
        connectionWindow.setRetryUPnPButtonText("Close UPnP");
        while (!serverSocket.isClosed() && usingUPnP) {
          Thread.sleep(500);
        }
        connectionWindow.setRetryUPnPButtonEnabled(false);
        if (activeGW.deletePortMapping(port, "TCP")) {
          println("Removed port mapping.");
        } else {
          println("Unable to remove port mapping :(");
        }
        connectionWindow.setRetryUPnPButtonText("Open UPnP");
        connectionWindow.setRetryUPnPButtonEnabled(true);
      } else {
        println("Unable to map port. Connections from outside your LAN may be unavailable :(");
        connectionWindow.setRetryUPnPButtonEnabled(true);
        connectionWindow.setRetryUPnPButtonText("Retry UPnP");
      }
    }
    usingUPnP = false;
  }

  public static void closeUPnP() {
    usingUPnP = false;
  }

  public static boolean usingUPnP() {
    return usingUPnP;
  }

  public static void stopListening() {
    if (serverSocket == null) {
      return;
    }
    try {
      serverSocket.close();
    } catch (Exception e) {
      System.out.println("Unable to stop serverSocket listening!");
      e.printStackTrace();
    }
  }

  public static void registerConnection(ClientConnection conn) {
    connections.add(conn);
    println("Registered connection: " + conn.getAddress());
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
//        killConnection(tempConnection);
        tempConnection.killConnection();
        connectionWindow.removeConnection(tempConnection.getConnectionPanel());
        println("KILLALL: Killed connection " + tempConnection.getAddress());
      }
      connections.clear();
      println("All connections killed.");
    } catch (Exception e) {
      println("Unable to close streams.");
      e.printStackTrace();
    }
  }

  public static void writeString(String str) {
  }

  /**
   * Sets the action to take for all new incoming connections. This should be set by the Connections window. If this is changed and the Connections
   * window JRadioButtons are not updated, the GUI will be inaccurate.
   *
   * @param mode 1 for ask, 2 for accept, 3 for deny
   */
  public static void setIncomingConnectionMode(int mode) {
    if (mode < 1) {
      mode = 1;
    } else if (mode > 3) {
      mode = 3;
    }
    incomingConnectionMode = mode;
  }

  /**
   * Returns the current mode for incoming connections.
   *
   * @return 1 for ask, 2 for accept, 3 for deny
   */
  public static int getIncomingConnectionMode() {
    return incomingConnectionMode;
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
      println("Unable to verify valip IP address: " + ipaddress);
      e.printStackTrace();
      return false;
    }
  }

  public static void println(String msg) {
    if (connectionWindow != null) {
      connectionWindow.println(msg);
    }
    System.out.println(msg);
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
