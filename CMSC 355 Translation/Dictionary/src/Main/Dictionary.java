package Main;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import objects.Language;
import objects.ClientHandler;
import objects.TranslatorThread;
/*****************************************************
 * 					Dictionary.java
 * @author Tracy Kennedy
 * Description of module..............................
 * 
 * This module is the main class for the Dictionary 
 * app. This module will only be practially accessed
 * via the Translator app. 
 * 
 * This app represents the "server" in a client/server
 * relationship.
 * 
 * For every request received over the socket, the 
 * Dictionary creates a new thread and sends it to
 * the Client Handler.
 * 
 * ***************************************************
 * Input: Two arguments - English word to be 
 * 		translated and language to translate the word
 * 		to
 * Output: Writes translation to System.out.println()
 * ***************************************************
 * 			Maintenance Log
 * ***************************************************
 *		 *3/23 - Updated to fit the Sockets Project
 *				 requirements
 *		* 4/12 - Updated to accomdate Threads and 
 *				 the correct Sockets configuration 
 *		* 4/14 - Updated to accomodate the 
 *				 ServiceBroker requirements
 * 
 */
public class Dictionary {
	public static void main(String args[]) throws IOException, InterruptedException{
	
		//JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Starting connection...");

		boolean accepting = true;
		ServerSocket serverSocket = null;

		/* Here we attempt to bind the Dictionary to the port 9090
		 * and start the process of translating any input from Translator.jar.
		 */
		//JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Opening port 9090");
	
		try{
			serverSocket = new ServerSocket(9090); //open listening port
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "[Dictionary]Connection failed. \nError:\n " +e.getMessage());
			System.exit(0);
		}
		
		//JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Accepting connection...");
		
		//Spawns new threads when new connections are made
		while(accepting){
			TranslatorThread th = new TranslatorThread(serverSocket.accept());
		//	JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Connection made. Passing to client handler...");
			ClientHandler ch = new ClientHandler(th);
			ch.run();
		}
		
		serverSocket.close();
		
	}
	
}
