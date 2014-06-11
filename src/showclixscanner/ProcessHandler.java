package showclixscanner;

import java.io.*;

/**
 *
 * @author SunnyBat
 */
public class ProcessHandler {

  private static final String TASKLIST = "tasklist";
  private static final String KILL = "taskkill /IM ";

  public static boolean isProcessRunging(String serviceName) throws Exception {
    Process p = Runtime.getRuntime().exec(TASKLIST);
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.toLowerCase();
      System.out.println(line);
      if (line.contains(serviceName)) {
        System.out.println(serviceName + " found -- killing process.");
        return true;
      }
    }
    return false;

  }

  public static void killProcess(String serviceName) throws Exception {
    Runtime.getRuntime().exec(KILL + serviceName);
  }

  public static void killFirefox() throws Exception {
    String processName = "firefox.exe";
    if (isProcessRunging(processName)) {
      killProcess(processName);
    }
  }
}
