package externals;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Main.Translator;


/*************************************************
 * WindowHandler class
 * Description of Class ..........................
 * This class is allows the user to decide whether 
 * they want to kill the Server on exit or not.
 * 
 * If so, this Class calls the main Class's 
 * killProc method to end the Dictionary's Java
 * process.
 * 
 *************************************************/

public class WindowHandler implements WindowListener {

	public void windowClosing(WindowEvent arg0) {
		JOptionPane pane =new JOptionPane("[Translator] Would you like to close the server?");
	    Object[] options = new String[] { "Yes", "No" };
	   
	    pane.setOptions(options);
	    JDialog dialog = pane.createDialog(new JFrame(), "Server Prompt");
	    
	    dialog.setVisible(true);
	    Object obj = pane.getValue(); 
	   
	    int result = -1;
	    for (int k = 0; k < options.length; k++)
	      if (options[k].equals(obj))
	        result = k;

	    if(result == 1){
	    	Translator.killProc();
	    }

		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
