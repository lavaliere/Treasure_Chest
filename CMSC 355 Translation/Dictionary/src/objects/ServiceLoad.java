package objects;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
/*************************************************
 * ServiceLoad class
 * Description of Class ..........................
 * This class is was made to fulfill the requirements
 * of the Project 4 service broker. 
 * 
 * This class imports data from the service.txt file
 * and stores the stub numbers and service names
 * into two String arrays, to be placed in a
 * HashMap that the ServiceBroker searches to find
 * the name of the service it needs to pull.
 * 
*************************************************/
public class ServiceLoad {
	String[] lang;
	String[] stub;
	HashMap<String, String> hm = new HashMap<String, String>();
	
	public ServiceLoad(){
		importService();
		toHash();
	}
	
	public String getLoader(String stubNo){
		JOptionPane.showMessageDialog(new JFrame(), "[Dictionary]SL: Getting loader for " + stubNo);
		return hm.get(stubNo);
	}
	
	private void toHash(){
		for(int i = 0; i < lang.length; i++){
			//Store stub Number as key and LanguageLoader as key value
			hm.put(stub[i], lang[i]);
		}
	}
	
	private void importService(){
	String service = "service.txt";
	ArrayList<String> alLang = new ArrayList<String>();
	ArrayList<String> alStub = new ArrayList<String>();
	
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(service));
			String line;
			String[] split;
			
			try {
				line = br.readLine();

				while( line != null){
					split = new String[2];
					split = line.split(" ");
					alStub.add(split[0]); //Stub Number

					alLang.add(split[1]);	//Service name

					//move pointer to next line
					line = br.readLine();
				}//while
				
				//close bufferedReader				
				br.close();
				
			} catch (IOException e) {
				JOptionPane.showMessageDialog(new JFrame(), "[Translator]SL: Problems reading" + service + "!");
				e.printStackTrace();			
			}
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(new JFrame(), "[Translator]SL: " + service + " not found!");
			e1.printStackTrace();
		}	 		
		stub = new String[alStub.size()];
		lang = new String[alLang.size()];
		
		for(int i= 0; i < lang.length; i++){
			stub[i] = alStub.get(i); //move Stub Number to array
			lang[i] = alLang.get(i); //move Stub Name to array
		}
		
	}

}
