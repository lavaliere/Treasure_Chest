package Main;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import externals.TranslateGUI;

/*****************************************************
 * 					Translator.java
 * @author Tracy Kennedy
 * Description of module..............................
 * 
 * This module is the main class for the Translator 
 * app. 
 * 
 * Data between Translator and Dictionary is passed using
 * sockets. The Translator attempts to connect to
 * the Dictionary through the local computer's port 9090,
 * then reads/write data through said port.
 * 
 * The Translator now also includes a method that to
 * kill the Dictionary process on exit.
 * 
 * ***************************************************
 * Input: Selected text fields from users 
 * Output: translation.text to ./Temp
 * ***************************************************
 * 			Maintenance Log
 * ***************************************************
 * 	*3/26 - Updated to fulfill Project 2- Sockets 
 * 		requirements.
 * 
 * * 4/12 - Updated to fulfill Project 3 Threading
 * 		requirements.
 * 
 * * 4/18 - Updated to fulfill Project 4 Service
 *  	Broker requirements.
 * 
 */
public class Translator{
	static Process proc = null;
	static Socket dictSocket = null;
	
	public static void main(String args[]){
	//	PrintWriter out = null;
	//	BufferedReader in = null;
		
		//try to load dictionry before showing GUI
		try {
			//Finding current directory location 
			String path = (new File(".")).getAbsolutePath();
			int end = path.lastIndexOf("\\");
			String decodedPath = path.substring(0, end);
			File dictionary = new File(decodedPath);
			//JOptionPane.showMessageDialog(new JFrame(), "[Translator] Found dictionary at " + decodedPath + "\n Now loading...");

			ProcessBuilder pb = new ProcessBuilder(new String[]{"javaw", "-jar", "Dictionary.jar"});
				pb.directory(dictionary);
				pb.redirectErrorStream(true);
				proc = pb.start();
				//JOptionPane.showMessageDialog(new JFrame(), "[Translator] Dictionary loaded!");

			try {
				/************************************************************
				 * Attempting to connect to the Dictionary through
				 * port 9090.
				 *************************************************************/
				String computerName = "127.0.0.1";
				JOptionPane.showMessageDialog(new JFrame(), "[Translator] Creating socket for server " + computerName + " at port: 9090");
				
				dictSocket = new Socket(computerName, 9090);
				JOptionPane.showMessageDialog(new JFrame(), "[Translator] Socket created!");
	
				
				JOptionPane.showMessageDialog(new JFrame(), "[Translator] Getting output/input streams...");
				PrintWriter out = null;
				BufferedReader in = null;
				
				try{
					out = new PrintWriter(dictSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(dictSocket.getInputStream()));
				}catch(Exception e1){
					e1.printStackTrace();
					JOptionPane.showMessageDialog(new JFrame(), "[Translator]Main: Error while connecting to sockets. \nError:\n " +e1.getMessage());
				}
				
				//JOptionPane.showMessageDialog(new JFrame(), "[Translator] Success!");
			
				/*********************************************************
				/*The GUI object is created here and the GUI is called
				 * to display itself.
				******************************************************** */
				TranslateGUI gui = new TranslateGUI(out, in);
				gui.display();
			}catch(IOException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "[Translator]Main: Dictionary could not be Loaded! \nError:\n " +e.getMessage());
				System.exit(1);
			}catch(Exception e1){
				e1.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "[Translator]Main: Dictionary could not be Loaded! \nError:\n " +e1.getMessage());
				System.exit(1);
			}
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(new JFrame(), "[Translator] Error in main:\n " +e1.getMessage());
			e1.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void killProc(){
		proc.destroy();
	}
	
}
