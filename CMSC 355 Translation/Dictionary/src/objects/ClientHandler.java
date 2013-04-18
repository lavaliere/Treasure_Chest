package objects;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
/*************************************************
 * ClientHandler. class
 * Description of Class ..........................
 * 
 * The Client Handler starts any threads created
 * by the Dictionary.
*************************************************/
public class ClientHandler{
	private TranslatorThread listener;
	
	public ClientHandler(TranslatorThread thread){
		this.listener = thread;
	}
	
	public void run(){
		listener.start();
	}

}