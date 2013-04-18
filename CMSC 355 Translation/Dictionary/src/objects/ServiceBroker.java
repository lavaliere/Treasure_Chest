package objects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*************************************************
 * ServiceBroker class
 * Description of Class ..........................
 * This class is was made to fulfill the requirements
 * of the Project 4 Service Broker Assignment. 
 * 
 * This class handles requests sent over a socket 
 * and loads the appropriate [Language]Loader.jar
 * file to return a list of translations.
 * 
*************************************************/
public class ServiceBroker {
	String language = "";
	String loader = "";
	
	public ServiceBroker(String lang) {
		this.language = lang; //Takes the stub number passed over the stream
	}

	@SuppressWarnings("resource")
	public ArrayList<String> getTranslation() {
		ArrayList<String> translations = new ArrayList<String>();
		ServiceLoad sl = new ServiceLoad(); //Loads list of services and stubs
		loader = sl.getLoader(language); //Search HashMap of services for right language loader
	
		//Now load up the loader, establish connection over a socket
		Process proc = null;
		Socket loadSock = null;
		PrintWriter logger = null;

		try {
			//Finding current directory location 
			String path = (new File(".")).getAbsolutePath();
			int end = path.lastIndexOf("\\");
			String decodedPath = path.substring(0, end);
			File LangLoad = new File(decodedPath);
			
			ProcessBuilder pb = new ProcessBuilder(new String[]{"javaw", "-jar", loader});
				pb.directory(LangLoad);
				pb.redirectErrorStream(true);
				proc = pb.start();
				
			File log = new File(decodedPath + "\\temp\\log.txt");
			if(!log.exists()){
				try{
					log.getParentFile().mkdirs();
					log.createNewFile();
				}catch(Exception e){
					JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] SB: ERROR: Could not create directory! \n" + e.getMessage());
					e.printStackTrace();
				}
			}
			logger = new PrintWriter(new BufferedWriter(new FileWriter(log, true)));
			
		//Now read any messages passed over the stream, print messages to a log file
			try {
				String computerName = "127.0.0.1";
				loadSock = new Socket(computerName, 9092);
				BufferedReader in = null;
				logger.println("[LOG] "+ System.currentTimeMillis() + ": " + " Opening connection at " + computerName + " and socket port: 9092...");

				
				try{
					in = new BufferedReader(new InputStreamReader(loadSock.getInputStream()));
				}catch(Exception e1){
					e1.printStackTrace();
					logger.println("[ERROR] "+ System.currentTimeMillis() + ": " + "ERROR: " + e1.getMessage());

					JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Error while getting input from " + loader + ". \nError:\n " +e1.getMessage());
				}
				
				logger.println("[LOG] "+ System.currentTimeMillis() + ": " +  loader + "Called");
				
				int received = 0;
				
				while(received < 15){ //Keep reading words
					String line = in.readLine();
					if(line.startsWith("[Word]")){
						logger.println("[LOG] "+ System.currentTimeMillis() + ": " + " Received line: " + line);

						int e = line.indexOf("]");
						translations.add(line.substring(e+1, line.length()));
						received++;
					}else{
						logger.println("[LOG] "+ System.currentTimeMillis() + ": " +  line);
					}
				}
				
				logger.println("[LOG] "+ System.currentTimeMillis() + ": " + " Connection closing...");
				loadSock.close();
			}catch(IOException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] " + loader + " could not be Loaded! \nError:\n " +e.getMessage());
				System.exit(1);
			}catch(Exception e1){
				e1.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] " + loader + " could not be Loaded! \nError:\n " +e1.getMessage());
				System.exit(1);
			}
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(new JFrame(), "[Dictionary] Error in ServiceBroker:\n " +e1.getMessage());
			e1.printStackTrace();
		}
		return translations;
	}

}
