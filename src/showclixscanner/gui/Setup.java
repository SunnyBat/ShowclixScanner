/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package showclixscanner.gui;

import java.io.File;
import javax.swing.JFileChooser;
import showclixscanner.Browser;
import showclixscanner.DatabaseManager;
import showclixscanner.Email;
import showclixscanner.ShowclixScanner;

/**
 *
 * @author SunnyBat
 */
public class Setup extends javax.swing.JFrame {

  private JFileChooser chooser = new JFileChooser();
  private boolean openedCookiesPrompt;

  /**
   * Creates new form Setup
   */
  public Setup() {
    initComponents();
    customComponents();
  }

  private void customComponents() {
    //setCookieDirText(DatabaseManager.getDatabaseDirectory());
    jPanel1.setVisible(false);
    pack();
  }

  public void openSelectCookiePrompt() {
    jButton2ActionPerformed(null);
  }

  public void setCookieDirText(String directory) {
//    String newStr = directory.substring(directory.length() / 5, directory.length());
//    jLabel6.setText("..." + newStr.substring(newStr.indexOf("\\", 0), newStr.length()));
    if (directory.length() > 60) {
      if (directory.contains("\\")) {
        String newStr = directory.substring(directory.length() - 60);
        jLabel6.setText("..." + newStr.substring(newStr.indexOf("\\")));
      } else {
        jLabel6.setText("..." + directory.substring(directory.length() - 60));
      }
    } else {
      jLabel6.setText(directory);
    }
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    JTFUsername = new javax.swing.JTextField();
    JPFPassword = new javax.swing.JPasswordField();
    JTFCellnum = new javax.swing.JTextField();
    jButton1 = new javax.swing.JButton();
    JCBCarrier = new javax.swing.JComboBox();
    jLabel5 = new javax.swing.JLabel();
    jSlider1 = new javax.swing.JSlider();
    JCBAutoReserveTickets = new javax.swing.JCheckBox();
    jPanel1 = new javax.swing.JPanel();
    jLabel6 = new javax.swing.JLabel();
    jButton2 = new javax.swing.JButton();
    JCBEvent = new javax.swing.JComboBox();
    JCBListenOnPort = new javax.swing.JCheckBox();
    jCheckBox1 = new javax.swing.JCheckBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("ShowclixScanner Setup");
    setResizable(false);

    jLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Showclix Scanner Setup");

    jLabel2.setText("Username:");

    jLabel3.setText("Password:");

    jLabel4.setText("Cell Num:");

    JTFUsername.setNextFocusableComponent(JPFPassword);

    JPFPassword.setNextFocusableComponent(JTFCellnum);

    JTFCellnum.setNextFocusableComponent(JCBEvent);

    jButton1.setText("START!");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    JCBCarrier.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AT&T", "Sprint", "Verizon", "T-Mobile", "U.S. Cellular" }));

    jLabel5.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel5.setText("Time Between Refresh");

    jSlider1.setMajorTickSpacing(10);
    jSlider1.setMaximum(60);
    jSlider1.setMinorTickSpacing(5);
    jSlider1.setPaintLabels(true);
    jSlider1.setPaintTicks(true);
    jSlider1.setSnapToTicks(true);
    jSlider1.setValue(30);

    JCBAutoReserveTickets.setText("Automatically open reservation in Firefox");
    JCBAutoReserveTickets.setToolTipText("<html>\nMake sure that you ONLY have Firefox<br>\nopen if you're right there working with it.<br>\nOtherwise, make sure it's COMPLETELY<br>\nclosed!<br>\n<i>The reason for this is:</i><br>\nIf Firefox doesn't close automatically<br>\n(if it prompts you if you really want to close<br>\nX tabs), the program will reserve tickets BUT<br>\nwill wait until Firefox is closed to open the PAX<br>\nregistration page.<br>\n<i>Also important to note:</i><br>\nFirefox should be set to your default web<br>\nbrowser. If not, you will have to manually<br>\nopen firefox (tickets will still be reserved<br>\nregardless of which browser is your default)\n</html>");
    JCBAutoReserveTickets.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JCBAutoReserveTicketsActionPerformed(evt);
      }
    });

    jLabel6.setText("Firefox Cookies Directory Not Set");

    jButton2.setText("Change Cookies Directory");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton2))
    );

    JCBEvent.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "PAX Prime", "PAX South", "PAX Aus" }));
    JCBEvent.setNextFocusableComponent(JCBCarrier);

    JCBListenOnPort.setText("Listen on Port 9243");

    jCheckBox1.setSelected(true);
    jCheckBox1.setText("Automatically reserve tickets");
    jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jCheckBox1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel4)
              .addComponent(jLabel3)
              .addComponent(jLabel2))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(JTFCellnum, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
              .addComponent(JTFUsername)
              .addComponent(JPFPassword))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(JCBCarrier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(JCBEvent, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jCheckBox1)
              .addComponent(JCBListenOnPort)
              .addComponent(JCBAutoReserveTickets))
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(JTFUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(JCBEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(JPFPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(JTFCellnum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(JCBCarrier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel5)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jCheckBox1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(JCBAutoReserveTickets)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(JCBListenOnPort)
        .addGap(18, 18, 18)
        .addComponent(jButton1)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // TODO add your handling code here:
    Email.setUsername(JTFUsername.getText());
    Email.setPassword(new String(JPFPassword.getPassword()));
    String text = JTFCellnum.getText();
    if (text.contains(";")) {
      Email.setCellList(text, JCBCarrier.getSelectedItem().toString());
    } else if (text.length() > 4) {
      Email.setCellNum(text, JCBCarrier.getSelectedItem().toString());
    }
    ShowclixScanner.setRefreshTime(jSlider1.getValue());
    ShowclixScanner.setReserveTickets(jCheckBox1.isSelected());
    if (ShowclixScanner.shouldReserveTickets()) {
      Browser.setKillFirefox(JCBAutoReserveTickets.isSelected());
      if (JCBListenOnPort.isSelected()) {
        ShowclixScanner.startNetworkConnection();
      }
    } else {
      Browser.setKillFirefox(false);
    }
    int showclixID;
    if (JCBEvent.getSelectedItem().toString().equals("PAX Aus")) {
      showclixID = ShowclixScanner.AUS_SHOWCLIX_ID;
    } else if (JCBEvent.getSelectedItem().toString().equals("PAX South")) {
      showclixID = ShowclixScanner.SOUTH_SHOWCLIX_ID;
    } else {
      showclixID = ShowclixScanner.PRIME_SHOWCLIX_ID;
    }
    Browser.setShowclixLink(showclixID);
    ShowclixScanner.setShowclixURL(showclixID);
    dispose();
  }//GEN-LAST:event_jButton1ActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    // TODO add your handling code here:
    chooser.setCurrentDirectory(new java.io.File(System.getenv("APPDATA")));
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("All Files", "sqlite"));
    final String reqSub = "Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles";
    chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
      @Override // C:\Users\SunnyBat\AppData\Roaming
      public boolean accept(File f) {
        String name = f.getName().toLowerCase();
        String userSub;
        try {
          userSub = f.getAbsolutePath().substring(f.getAbsolutePath().toLowerCase().indexOf("users", 0));
          if (userSub.length() < 8) {
            userSub = "C:\\Users\\GoodName32481234";
          }
          userSub += "\\";
        } catch (Exception e) {
          try {
            if (f.getAbsolutePath().endsWith(":\\")) {
              return true;
            }
          } catch (Exception ef) {
          }
          userSub = "Not here, Unfortunately\\";
        }
        if (name.length() < 2) {
          return false;
        } else if (name.equals("users")
            || userSub.endsWith(System.getProperty("user.name") + "\\")
            || name.equals("appdata")
            || name.equals("roaming")
            || name.equals("mozilla")
            || name.equals("firefox")
            || name.equals("profiles")
            || name.equals("cookies.sqlite")) {
          return true;
        } else if (userSub.contains("\\Firefox\\Profiles")) {
          if (userSub.substring(0, userSub.lastIndexOf("\\") - 1).substring(0, userSub.substring(0, userSub.lastIndexOf("\\") - 1).lastIndexOf("\\")).equals(reqSub)) {
            return true;
          }
        }
        return false;
      }

      @Override
      public String getDescription() {
        return "Find For Me";
      }
    });
    chooser.setFileHidingEnabled(false);
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.setDialogTitle("Select Firefox Cookie Database");
    chooser.setVisible(true);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      DatabaseManager.setDatabaseDirectory(chooser.getCurrentDirectory().getAbsolutePath());
      setCookieDirText(DatabaseManager.getDatabaseDirectory());
    } else {
      System.out.println("No Selection ");
    }
    if (!DatabaseManager.isValidDatabase()) {
      jButton1.setEnabled(false);
    } else {
      jButton1.setEnabled(true);
    }
  }//GEN-LAST:event_jButton2ActionPerformed

  private void JCBAutoReserveTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JCBAutoReserveTicketsActionPerformed
    // TODO add your handling code here:
    jPanel1.setVisible(JCBAutoReserveTickets.isSelected());
    if (jPanel1.isVisible()) {
      try {
        File cookiesFile;
        File profileFolder = new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles");
        if (profileFolder.isDirectory()) {
          if (profileFolder.list().length == 1) {
            cookiesFile = new File("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\" + profileFolder.list()[0] + "\\cookies.sqlite");
            if (cookiesFile.exists()) {
              DatabaseManager.setDatabaseDirectory(cookiesFile.getParent());
              setCookieDirText(DatabaseManager.getDatabaseDirectory());
              openedCookiesPrompt = true;
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (!openedCookiesPrompt) {
        pack();
        openSelectCookiePrompt();
        openedCookiesPrompt = true;
      }
      if (!DatabaseManager.isValidDatabase()) {
        jButton1.setEnabled(false);
      }
    } else {
      jButton1.setEnabled(true);
    }
    pack();
  }//GEN-LAST:event_JCBAutoReserveTicketsActionPerformed

  private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
    // TODO add your handling code here:
    if (jCheckBox1.isSelected()) {
      JCBAutoReserveTickets.setEnabled(true);
      JCBListenOnPort.setEnabled(true);
    } else {
      JCBAutoReserveTickets.setSelected(false);
      JCBAutoReserveTickets.setEnabled(false);
      JCBAutoReserveTicketsActionPerformed(null);
      JCBListenOnPort.setSelected(false);
      JCBListenOnPort.setEnabled(false);
    }
  }//GEN-LAST:event_jCheckBox1ActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JCheckBox JCBAutoReserveTickets;
  private javax.swing.JComboBox JCBCarrier;
  private javax.swing.JComboBox JCBEvent;
  private javax.swing.JCheckBox JCBListenOnPort;
  private javax.swing.JPasswordField JPFPassword;
  private javax.swing.JTextField JTFCellnum;
  private javax.swing.JTextField JTFUsername;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JCheckBox jCheckBox1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JSlider jSlider1;
  // End of variables declaration//GEN-END:variables
}
