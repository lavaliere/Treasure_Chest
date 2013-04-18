package externals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
/*************************************************
 * ServiceLoad class
 * Description of Class ..........................
 * This class is where the service stub numbers/names
 * from the service.txt file are pulled and
 * store into String arrays.
 * 
 *************************************************/
public class ServiceLoad {
	String[] stub; //array of stub Numbers
	String[] lang; //array of Stub Service Names
	
	public ServiceLoad(){
		importService();
	}
	
	//Returns the service stub numbers
	public String[] loadStubs(){
		return stub;
	}
	
	//Loads the substring of the service stubs for display as the language
	public String[] loadLang() {
		for(int i =0; i < stub.length; i++){
			int e = lang[i].indexOf("Loader");
			if(e>0){
				lang[i] = lang[i].substring(0, e);
			}
		}
		return lang;
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
