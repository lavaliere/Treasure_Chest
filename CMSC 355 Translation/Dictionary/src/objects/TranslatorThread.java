package objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Main.Dictionary;
/*************************************************
 * TranslatorThread. class
 * Description of Class ..........................
 * This class is was made to fulfill the requirements
 * of the Project 3 multi-threading assignment. This
 * class extends the Thread interface and allows for
 * multiple translation requests to be processed by 
 * the Dictionary.
 * 
*************************************************/
public class TranslatorThread extends Thread{
	private Socket socket = null;
	final String[] wordList = new String[]{
			"pig", "dog", "cat", "mouse", "house",
			 "runs", "jumps","flies", "eats", "digs",
			 "he", "she", "it", "we", "them" }; //static list of translated words
	
	public TranslatorThread(Socket socket){
		super("TranslatorThread");
		this.socket = socket;
	}
	
	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		try{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(Exception e1){
			e1.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Thread: Streams could not be opened. \nError:\n " +e1.getMessage());
		}
		
		//write loop
		while(!out.checkError()){
			translate(out, in);
		}
			
		close(out, in);
	}
	
	private void translate(PrintWriter out, BufferedReader in){
		String request = "";
		String language = "";
		
		try {
			request = in.readLine(); //Get word to be translated
			language = in.readLine(); //Get service stub number
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new JFrame(), "[Dictionary]Thread: Error while reading input stream! \n" + e.getMessage());
			e.printStackTrace();
			close(out, in);
			System.exit(0);
		}
		ArrayList<String> alEnglish = new ArrayList<String>();
		
		//build ArrayList of translations
		for(int i = 0; i < wordList.length; i++){
			alEnglish.add(wordList[i]);
		}

		//send translations to Language class for translation
		Language lang = new Language(alEnglish, loadLang(language));
		String result = lang.translate(request);
		
		//JOptionPane.showMessageDialog(new JFrame(), "[Dictionary]Thread: Result is " + result);
		out.println(result);
		
	}

	private boolean close(PrintWriter out, BufferedReader in){
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Thread: Error while closing streams: \n" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		
		this.setDaemon(true);
		JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Thread: Thread is complete.");
		return true;
	}		
	
	private static ArrayList<String> loadLang(String lang){
	//	JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Sending request for " + lang);
		ServiceBroker sb = new ServiceBroker(lang);
		return sb.getTranslation();
	}
	

}
