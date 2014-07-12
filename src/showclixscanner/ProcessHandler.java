package showclixscanner;

import java.io.*;

/**
 *
 * @author SunnyBat
 */
public class ProcessHandler {

  private static final String TASKLIST = "tasklist";
  private static final String KILL = "taskkill /T /IM ";
  private static final String FORCEKILL = "taskkill /T /F /IM ";

  public static boolean isProcessRunging(String serviceName) {
    try {
      Process p = Runtime.getRuntime().exec(TASKLIST);
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.toLowerCase();
        if (line.contains(serviceName)) {
          ShowclixScanner.println(serviceName + " found.", ShowclixScanner.LOGTYPE.VERBOSE);
          return true;
        }
      }
    } catch (IOException e) {
      System.out.println("Error reading processes!");
      e.printStackTrace();
    }
    return false;

  }

  public static void killProcess(String serviceName) {
    try {
      Runtime.getRuntime().exec(KILL + serviceName);
    } catch (IOException e) {
      ShowclixScanner.println("Unable to kill process " + serviceName, ShowclixScanner.LOGTYPE.MINIMUM);
      e.printStackTrace();
    }
  }

  public static void forceKillProcess(String serviceName) {
    try {
      Runtime.getRuntime().exec(FORCEKILL + serviceName);
    } catch (IOException e) {
      ShowclixScanner.println("Unable to kill process " + serviceName, ShowclixScanner.LOGTYPE.MINIMUM);
      e.printStackTrace();
    }
  }

  public static void killFirefox() {
    String processName = "firefox.exe";
    if (isProcessRunging(processName)) {
      ShowclixScanner.println("Send close request to Firefox...", ShowclixScanner.LOGTYPE.DEBUG);
      killProcess(processName);
    } else {
      ShowclixScanner.println("Unable to find firefox.exe -- Please close Firefox manually if it is running (check Task Manager if necesssary).", ShowclixScanner.LOGTYPE.MINIMUM);
    }
  }

  public static void forceKillFirefox() {
    String processName = "firefox.exe";
    if (isProcessRunging(processName)) {
      ShowclixScanner.println("WARNING: Force killing Firefox...", ShowclixScanner.LOGTYPE.MINIMUM);
      forceKillProcess(processName);
    } else {
      ShowclixScanner.println("Unable to find firefox.exe -- Please close Firefox manually if it is running (check Task Manager if necesssary).", ShowclixScanner.LOGTYPE.MINIMUM);
    }
  }
}
