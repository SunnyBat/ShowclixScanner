/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package showclixscanner;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import showclixscanner.gui.*;

/**
 *
 * @author SunnyBat
 */
public class ShowclixScanner {

  private static String showclixLink;
  public static final String VERSION = "2.1.7.2.1";
  private static URL showclixURL;
  private static Setup setup;
  private static Status status;
  private static int secondsBetweenRefresh = 30;
  private static int connectionErrorCount;
  private static int connectionSuccessCount;
  private static LOGTYPE printLevel = LOGTYPE.MINIMUM;
  public static final int PRIME_SHOWCLIX_ID = 3846764;
  public static final int SOUTH_SHOWCLIX_ID = 3854062;
  public static final int AUS_SHOWCLIX_ID = 3776089;
  private static boolean updateProgram;
  protected static Update update;
  private static java.awt.Image showclixIcon;
  private static List<Throwable> throwableList = new ArrayList();
  private static boolean terminateProgram;
  private static int queueStep;
  private static boolean autoReserveTickets;

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
//    NetworkHandler.writeHttpCookie(new HttpCookie("TESTNAME", "TESTHOST"));
    //Network.doStuff();
//    if (true) {
//      return;
//    }
    //Browser.readShowclixInventory();
    javax.swing.ToolTipManager.sharedInstance().setDismissDelay(600000); // Make Tooltips stay forever
    boolean doUpdate = true;
    boolean doSetup = true;
    if (args.length > 0) {
      System.out.println("Args!");
      for (int a = 0; a < args.length; a++) {
        System.out.println("args[" + a + "] = " + args[a]);
        if (args[a].equals("noupdate")) { // Used by the program when starting the new version just downloaded. Can also be used if you don't want updates
          doUpdate = false;
//        } else if (args[a].startsWith("username=")) {
//
//        } else if (args[a].startsWith("port=")) {

        } else if (args[a].equals("autostart")) {
          // How to autostart??
          doSetup = false;
          if (args.length > a) {
            try {
              Browser.setShowclixLink(Integer.parseInt(args[a + 1]));
              setShowclixURL(Integer.parseInt(args[a + 1]));
            } catch (NumberFormatException numberFormatException) {
              Browser.setShowclixLink(PRIME_SHOWCLIX_ID);
              setShowclixURL(PRIME_SHOWCLIX_ID);
            }
          } else {
            Browser.setShowclixLink(PRIME_SHOWCLIX_ID);
            setShowclixURL(PRIME_SHOWCLIX_ID);
          }
        } else if (args[a].equals("debug")) {
          setPrintLevel(LOGTYPE.ALL);
        }
      }
    }
    Email.init();
    Browser.init();
    loadPatchNotesInBackground();
    prefetchIconsInBackground();
    int showclixID = PRIME_SHOWCLIX_ID;
    Browser.setShowclixLink(showclixID);
    setShowclixURL(showclixID);
    if (doUpdate) {
      try {
        System.out.println("Checking for updates...");
        if (Browser.updateAvailable()) {
          update = new Update();
          while (update.isVisible() && !updateProgram) {
            Thread.sleep(100);
          }
          if (updateProgram) {
            update.setStatusLabelText("Downloading update...");
            Browser.updateProgram();
            update.dispose();
            return;
          }
          update.dispose();
        }
      } catch (Exception e) {
//        ErrorManagement.showErrorWindow("ERROR", "An error has occurred while attempting to update the program. If the problem persists, please manually download the latest version.", e);
//        ErrorManagement.fatalError();
        return;
      }
    }
    if (doSetup) {
      setup = new Setup();
      setup.setVisible(true);
      while (setup.isVisible()) {
        Thread.sleep(250);
      }
    }
    setPrintLevel(LOGTYPE.ALL);
    status = new Status();
    if (showclixIcon == null) {
      println("Note: Icon is null. Default Java icon will be used.", LOGTYPE.NOTES);
    } else {
      status.setIconImage(showclixIcon);
    }
    URLConnection inputConnection;
    InputStream textInputStream;
    BufferedReader myReader = null;
    BufferedWriter myWriter = null;
    int maxValue = 0;
    int noBadgeCount = 0;
    long startTime;
    long bytesRead = 0;
    File outputFile;
    List<Badge> badgeList = new ArrayList();
    Badge currentBadge;
    println("~~~~~~STARTING~~~~~~", LOGTYPE.MINIMUM);
    outputFile = new File(System.getProperty("user.home") + "/Desktop/Showclix.html");
    CookieManager cManager = new CookieManager(); // Open CookieManager so it saves the cookies first given to the browser (for the queue!)
    CookieHandler.setDefault(cManager);
    mainLoop:
    while (programRunning()) {
      startTime = System.currentTimeMillis();
      try {
        myWriter = new BufferedWriter(new FileWriter(outputFile));
        println("Opening connection...", LOGTYPE.MINIMUM);
        inputConnection = showclixURL.openConnection();
        inputConnection.setConnectTimeout(10000);
        inputConnection.setReadTimeout(10000);
        textInputStream = inputConnection.getInputStream();
        myReader = new BufferedReader(new InputStreamReader(textInputStream));
        println("Connection opened. Processing input...", LOGTYPE.MINIMUM);
        String line;
        currentBadge = new Badge();
        while ((line = myReader.readLine()) != null) {
          bytesRead += line.length();
          myWriter.write(line);
          line = line.trim();
          if (line.toLowerCase().contains("queue")) {
            if (queueStep == 0) {
              queueStep = 1;
              println("Keyword 'queue' found: " + line, LOGTYPE.MINIMUM);
            } else {
              println("'queue' keyword found again -- ignoring.", LOGTYPE.MINIMUM);
            }
          }
          if (line.contains("<em>Sold Out</em>")) {
            currentBadge.badgeStatus = BADGE_STATUS.SOLD_OUT;
            println("Badge Status: Sold Out", LOGTYPE.DEBUG);
          } else if (line.contains("<span class=\"product_name\">")) {
            try {
              String chop1 = line.substring(0, line.indexOf("                                             ")).trim();
              String chop2 = chop1.substring(chop1.indexOf("<span class=\"product_name\">") + 27).toLowerCase();
              currentBadge.badgeType = parseType(chop2);
              println("Badge Type = " + currentBadge.badgeType, LOGTYPE.DEBUG);
              if (line.contains("<label for=\"select_level_")) {
                chop1 = line.substring(line.indexOf("<label for=\"select_level_") + 26);
                chop2 = chop1.substring(0, chop1.indexOf("\""));
                currentBadge.backupQuery = "level[" + chop2 + "]=";
                println("backupQuery = " + currentBadge.backupQuery, LOGTYPE.DEBUG);
              }
            } catch (Exception e) {
              println("ERROR parsing value!", LOGTYPE.DEBUG);
              e.printStackTrace();
              addError(e);
            }
          } else if (line.contains("<option value=")) {
            try {
              String chop1 = line.substring(line.indexOf("'"));
              String chop2 = chop1.substring(0, 3);
              String chop3 = chop2.replaceAll("'", "");
              int value = Integer.parseInt(chop3);
              maxValue = Math.max(maxValue, value);
              currentBadge.maxBadges = maxValue;
              if (maxValue != 0) {
                currentBadge.badgeStatus = BADGE_STATUS.AVAILABLE;
              } else {
                currentBadge.badgeStatus = BADGE_STATUS.SOLD_OUT;
              }
              println("maxBadges = " + currentBadge.maxBadges, LOGTYPE.DEBUG);
            } catch (Exception e) {
              println("ERROR parsing value!", LOGTYPE.DEBUG);
              e.printStackTrace();
              addError(e);
            }
          } else if (line.contains("class=\"ticket-select\"")) {
            try {
              String chop1 = line.substring(line.indexOf("name='"));
              String chop2 = chop1.substring(6, chop1.indexOf("class=\"ticket-select\"") - 2);
              String orderPart = chop2 + "=";
              println("badgeQuery = " + orderPart, LOGTYPE.DEBUG);
              currentBadge.badgeQuery = orderPart;
            } catch (Exception e) {
              println("ERROR parsing ticket ID!", LOGTYPE.DEBUG);
              e.printStackTrace();
              addError(e);
            }
          } else if (line.equals("</tr>")) { // New list
            badgeList.add(currentBadge);
            maxValue = 0;
            currentBadge = new Badge();
            println("Badge info saved.", LOGTYPE.DEBUG);
          } else if (line.toLowerCase().contains("enter the words exactly as you see them in the box below, including spaces.")) {
            println("WARNING: potential reCAPTCHA found. Tickets may not be reserved, or tickets may be revoked if purchased. Purchase with care!", LOGTYPE.MINIMUM);
          }
        }
        myWriter.close();
        CookieStore cookieJar = cManager.getCookieStore();
        List<HttpCookie> cookies = cookieJar.getCookies();
        for (HttpCookie cookie : cookies) {
          ShowclixScanner.println("COOKIE: " + cookie.getName() + " :: " + cookie.getValue(), ShowclixScanner.LOGTYPE.DEBUG);
        }
        double dataUsed = (double) ((int) ((double) bytesRead / 1024 / 1024 * 100)) / 100;
        status.setDataUsed(dataUsed, ++connectionSuccessCount, connectionErrorCount);
        println("~~~~~~FINISHED~~~~~~", LOGTYPE.MINIMUM);
        println("Program has used " + (dataUsed) + "MB of data (" + bytesRead + "bytes) over " + connectionSuccessCount + " connections and " + connectionErrorCount + " failed tries.", LOGTYPE.NOTES);
      } catch (Exception e) {
        println("Unable to load the Showclix website for " + ++connectionErrorCount + " tries!", LOGTYPE.MINIMUM);
        double dataUsed = (double) ((int) ((double) bytesRead / 1024 / 1024 * 100)) / 100;
        status.setDataUsed(dataUsed, connectionSuccessCount, connectionErrorCount);
        e.printStackTrace();
        addError(e);
      } finally {
        try {
          println("Closing streams...", LOGTYPE.NOTES);
          if (myReader != null) {
            myReader.close();
          }
          if (myWriter != null) {
            myWriter.close();
          }
        } catch (IOException e) {
          // nothing to see here
        }
      }
      String[] temp = processBadges(badgeList);
      String query = temp[0];
      String badges = temp[1];
      if (badges == null) {
        status.setTicketsFound("NONE");
        noBadgeCount++;
      } /*else if (badgeList.size() != 5) { // 5 for PAX Prime
       if (noBadgeCount > (int) Math.pow(2, 31) - 10000) {
       println("Too many errors... Hopefully you get the point though.", LOGTYPE.MINIMUM);
       } else {
       noBadgeCount += 10000;
       }
       }*/ else {
        status.setTicketsFound(badges);
      }
      if (query != null) {
        if (status != null) {
          status.setRefreshTime("Tickets have been FOUND!" + (Browser.shouldKillFirefox() ? " Reserving tickets..." : ""));
        }
        try {
//          if (Browser.shouldKillFirefox() || NetworkHandler.connectionsAvailable()) { // Check to see whether the program will be able to pass off its reservation, either through NetworkHandler or the Browser -- If not, don't reserve tickets
          if (shouldReserveTickets()) {
            Browser.processQuery(query, cManager);
          }
//          } else {
//            println("Program is NOT reserving tickets -- there is no way to get the tickets from the program.", LOGTYPE.MINIMUM);
//            Browser.openLinkInBrowser(Browser.getShowclixLink());
//          }
        } catch (Exception e) {
          println("ERROR processing query -- your tickets may not have been reserved! Check the system out for details.", LOGTYPE.MINIMUM);
          e.printStackTrace();
          addError(e);
        }
        if (Email.sendMessage("Showclix Website Update!", "PAX Tickets have gone on sale again! " + (shouldReserveTickets() ? "ShowclixScanner has (hopefully) reserved your PAX tickets for you." : "The Showclix website has been opened for you!"))) {
          println("Text successfully sent!", LOGTYPE.NOTES);
        } else {
          println("Unable to send text message. :(", LOGTYPE.MINIMUM);
        }
        println("Final query: " + query, LOGTYPE.MINIMUM);
        try {
          File newOutputFile = new File(System.getProperty("user.home") + "/Desktop/ShowclixBadges.html");
          if (newOutputFile.exists()) {
            newOutputFile.delete();
          }
          if (outputFile.renameTo(newOutputFile)) {
            println("File renamed!", LOGTYPE.MINIMUM);
          } else {
            println("Unable to rename file :(", LOGTYPE.MINIMUM);
          }
        } catch (Exception e) {
          println("Unable to rename file!", LOGTYPE.MINIMUM);
        }
        queueStep = 4;
        if (shouldReserveTickets()) {
          status.setForceKillFirefoxButtonText("Open Firefox Reservation Again");
          status.setForceKillFirefoxButtonVisible(true);
        }
        status.setRefreshTime("Program complete. Please close GUI when finished.");
        break mainLoop;
      } else if (queueStep == 1) {
        queueStep = 2;
        secondsBetweenRefresh = 10;
        println("Refresh time set to 10 seconds to emulate queue delay.");
        println("Opening link to browser...", LOGTYPE.MINIMUM);
        println("PLEASE NOTE: The program is also in the queue. If you have chosen to auto-reserve tickts, it will do so when it finds them.", LOGTYPE.MINIMUM);
        Browser.openLinkInBrowser(showclixID);
        if (Email.sendMessage("Showclix Queue", "Keyword 'queue' has been found on the Showclix webpage! The website has been opened on your computer. PAX Tickets MIGHT be on sale.")) {
          println("Text successfully sent!", LOGTYPE.NOTES);
        } else {
          println("Unable to send text message. :(", LOGTYPE.MINIMUM);
        }
      } else if (queueStep == 2) {
        queueStep = 3;
        secondsBetweenRefresh = 30;
        println("Refresh time set to 30 seconds to emulate queue delay.");
      }
      println("No badges found for " + noBadgeCount + " times", LOGTYPE.MINIMUM);
      while (System.currentTimeMillis() - startTime < secondsBetweenRefresh * 1000 && programRunning()) {
        status.setRefreshTime(secondsBetweenRefresh - (int) (System.currentTimeMillis() - startTime) / 1000);
        Thread.sleep(250);
      }
      status.clearConsole();
      badgeList.clear();
      if (connectionSuccessCount % 250 == 0 && queueStep == 0) {
        cManager = new CookieManager(); // Reset CookieManager to get new cookies from the PAX website. This hopefully helps prevent a reCAPTCHA from appearing, which will destroy the program's auto-reserving tickets function.
        CookieHandler.setDefault(cManager);
        println("Program cookies have been reset.", LOGTYPE.NOTES); // This hopefully prevents Showclix from tagging us as a bot... Maybe =/
      }
    }
    println("Main loop broken.", LOGTYPE.ALL);
  }

  public static void terminateProgram() {
    println("terminateProgram() called! Terminating program...", LOGTYPE.MINIMUM);
    NetworkHandler.closeConnectionWindow(); // Kills connections, stops listening for connections
    terminateProgram = true;
    killProgramAfter(30); // Kills program after X seconds if it's still running
  }

  private static String[] processBadges(List<Badge> badgeList) {
    String query = null;
    String badges = null;
    Iterator<Badge> myIt = badgeList.listIterator();
    while (myIt.hasNext()) {
      Badge currBadge = myIt.next();
      if (currBadge == null) {
        println("currBadge = null", LOGTYPE.VERBOSE);
        continue;
      }
      try {
        println("Badge (" + currBadge.badgeType.toString() + "):", LOGTYPE.DEBUG);
      } catch (Exception e) {
      }
      try {
        println("Availability = " + currBadge.badgeStatus.toString(), LOGTYPE.DEBUG);
      } catch (Exception e) {
      }
      try {
        println("Query = " + currBadge.badgeQuery, LOGTYPE.DEBUG);
      } catch (Exception e) {
      }
      try {
        println("backupQuery = " + currBadge.backupQuery, LOGTYPE.DEBUG);
      } catch (Exception e) {
      }
      try {
        println("Amount = " + currBadge.maxBadges, LOGTYPE.DEBUG);
      } catch (Exception e) {
      }
      try {
        if (currBadge.badgeQuery != null || currBadge.badgeStatus != BADGE_STATUS.SOLD_OUT || currBadge.maxBadges > 0) {
          if (currBadge.maxBadges <= 0 || currBadge.maxBadges > 4) {
            currBadge.maxBadges = 2;
          }
          if (currBadge.badgeQuery == null) {
            currBadge.badgeQuery = currBadge.backupQuery;
            println("Backup query init.", LOGTYPE.ALL);
          }
          if (query == null) {
            query = currBadge.badgeQuery + currBadge.maxBadges; // Assigning null is fine. It was already null
          } else if (currBadge.badgeQuery != null) { // NOW we don't want to assign null
            query += "&" + currBadge.badgeQuery + currBadge.maxBadges;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        addError(e);
      }
      if (currBadge.badgeType != null) {
        if (badges == null) {
          badges = currBadge.badgeType.toString();
        } else {
          badges += "; " + currBadge.badgeType.toString();
        }
      } else {
        println("Badge = null", LOGTYPE.DEBUG);
      }
    }
    return new String[]{query, badges};
  }

  /**
   * Checks whether or not the program has found tickets.
   *
   * @return True if tickets have been found, false if not
   */
  public static boolean ticketsFound() {
    return queueStep == 4;
  }

  public static void setReserveTickets(boolean reserve) {
    autoReserveTickets = reserve;
  }

  public static boolean shouldReserveTickets() {
    return autoReserveTickets;
  }

  public static void setStatusNetworkButton(boolean enabled) {
    status.setNetworkConnectionButtonState(enabled);
  }

  public static void setForceKillFirefoxButtonVisible(boolean visible) {
    status.setForceKillFirefoxButtonVisible(visible);
  }

  public static boolean programRunning() {
    return !terminateProgram;
  }

  public static void addError(Throwable t) {
    throwableList.add(t);
  }

  public static List<Throwable> getThrowableList() {
    return throwableList;
  }

  public static void setRefreshTime(int seconds) {
    secondsBetweenRefresh = seconds;
  }

  public static int getFailedAttemptCount() {
    return connectionErrorCount;
  }

  public static void setPrintLevel(LOGTYPE level) {
    printLevel = level;
  }

  public static void println(String msg) {
    println(msg, LOGTYPE.NOTES);
  }

  public static void println(String msg, LOGTYPE type) {
    if (status != null && LOGTYPE.shouldPrint(type)) {
      status.println(msg);
    }
    System.out.println(msg);
  }

  public static enum LOGTYPE {

    MINIMUM, NOTES, VERBOSE, DEBUG, ALL;

    public static boolean shouldPrint(LOGTYPE type) {
      //LOGTYPE setting = status.getConsoleOutputLevel().toUpperCase();
      switch (type) {
        case MINIMUM:
          return true;
        case NOTES:
          if (printLevel == NOTES || printLevel == VERBOSE || printLevel == DEBUG || printLevel == ALL) {
            return true;
          }
          break;
        case VERBOSE:
          if (printLevel == VERBOSE || printLevel == DEBUG || printLevel == ALL) {
            return true;
          }
          break;
        case DEBUG:
          if (printLevel == DEBUG || printLevel == ALL) {
            return true;
          }
          break;
        case ALL:
          if (printLevel == ALL) {
            return true;
          }
          break;
      }
      return false;
    }
  }

  public static BADGE_STATUS parseAmount(int amount) {
    if (amount == 1) {
      return BADGE_STATUS.LOW;
    } else if (amount > 1) {
      return BADGE_STATUS.AVAILABLE;
    } else {
      return BADGE_STATUS.SOLD_OUT;
    }
  }

  public static BADGE_TYPES parseType(String parse) {
    if (parse.contains("friday")) {
      return BADGE_TYPES.FRIDAY;
    } else if (parse.contains("saturday")) {
      return BADGE_TYPES.SATURDAY;
    } else if (parse.contains("sunday")) {
      return BADGE_TYPES.SUNDAY;
    } else if (parse.contains("monday")) {
      return BADGE_TYPES.MONDAY;
    } else if (parse.contains("4 day")) {
      return BADGE_TYPES.FOURDAY;
    } else {
      return BADGE_TYPES.NONE;
    }
  }

  private static class Badge {

    public BADGE_TYPES badgeType;
    public BADGE_STATUS badgeStatus;
    public int maxBadges;
    public String badgeQuery;
    public String backupQuery;

    public Badge() {
    }
  }

  public static enum BADGE_STATUS {

    AVAILABLE, LOW, SOLD_OUT;
  }

  public static enum BADGE_TYPES {

    NONE, FRIDAY, SATURDAY, SUNDAY, MONDAY, FOURDAY;
  }

  public static void setShowclixURL(int showclixID) {
    setShowclixURL("https://www.showclix.com/event/" + showclixID);
  }

  public static void setShowclixURL(String URL) {
    showclixLink = URL;
    try {
      showclixURL = new URL(showclixLink);
    } catch (Exception e) {
      println("ERROR creating new Showclix URL with link " + showclixLink, LOGTYPE.MINIMUM);
      e.printStackTrace();
    }
  }

  /**
   * Set the updateProgram flag to true. This will start the program updating process. This should only be called by the Update GUI when the main()
   * method is waiting for the prompt.
   */
  public static void startUpdatingProgram() {
    updateProgram = true;
  }

  public static void startNewThread(Runnable run, String name) {
    Thread newThread = new Thread(run);
    newThread.setName(name);
    newThread.setDaemon(false);
    newThread.setPriority(Thread.NORM_PRIORITY); // Default, but just inc ase
    newThread.start(); // Start the Thread
  }

  /**
   * This makes a new daemon, low-priority Thread and runs it.
   *
   * @param run The Runnable to make into a Thread and run
   */
  public static void startBackgroundThread(Runnable run) {
    startBackgroundThread(run, "General Background Thread");
  }

  public static void startBackgroundThread(Runnable run, String name) {
    Thread newThread = new Thread(run);
    newThread.setName(name);
    newThread.setDaemon(true); // Kill the JVM if only daemon threads are running
    newThread.setPriority(Thread.MIN_PRIORITY); // Let other Threads take priority, as this will probably not run for long
    newThread.start(); // Start the Thread
  }

  public static void loadPatchNotesInBackground() {
    startBackgroundThread(new Runnable() {
      @Override
      public void run() {
        Browser.loadVersionNotes();
      }
    }, "Load Patch Notes");
  }

  public static void startNewProgramInstance() {
    try {
      String path = ShowclixScanner.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + "\\bin\\javaw.exe", "-jar", new File(path).getAbsolutePath()); // path can have leading / on it, getAbsolutePath() removes them
      Process p = pb.start();
    } catch (Exception e) {
      //ErrorManagement.showErrorWindow("Small Error", "Unable to automatically run update.", null);
    }
  }

  public static void startNetworkConnection() {
    if (NetworkHandler.connectionsAvailable()) {
      return;
    }
    if (status != null) {
      status.setNetworkConnectionButtonState(false);
    }
    startBackgroundThread(new Runnable() {
      @Override
      public void run() {
        NetworkHandler.listenForConnections();
      }
    }, "Network Connection Thread");
  }

  public static void prefetchIconsInBackground() {
    startBackgroundThread(new Runnable() {
      @Override
      public void run() {
        try {
          showclixIcon = javax.imageio.ImageIO.read(ShowclixScanner.class.getResourceAsStream("/resources/Showclix.png"));
        } catch (Exception e) {
          System.out.println("Unable to fetch Showclix Icon!");
        }
      }
    }, "Icon Prefetch Thread");
  }

  public static void killProgramAfter(final int seconds) {
    startBackgroundThread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(seconds * 1000);
          System.exit(1); // This should really only run if there was a problem with the program closing, as it should close within the allotted amount of time
        } catch (Exception e) {
          println("Unable to kill program!");
        }
      }
    }, "Icon Prefetch Thread");
  }
}
