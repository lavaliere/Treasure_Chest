import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/*************************************************
 * ClientHandler. class
 * Description of Class ..........................
 * 
 * The Client Handler starts any connections passed
 * to the SpanishLoader.jar and loads the translations
 * from the spanish.txt file to pass back to the 
 * Dictionary.jar before exiting.
*************************************************/
public class ClientHandler {
	Socket socket;
	public ClientHandler(Socket translation){
		this.socket = translation;
	}

	//Now output the results back over the stream
	public void run(){
		BufferedReader br;
		PrintWriter out;
		ArrayList<String> translations = new ArrayList<String>();
		String langFile = "spanish.txt";

		try{
			out = new PrintWriter(socket.getOutputStream(), true);
			try {
				br = new BufferedReader(new FileReader(langFile));
				String line;
				String[] split = new String[5];
				line = br.readLine();
	
				while(line!=null){
					//split along commas into String array
					split = line.split(",");

					//add each item to ArrayList for return
					for(int i = 0; i < split.length; i++){
						translations.add(split[i]);
					}
					
					//move pointer to next line
					line = br.readLine();
				}//while
				br.close();
				
				for(int i=0; i < translations.size(); i++){
					out.println("[Word]" + translations.get(i));
				}
				
				out.flush();
				out.close();
				socket.close();
				System.exit(0);

			} catch (IOException e) {
				JOptionPane.showMessageDialog(new JFrame(), "[SpanishLoader] ERROR: \n" + e.getMessage());
				out.print("[ERROR]" + e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(new JFrame(), "[SpanishLoader] Can't open output stream!");
			e.printStackTrace();
			System.exit(0);
		}
	}
}
