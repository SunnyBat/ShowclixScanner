/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package showclixscanner.gui;

import showclixscanner.Browser;
import showclixscanner.ShowclixScanner;

/**
 *
 * @author SunnyBat
 */
public class PatchNotes extends javax.swing.JFrame {

  private Update updateFrom;

  /** Creates new form patchNotes.
   *
   * @param uF The Update object that this patchNotes is attached to
   */
  public PatchNotes(Update uF) {
    initComponents();
    customComponents();
    updateFrom = uF;
  }

  private void customComponents() {
    loadNotesOnNewThread();
    setTitle("Patch Notes for Version " + ShowclixScanner.VERSION);
  }

  public void setWindowTitle(final String title) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setTitle(title);
      }
    });
  }

  public void setMainText(final String text) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        jTextArea1.setText(text);
      }
    });
  }

  @Override
  public void dispose() {
    if (updateFrom != null) {
      updateFrom.setPatchNotesButtonState(true);
    }
    super.dispose();
  }

  public void loadNotesOnNewThread() {
    ShowclixScanner.startBackgroundThread(new Runnable() {
      @Override
      public void run() {
        String versionNotes = Browser.getVersionNotes(ShowclixScanner.VERSION);
        if (!versionNotes.contains("~~~")) {
          jTextArea1.setText("Unknown updates? Showing full notes..." + System.getProperty("line.separator", "\n") + Browser.getVersionNotes());
        } else {
          jTextArea1.setText(versionNotes);
        }
      }
    });
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jTextArea1 = new javax.swing.JTextArea();
    jButton1 = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Patch Notes");
    setMinimumSize(this.getPreferredSize());

    jLabel1.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Recent Patch Notes");

    jTextArea1.setEditable(false);
    jTextArea1.setColumns(20);
    jTextArea1.setLineWrap(true);
    jTextArea1.setRows(5);
    jTextArea1.setText("Loading patch notes, please wait...");
    jScrollPane1.setViewportView(jTextArea1);

    jButton1.setText("Close Patch Notes Window");
    jButton1.setMinimumSize(this.getPreferredSize());
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
          .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // TODO add your handling code here:
    dispose();
  }//GEN-LAST:event_jButton1ActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTextArea jTextArea1;
  // End of variables declaration//GEN-END:variables
}
