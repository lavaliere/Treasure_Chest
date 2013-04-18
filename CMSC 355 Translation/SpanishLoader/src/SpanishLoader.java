import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
/*****************************************************
 * 					SpanishLoader.java
 * @author Tracy Kennedy
 * Description of module..............................
 * 
 * This module is called by the Dictionary's service
 * broker if it's service stub has been called by
 * the Translator.jar.
 * ***************************************************
 * 			Maintenance Log
 * ***************************************************
 *		 *4/18/ - Created to fulfill the Proj. 4 
 *				requirements.
 */
public class SpanishLoader {
	@SuppressWarnings("resource")
	public static void main(String[] args){
		try{
			//Establish a connection to ServiceBroker & print over stream confirmation
		//	JOptionPane.showMessageDialog(new JFrame(), "[SpanishLoader] SpanishLoader loaded!");
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(9092)); //open listening port
			Socket translation = null;
			
			while(true){
		//		JOptionPane.showMessageDialog(new JFrame(), "[SpanishLoader]Connecting...");
				translation = serverSocket.accept();
				ClientHandler ch = new ClientHandler(translation);
				ch.run();
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(new JFrame(), "[SpanishLoader] Cannot open socket 9092! \n" + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
		
	}
}
