/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.applet;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author hildebj
 */
public class PoolStats extends JApplet {

  /**
   * Initialization method that will be called after the applet is loaded into
   * the browser.
   */
  @Override
  public void init() {
    // TODO start asynchronous download of heavy resources
    //Execute a job on the event-dispatching thread; creating this applet's GUI.
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          JLabel lbl = new JLabel("Pool");
          add(lbl);
        }
      });
    } catch (Exception e) {
      System.err.println("createGUI didn't complete successfully");
    }
  }
  // TODO overwrite start(), stop() and destroy() methods
}
