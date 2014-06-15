package showclixscanner;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

/**
 *
 * @author SunnyBat
 */
public class Browser {

  private static boolean killFirefox;
  private static String showclixLink;
  private static long updateSize;
  private static String versionNotes;
  private static final String updateLink = "https://dl.dropboxusercontent.com/u/16152108/ShowclixScanner.jar";
  private static final String patchNotesLink = "https://dl.dropboxusercontent.com/u/16152108/ShowclixScannerUpdates.txt";
  private static URL updateURL;
  private static URL patchNotesURL;

  protected static void init() {
    try {
      updateURL = new URL(updateLink);
      patchNotesURL = new URL(patchNotesLink);
    } catch (MalformedURLException malformedURLException) {
    }
  }

  public static void setKillFirefox(boolean kill) {
    killFirefox = kill;
  }

  public static boolean shouldKillFirefox() {
    return killFirefox;
  }

//  public static void doIt() throws Exception {
////    curl --data "email=api@example.com&password=opensesame" -X POST https://admin.showclix.com/api/registration
////
////{"token":"<token>","user_id":"<user>","seller_id":"<seller>","name":{"first":"<user_first>","last":"<user_last>"},"org":"<org>","avatar":"","locale":"en_US"}
//    
//    URL myurl = new URL("https://developer.showclix.com/registration");
//    HttpsURLConnection con;
//    String query = "email=Sunnybat@yahoo.com&password=password";
//    
//    con = (HttpsURLConnection) myurl.openConnection();
//    con.setRequestMethod("POST"); // Tell website that we want to buy tickets with the specified query, added tons of extra headers to ensure connection
//    con.setRequestProperty("Content-length", String.valueOf(query.length()));
//    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Fairly certain this is the only thing we need, but whatever
//    con.setRequestProperty("User-Agent", "Mozilla/5.0");
//    con.setDoOutput(true);
//    con.setDoInput(true);
//    try (DataOutputStream output = new DataOutputStream(con.getOutputStream())) { // Actually open website connection
//      output.writeBytes(query); // Send POST information (above) to website
//      ShowclixScanner.println("Resp Code:" + con.getResponseCode());
//      ShowclixScanner.println("Resp Message:" + con.getResponseMessage());
//      ShowclixScanner.println("URL=" + con.getURL().toString());
//    } catch (Exception e) {
//      ShowclixScanner.println("ERROR connecting to OutputStream!");
//      e.printStackTrace();
//    }
//    if (con.getResponseCode() >= 400) {
//      ShowclixScanner.println("ERROR detected while receiving server response!");
//    }
//    try {
//      BufferedReader reader = new BufferedReader(new InputStreamReader(con.getResponseCode() >= 400 ? con.getErrorStream() : con.getInputStream()));
//      ShowclixScanner.println("URL = " + con.getURL().toString());
//      String line;
//      while ((line = reader.readLine()) != null) {
//        ShowclixScanner.println(line);
//      }
//    } catch (Exception e) {
//      ShowclixScanner.println("Unable to read :(");
//    }
//  }
  public static void setShowclixLink(String link) {
    showclixLink = link;
  }

  public static void setShowclixLink(int ID) {
    setShowclixLink("https://www.showclix.com/event/" + ID);
  }

  public static void processQuery(String query) throws Exception {
    String httpsURL = showclixLink;
    URL myurl = new URL(httpsURL);
    HttpsURLConnection con;
    //query = "level[3976804]=0&level[3976806]=1&level[3976811]=0"; // Test query for if you want to make sure this method works (which it does!)

    CookieManager cManager = new CookieManager(); // Open CookieManager so it starts capturing cookies
    CookieHandler.setDefault(cManager);

    /* Start by connecting to website so CookieManager can grab new cookies */
    con = (HttpsURLConnection) myurl.openConnection();
    con.setRequestMethod("POST"); // Tell website that we want to buy tickets with the specified query, added tons of extra headers to ensure connection
    con.setRequestProperty("Content-length", String.valueOf(query.length()));
    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Fairly certain this is the only thing we need, but whatever
    con.setRequestProperty("User-Agent", "Mozilla/5.0");
    con.setRequestProperty("Referer", httpsURL);
    con.setRequestProperty("Connection", "keep-alive");
    con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    con.setRequestProperty("Accept-Encoding", "gzip, deflate");
    con.setRequestProperty("DNT", "1");
    con.setRequestProperty("Host", "www.showclix.com");
    con.setDoOutput(true);
    con.setDoInput(true);

    /* Write POST query to page to reserve tickets (and update cookies if necessary, but that really shouldn't happen) */
    try (DataOutputStream output = new DataOutputStream(con.getOutputStream())) { // Actually open website connection
      output.writeBytes(query); // Send POST information (above) to website
      ShowclixScanner.println("Resp Code:" + con.getResponseCode(), ShowclixScanner.LOGTYPE.DEBUG);
      ShowclixScanner.println("Resp Message:" + con.getResponseMessage(), ShowclixScanner.LOGTYPE.DEBUG);
      ShowclixScanner.println("URL=" + con.getURL().toString());
      if (con.getResponseCode() >= 400) { // Error page returned... No tickets reserved :(
        ShowclixScanner.println("ERROR detected while receiving server response!", ShowclixScanner.LOGTYPE.MINIMUM);
        openLinkInBrowser(showclixLink);
        return;
      }
    } catch (Exception e) {
      ShowclixScanner.println("ERROR connecting to OutputStream!");
      e.printStackTrace();
      openLinkInBrowser(showclixLink);
      return;
    }

    /* Open new page in Firefox */
    CookieStore cookieJar = cManager.getCookieStore();
    List<HttpCookie> cookies = cookieJar.getCookies();
    ShowclixScanner.println("````COOKIES````");
    for (HttpCookie cookie : cookies) {
      ShowclixScanner.println(cookie.getName() + " : " + cookie.getValue(), ShowclixScanner.LOGTYPE.DEBUG);
      Network.writeHttpCookie(cookie);
    }
    Network.endCookies();
    if (shouldKillFirefox()) {
      ProcessHandler.killFirefox();
      while (!DatabaseManager.isDatabaseAvailable()) { // Wait for Firefox to save changes to the cookie database
        Thread.sleep(100);
      }
      ShowclixScanner.println("Database deemed available.", ShowclixScanner.LOGTYPE.NOTES);
      DatabaseManager.writeCookies(cookies);
    }
    openLinkInBrowser(showclixLink);
  }

  public static void openLinkInBrowser(int showclixID) {
    openLinkInBrowser("https://www.showclix.com/event/" + showclixID);
  }

  public static void openLinkInBrowser(String link) {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      try {
        desktop.browse(new URI(link));
      } catch (Exception e) {
        ShowclixScanner.println("Unable to open link in browser window!");
      }
    } else {
      ShowclixScanner.println("Unable to open link in default browser.");
    }
  }

  public static void openLinkInBrowser(URI uri) throws Exception {
    Desktop.getDesktop().browse(uri);
  }

  public static int readShowclixInventory() {
    try {
      URL url = new URL("https://api.showclix.com/Event/3846764/inventory");
      HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
      httpCon.addRequestProperty("User-Agent", "Mozilla/4.0");
      ShowclixScanner.println("Response: " + httpCon.getResponseCode() + " -- " + httpCon.getResponseMessage());
      String jsonText = "";
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpCon.getInputStream()))) {
        String line = null;
        while ((line = reader.readLine()) != null) {
          jsonText += line;
        }
      }
      JSONParser mP = new JSONParser();
      JSONObject obj = (JSONObject) mP.parse(jsonText);

      int maxId = 0;
      for (String s : (Iterable<String>) obj.keySet()) {
        maxId = Math.max(maxId, Integer.parseInt((String) s)); // Not sure if this will work... Not sure how the json return text is, nor how the JSONParser will return values
        ShowclixScanner.println("Inventory: " + s);
      }
      return maxId;
    } catch (Exception e) {
//      ErrorManagement.showErrorWindow("ERORR checking the Showclix website for updates!", e);
      e.printStackTrace();
      return -1;
    }
  }

  public static String getVersionNotes(String version) { // TODO: Utilize getVersionNotes() instead of copying code and adding 3 lines
    String versNotes = getVersionNotes();
    if (versNotes == null) {
      return null;
    }
    try {
      versNotes = versNotes.substring(0, versNotes.indexOf("~~~" + version)).trim();
    } catch (Exception e) {
    }
    return versNotes;
  }

  public static String getVersionNotes() {
    return versionNotes;
  }

  public static void loadVersionNotes() {
    URLConnection inputConnection;
    InputStream textInputStream;
    BufferedReader myReader = null;
    try {
      inputConnection = patchNotesURL.openConnection();
      textInputStream = inputConnection.getInputStream();
      myReader = new BufferedReader(new InputStreamReader(textInputStream));
      String line;
      String lineSeparator = System.getProperty("line.separator", "\n");
      String allText = "Patch Notes:" + lineSeparator;
      while ((line = myReader.readLine()) != null) {
        line = line.trim();
        allText += line + lineSeparator;
      }
      versionNotes = allText.trim();
    } catch (Exception e) {
      System.out.println("Unable to load version notes!");
    } finally {
      try {
        if (myReader != null) {
          myReader.close();
        }
      } catch (IOException e) {
        // nothing to see here
      }
    }
  }

  public static long getUpdateSize() {
    return updateSize;
  }

  /**
   * Checks whether or not an update to the program is available. Note that this compares the file
   * sizes between the current file and the file on the Dropbox server. This means that if ANY
   * modification is made to the JAR file, it's likely to trigger an update.
   * This THEORETICALLY works well. We'll find out whether or not it will actually work in
   * practice.
   *
   * @return True if an update is available, false if not.
   */
  public static boolean updateAvailable() {
    try {
      File mF = new File(ShowclixScanner.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
      long fileSize = mF.length();
      if (fileSize == 4096) { // No, I do NOT want to update when I'm running in Netbeans
        return false;
      }
      URLConnection conn = updateURL.openConnection();
      updateSize = conn.getContentLengthLong();
      System.out.println("Updatesize = " + updateSize + " -- Filesize = " + fileSize);
      if (updateSize == -1) {
        //ErrorManagement.showErrorWindow("ERROR checking for updates!", "PAX Checker was unable to check for updates.", null);
        return false;
      } else if (updateSize != fileSize) {
        System.out.println("Update available!");
        return true;
      }
    } catch (Exception e) {
      System.out.println("ERROR updating program!");
      //ErrorManagement.showErrorWindow("ERROR updating program!", "The program was unable to check for new updates.", e);
    }
    return false;
  }

  /**
   * Downloads the latest JAR file from the Dropbox server. Note that this automatically closes
   * the
   * program once finished. Also note that once this is run, the program WILL eventually close,
   * either through finishing the update or failing to properly update.
   */
  public static void updateProgram() {
    try {
      URLConnection conn = updateURL.openConnection();
      InputStream inputStream = conn.getInputStream();
      long remoteFileSize = conn.getContentLength();
      System.out.println("Downloding file...\nUpdate Size(compressed): " + remoteFileSize + " Bytes");
      String path = ShowclixScanner.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      BufferedOutputStream buffOutputStream = new BufferedOutputStream(new FileOutputStream(new File(path.substring(0, path.lastIndexOf(".jar")) + ".temp.jar")));
      byte[] buffer = new byte[32 * 1024];
      int bytesRead = 0;
      int in = 0;
      int prevPercent = 0;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        in += bytesRead;
        buffOutputStream.write(buffer, 0, bytesRead);
        if (ShowclixScanner.update != null) {
          if ((int) (((in * 100) / remoteFileSize)) != prevPercent) {
            prevPercent = (int) (((in * 100) / remoteFileSize));
            ShowclixScanner.update.updateProgress(prevPercent);
          }
        }
      }
      buffOutputStream.flush();
      buffOutputStream.close();
      inputStream.close();
      if (ShowclixScanner.update != null) {
        ShowclixScanner.update.setStatusLabelText("Finishing up...");
      }
      try { // Code to make a copy of the current JAR file
        File inputFile = new File(path.substring(0, path.lastIndexOf(".jar")) + ".temp.jar");
        InputStream fIn = new BufferedInputStream(new FileInputStream(inputFile));
        File outputFile = new File(path);
        buffOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        buffer = new byte[32 * 1024];
        bytesRead = 0;
        in = 0;
        while ((bytesRead = fIn.read(buffer)) != -1) {
          in += bytesRead;
          buffOutputStream.write(buffer, 0, bytesRead);
        }
        buffOutputStream.flush();
        buffOutputStream.close();
        fIn.close();
        inputFile.delete();
      } catch (Exception e) {
//        ErrorManagement.showErrorWindow("ERROR updating", "Unable to complete update -- unable to copy temp JAR file to current JAR file.", e);
//        ErrorManagement.fatalError();
      }
      System.out.println("Download Complete!");
      ShowclixScanner.startNewProgramInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR updating program!");
//      ErrorManagement.showErrorWindow("ERROR updating the program", "The program was unable to successfully download the update. If the problem continues, please manually download the latest version at " + updateURL.getPath(), e);
//      ErrorManagement.fatalError();
    }
  }
}
