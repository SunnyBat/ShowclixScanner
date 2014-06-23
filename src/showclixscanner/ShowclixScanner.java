/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package showclixscanner;

import java.io.*;
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
  public static final String VERSION = "2.0.0.7.1";
  private static URL showclixURL;
  private static Setup setup;
  private static Status status;
  private static int secondsBetweenRefresh = 20;
  private static int connectionErrorCount;
  private static int connectionSuccessCount;
  private static LOGTYPE printLevel = LOGTYPE.MINIMUM;
  public static final int AUS_SHOWCLIX_ID = 3776089;
  public static final int PRIME_SHOWCLIX_ID = 3846764;
  private static boolean updateProgram;
  protected static Update update;
  private static java.awt.Image showclixIcon;
  private static List<Throwable> throwableList = new ArrayList();
  private static boolean terminateProgram;

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
    if (args.length > 0) {
      System.out.println("Args!");
      for (int a = 0; a < args.length; a++) {
        System.out.println("args[" + a + "] = " + args[a]);
        if (args[a].equals("noupdate")) { // Used by the program when starting the new version just downloaded. Can also be used if you don't want updates
          doUpdate = false;
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
    setup = new Setup();
    setup.setVisible(true);
    while (setup.isVisible()) {
      Thread.sleep(250);
    }
    status = new Status();
    if (showclixIcon == null) {
      System.out.println("ERROR: ICON IS NULL");
    } else {
      status.setIconImage(showclixIcon);
    }
    URLConnection inputConnection;
    InputStream textInputStream;
    BufferedReader myReader = null;
    BufferedWriter myWriter = null;
    int messages = 0;
    int maxValue = 0;
    long startTime;
    int noBadgeCount = 0;
    File outputFile;
    List<Badge> badgeList = new ArrayList();
    Badge currentBadge;
    println("~~~~~~STARTING~~~~~~", LOGTYPE.MINIMUM);
    outputFile = new File(System.getProperty("user.home") + "/Desktop/Showclix.html");
    int bytesRead = 0;
    mainLoop:
    while (programRunning()) {
      startTime = System.currentTimeMillis();
      try {
        myWriter = new BufferedWriter(new FileWriter(outputFile));
        println("Opening connection...", LOGTYPE.MINIMUM);
        inputConnection = showclixURL.openConnection();
        textInputStream = inputConnection.getInputStream();
        myReader = new BufferedReader(new InputStreamReader(textInputStream));
        println("Connection opened. Processing input...", LOGTYPE.MINIMUM);
        String line;
        currentBadge = new Badge();
        while ((line = myReader.readLine()) != null) {
          bytesRead += line.length();
          myWriter.write(line);
          line = line.trim();
          if (line.contains("<em>Sold Out</em>")) {
            currentBadge.badgeStatus = BADGE_STATUS.SOLD_OUT;
          } else if (line.contains("<span class=\"product_name\">")) {
            try {
              String chop1 = line.substring(0, line.indexOf("                                             ")).trim();
              String chop2 = chop1.substring(chop1.indexOf("<span class=\"product_name\">") + 27).toLowerCase();
              currentBadge.badgeType = parseType(chop2);
              if (line.contains("<label for=\"select_level_")) {
                chop1 = line.substring(line.indexOf("<label for=\"select_level_") + 26);
                chop2 = chop1.substring(0, chop1.indexOf("\""));
                currentBadge.backupQuery = "level[" + chop2 + "]=";
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
              println("orderPart = " + orderPart, LOGTYPE.DEBUG);
              currentBadge.badgeQuery = orderPart;
            } catch (Exception e) {
              println("ERROR parsing ticket ID!", LOGTYPE.DEBUG);
              e.printStackTrace();
              addError(e);
            }
          } else if (line.equals("</tr>")) { // New list
            if (currentBadge != null) {
              badgeList.add(currentBadge);
            }
            maxValue = 0;
            currentBadge = new Badge();
          }
        }
        myWriter.close();
        double dataUsed = (double) ((int) ((double) bytesRead / 1024 / 1024 * 100)) / 100;
        status.setDataUsed(dataUsed, ++connectionSuccessCount, connectionErrorCount);
        println("~~~~~~FINISHED~~~~~~", LOGTYPE.MINIMUM);
        println("Program has used " + (dataUsed) + "MB of data (" + bytesRead + "bytes) over " + connectionSuccessCount + " connections and " + connectionErrorCount + " failed tries.", LOGTYPE.MINIMUM);
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
      String query = null;
      String badges = null;
      Iterator<Badge> myIt = badgeList.listIterator();
      while (myIt.hasNext()) {
        Badge currBadge = myIt.next();
        if (currBadge == null) {
          println("currBadge = null", LOGTYPE.VERBOSE);
          break;
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
      if (badges == null) {
        status.setTicketsFound("NONE");
        noBadgeCount++;
      } else if (badgeList.size() != 5) {
        if (noBadgeCount > (int) Math.pow(2, 31) - 10000) {
          println("Too many errors... Hopefully you get the point though.", LOGTYPE.MINIMUM);
        } else {
          noBadgeCount += 10000;
        }
      } else {
        status.setTicketsFound(badges);
      }
      println("No badges found for " + noBadgeCount + " times", LOGTYPE.MINIMUM);
      if (query != null) {
        if (status != null) {
          status.setRefreshTime("Tickets have been FOUND!" + (Browser.shouldKillFirefox() ? " Reserving tickets..." : ""));
        }
        try {
          File newOutputFile = new File(System.getProperty("user.home") + "/Desktop/Showclix" + messages + ".html");
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
        println("Main loop broken.", LOGTYPE.ALL);
        Browser.processQuery(query);
        if (Email.sendMessage("Showclix Website Update!", "PAX Tickets have gone on sale again! ShowclixScanner has (hopefully) reserved your PAX tickets for you.")) {
          println("Text successfully sent!", LOGTYPE.NOTES);
        } else {
          println("Unable to send text message. :(", LOGTYPE.MINIMUM);
        }
        println("Final query: " + query, LOGTYPE.MINIMUM);
        status.setRefreshTime("Program complete. Please close GUI when finished.");
        break mainLoop;
      }
      while (System.currentTimeMillis() - startTime < secondsBetweenRefresh * 1000 && programRunning()) {
        status.setRefreshTime(secondsBetweenRefresh - (int) (System.currentTimeMillis() - startTime) / 1000);
        Thread.sleep(250);
      }
      status.clearConsole();
      badgeList.clear();
    }
    System.out.println("Program ended.");
  }

  public static void terminateProgram() {
    println("terminateProgram() called! Terminating program...", LOGTYPE.MINIMUM);
    NetworkHandler.stopListening();
    terminateProgram = true;
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
   * Set the updateProgram flag to true. This will start the program updating process. This should
   * only be called by the Update GUI when the main() method is waiting for the prompt.
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
}
