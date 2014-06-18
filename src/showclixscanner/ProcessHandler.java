package showclixscanner;

import java.io.*;

/**
 *
 * @author SunnyBat
 */
public class ProcessHandler {

  private static final String TASKLIST = "tasklist";
  private static final String KILL = "taskkill /T /IM ";

  public static boolean isProcessRunging(String serviceName) {
    try {
      Process p = Runtime.getRuntime().exec(TASKLIST);
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.toLowerCase();
        if (line.contains(serviceName)) {
          System.out.println(serviceName + " found -- killing process.");
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
      System.out.println("Unable to kill process " + serviceName);
      e.printStackTrace();
    }
  }

  public static void killFirefox() {
    String processName = "firefox.exe";
    if (isProcessRunging(processName)) {
      killProcess(processName);
    }
  }
}
