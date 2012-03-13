/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.applet;

import com.wizards.operations.icontrol.MemberHolder;
import com.wizards.operations.icontrol.Pool;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.omg.CORBA.ORB;

/**
 *
 * @author hildebj
 */
public class PoolStats extends JApplet implements ActionListener {
  
  private Pool pool;
  private MemberHolder[] members;
  private Button enableButton;
  private Button disableButton;
  private JFrame[] frames;
  private Checkbox[] checkboxes;
  private JLabel[] labels;
  private TextField[] textFields;

  /**
   * Initialization method that will be called after the applet is loaded into
   * the browser.
   */
  @Override
  public void init() {
    // TODO start asynchronous download of heavy resources
    //Execute a job on the event-dispatching thread; creating this applet's GUI.
    try {
      // initialize the ORB (using this applet)
      ORB orb = ORB.init( this, null );
    } catch(Exception e) {
      System.err.println(e);
    }
    
  }
  // TODO overwrite start(), stop() and destroy() methods

  @Override
  public void actionPerformed(ActionEvent e) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
